package com.nestedinfinity.mod.items.algae;
import com.nestedinfinity.mod.NestedInfinity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Registers one petri dish for every compound culture allowed by the color wheel:
 * any two strains on a dish are at least {@link NIAlgae#MIN_SEPARATION} steps apart,
 * so a dish hosts between one and four algae (12 + 42 + 40 + 3 = 97 dishes).
 *
 * <p>Item ids and display names concatenate the color roots of the strains, the last
 * one keeping the full taxon: red + cyan -&gt; {@code petri_erythrocyanophyta} /
 * "Erythrocyanophyta Petri Dish". Mirrored by {@code tools/gen_algae_assets.py}.
 */
public final class NIPetriDishes {
    public record PetriDish(List<NIAlgae> algae, DeferredItem<PetriDishItem> item) {}

    public static final List<PetriDish> ALL = buildAll();

    /** The three dishes carrying the maximum of four strains ({0,3,6,9} and rotations). */
    public static final List<PetriDish> FOUR_STRAIN = ALL.stream().filter(d -> d.algae().size() == 4).toList();

    /**
     * Wild isolates: the two-strain dishes whose colors sit 3 or 5 steps apart on the
     * wheel (12 + 12 = 24 dishes) — the only cultures that soil plating can surface.
     */
    public static final List<PetriDish> WILD_ISOLATES = ALL.stream()
            .filter(d -> d.algae().size() == 2)
            .filter(d -> {
                int distance = wheelDistance(d.algae().get(0), d.algae().get(1));
                return distance == 3 || distance == 5;
            })
            .toList();

    /** Circular distance between two strains on the color wheel. */
    public static int wheelDistance(NIAlgae a, NIAlgae b) {
        int diff = Math.abs(a.ordinal() - b.ordinal());
        return Math.min(diff, NIAlgae.WHEEL_SIZE - diff);
    }

    private static List<PetriDish> buildAll() {
        List<PetriDish> dishes = new ArrayList<>();
        int wheel = NIAlgae.WHEEL_SIZE;
        for (int mask = 1; mask < (1 << wheel); mask++) {
            if (!spaced(mask, wheel)) {
                continue;
            }
            List<NIAlgae> members = new ArrayList<>();
            for (NIAlgae alga : NIAlgae.values()) {
                if ((mask & (1 << alga.ordinal())) != 0) {
                    members.add(alga);
                }
            }
            String id = "petri_" + word(members);
            DeferredItem<PetriDishItem> item = NestedInfinity.ITEMS.register(id,
                    () -> new PetriDishItem(members, new Item.Properties()));
            dishes.add(new PetriDish(List.copyOf(members), item));
        }
        return List.copyOf(dishes);
    }

    /** True when every pair of set bits is at least {@link NIAlgae#MIN_SEPARATION} apart on the wheel. */
    private static boolean spaced(int mask, int wheel) {
        for (int i = 0; i < wheel; i++) {
            if ((mask & (1 << i)) == 0) {
                continue;
            }
            for (int j = i + 1; j < wheel; j++) {
                if ((mask & (1 << j)) == 0) {
                    continue;
                }
                int distance = Math.min(j - i, wheel - (j - i));
                if (distance < NIAlgae.MIN_SEPARATION) {
                    return false;
                }
            }
        }
        return true;
    }

    /** erythro + cyano... -&gt; "erythrocyanophyta" (the last strain keeps the full taxon). */
    public static String word(List<NIAlgae> members) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < members.size() - 1; i++) {
            builder.append(members.get(i).root());
        }
        return builder.append(members.get(members.size() - 1).taxon()).toString();
    }

    public static void init() {}

    private NIPetriDishes() {}
}
