package com.nestedinfinity.mod.logic;

import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import com.nestedinfinity.mod.blocks.NIMachines;
import com.nestedinfinity.mod.items.algae.PetriDishItem;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Output behavior of the algae cultivator: each craft draws TWO product dishes
 * independently from the recipe's probability pool (so every dish type's expected
 * yield is twice its listed probability, and a run always produces exactly two
 * dishes — the two culture slots of the original design). The machine only runs
 * when EVERY possible pair of draws has space; otherwise it stays idle (standard
 * MI "output full" behavior). This replaces MI's independent per-entry rolls,
 * which can produce nothing or too much. The recipe JSON stays a normal MI
 * probability recipe, so recipe viewers still show the pool.
 *
 * <p>Deterministic single-entry recipes (an unconflicted XOR compound, amount 2)
 * draw the entry twice at half amount — again exactly two dishes.
 *
 * <p>Dish-pool recipes in OTHER machines (the chemical reactor's wild isolation)
 * draw at most ONE dish instead: a single roll below the pool's total listed
 * probability yields one dish picked by weight — never several at once.
 *
 * <p>Repeat penalty: the machine remembers the petri dishes used by its last
 * {@link #HISTORY_SIZE} crafts. A new craft whose dishes already appear n times
 * in that history takes 4^n times longer, and n &gt; 4 adds another 10x on top —
 * rotating dish pairs is therefore strongly encouraged. Applies to every cultivator
 * recipe that consumes a dish, including the mutagenic bombardment.
 */
public final class AlgaeCultivatorLogic {
    /** How many past crafts the per-machine dish history keeps. */
    public static final int HISTORY_SIZE = 20;

    /** Per-crafter rolling dish history; weak so chunk unload can collect it. */
    private static final Map<CrafterComponent, Deque<Item>> DISH_HISTORY = new WeakHashMap<>();

    /** Penalty multiplier staged by the input-consumption hook for the energy hook. */
    private static final ThreadLocal<Long> PENDING_PENALTY = new ThreadLocal<>();

    /**
     * Called when a cultivator recipe actually consumes its item inputs: resolves the
     * dishes used, folds them into the history, and stages the time penalty.
     */
    public static void onInputsConsumed(CrafterComponent crafter, MachineRecipe recipe,
            CrafterComponent.Inventory inventory) {
        PENDING_PENALTY.remove();
        if (recipe.getType() != NIMachines.ALGAE_CULTIVATOR) {
            return;
        }
        List<Item> dishes = resolveDishes(recipe, inventory);
        if (dishes.isEmpty()) {
            return;
        }
        Deque<Item> history = DISH_HISTORY.computeIfAbsent(crafter, k -> new ArrayDeque<>());
        int repeats = 0;
        for (Item past : history) {
            if (dishes.contains(past)) {
                repeats++;
            }
        }
        long multiplier = 1L << (2 * Math.min(repeats, 16)); // 4^repeats
        if (repeats > 4) {
            multiplier *= 10;
        }
        for (Item dish : dishes) {
            history.addLast(dish);
        }
        while (history.size() > HISTORY_SIZE) {
            history.removeFirst();
        }
        PENDING_PENALTY.set(multiplier);
    }

    /** The staged penalty for the craft that just started (1 if none); clears the stage. */
    public static long takePendingPenalty() {
        Long penalty = PENDING_PENALTY.get();
        PENDING_PENALTY.remove();
        return penalty == null ? 1 : penalty;
    }

    /**
     * The dishes a recipe consumes: concrete dish inputs resolve directly; a tag
     * input (the mutagenic bombardment's "any dish") is intersected with the dish
     * variants currently sitting in the machine's input slots.
     */
    private static List<Item> resolveDishes(MachineRecipe recipe, CrafterComponent.Inventory inventory) {
        List<Item> dishes = new ArrayList<>();
        for (MachineRecipe.ItemInput input : recipe.itemInputs) {
            List<Item> items = input.getInputItems();
            if (items.size() == 1) {
                if (items.get(0) instanceof PetriDishItem dish && !dishes.contains(dish)) {
                    dishes.add(dish);
                }
                continue;
            }
            for (ConfigurableItemStack slot : inventory.getItemInputs()) {
                Item item = slot.getVariant().getItem();
                if (item instanceof PetriDishItem dish && !dishes.contains(dish)
                        && input.matches(new ItemStack(dish))) {
                    dishes.add(dish);
                }
            }
        }
        return dishes;
    }

    /** True when every output of this cultivator recipe is a petri dish (the two-draw pool). */
    public static boolean isDishPool(MachineRecipe recipe) {
        if (recipe.itemOutputs.isEmpty()) {
            return false;
        }
        for (MachineRecipe.ItemOutput out : recipe.itemOutputs) {
            if (!(out.variant().getItem() instanceof PetriDishItem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean putTwoDraws(List<ConfigurableItemStack> outputs, MachineRecipe recipe, boolean simulate) {
        // gate: refuse to run unless every possible pair of draws currently fits
        List<MachineRecipe.ItemOutput> pool = recipe.itemOutputs;
        for (MachineRecipe.ItemOutput a : pool) {
            for (MachineRecipe.ItemOutput b : pool) {
                if (!pairFits(outputs, a, b, pool.size())) {
                    return false;
                }
            }
        }
        if (simulate) {
            return true;
        }
        MachineRecipe.ItemOutput first = weightedPick(pool);
        if (!insert(outputs, first, unit(pool.size(), first), false)) {
            return false;
        }
        MachineRecipe.ItemOutput second = weightedPick(pool);
        return insert(outputs, second, unit(pool.size(), second), false);
    }

    /**
     * Dish-pool output in any other machine (wild isolation): the pool's listed
     * probabilities are both the draw weights and the total success chance, so
     * one craft yields one dish at most. Runs only while every candidate dish
     * would individually fit in the output slots.
     */
    public static boolean putSingleDraw(List<ConfigurableItemStack> outputs, MachineRecipe recipe, boolean simulate) {
        List<MachineRecipe.ItemOutput> pool = recipe.itemOutputs;
        for (MachineRecipe.ItemOutput out : pool) {
            if (!insert(outputs, out, out.amount(), true)) {
                return false;
            }
        }
        if (simulate) {
            return true;
        }
        float total = 0;
        for (MachineRecipe.ItemOutput out : pool) {
            total += out.probability();
        }
        if (ThreadLocalRandom.current().nextFloat() >= total) {
            return true; // no culture took hold
        }
        MachineRecipe.ItemOutput pick = weightedPick(pool);
        return insert(outputs, pick, pick.amount(), false);
    }

    /**
     * Amount of a single draw: pools with several entries roll one dish each;
     * a deterministic pool carries the combined amount 2 on its single entry,
     * so one draw is half of it.
     */
    private static long unit(int poolSize, MachineRecipe.ItemOutput out) {
        return poolSize == 1 ? Math.max(1, out.amount() / 2) : out.amount();
    }

    private static MachineRecipe.ItemOutput weightedPick(List<MachineRecipe.ItemOutput> pool) {
        float total = 0;
        for (MachineRecipe.ItemOutput out : pool) {
            total += out.probability();
        }
        float roll = ThreadLocalRandom.current().nextFloat() * total;
        for (int i = 0; i < pool.size(); i++) {
            roll -= pool.get(i).probability();
            if (roll <= 0) {
                return pool.get(i);
            }
        }
        return pool.get(pool.size() - 1);
    }

    /** Can draws {@code a} and {@code b} (possibly the same entry) be inserted together? */
    private static boolean pairFits(List<ConfigurableItemStack> outputs, MachineRecipe.ItemOutput a,
            MachineRecipe.ItemOutput b, int poolSize) {
        long ua = unit(poolSize, a);
        long ub = unit(poolSize, b);
        ItemVariant va = a.variant();
        ItemVariant vb = b.variant();
        if (va.equals(vb)) {
            long free = 0;
            for (ConfigurableItemStack stack : outputs) {
                ItemVariant v = stack.getVariant();
                if (v.isBlank() || v.equals(va)) {
                    free += stack.getRemainingCapacityFor(va) - (v.isBlank() ? 0 : stack.getAmount());
                }
            }
            return free >= ua + ub;
        }
        // different dishes: capacity already holding the right variant plus blank slots;
        // the shared-blank corner case (both relying on the same single free slot) is excluded
        long freeA = 0, freeB = 0, blank = 0;
        for (ConfigurableItemStack stack : outputs) {
            ItemVariant v = stack.getVariant();
            if (v.isBlank()) {
                blank += stack.getRemainingCapacityFor(va);
            } else if (v.equals(va)) {
                freeA += stack.getRemainingCapacityFor(va) - stack.getAmount();
            } else if (v.equals(vb)) {
                freeB += stack.getRemainingCapacityFor(vb) - stack.getAmount();
            }
        }
        return freeA + blank >= ua && freeB + blank >= ub
                && (freeA >= ua || freeB >= ub || blank >= ua + ub);
    }

    private static boolean insert(List<ConfigurableItemStack> outputs, MachineRecipe.ItemOutput out, long amount, boolean simulate) {
        long remaining = amount;
        for (ConfigurableItemStack stack : outputs) {
            if (remaining <= 0) {
                return true;
            }
            boolean blank = stack.getVariant().isBlank();
            if (blank || stack.getVariant().equals(out.variant())) {
                long inStack = blank ? 0 : stack.getAmount();
                long canAdd = Math.min(remaining, stack.getRemainingCapacityFor(out.variant()) - inStack);
                if (canAdd > 0) {
                    if (!simulate) {
                        if (blank) {
                            stack.setKey(out.variant());
                        }
                        stack.setAmount(inStack + canAdd);
                    }
                    remaining -= canAdd;
                }
            }
        }
        return remaining == 0;
    }

    private AlgaeCultivatorLogic() {}
}
