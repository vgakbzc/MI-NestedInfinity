package com.nestedinfinity.mod.microverse;

import com.nestedinfinity.mod.NestedInfinity;
import com.nestedinfinity.mod.items.NIItems;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

/**
 * EMI integration for the microverse program: a graphical recipe page per
 * universe matter tier (what the projector consumes and yields) and per
 * singularity (what the catalyzer condenses it back into), plus an info
 * page on the heart.
 *
 * <p>EMI discovers this class through the {@link EmiEntrypoint} annotation
 * and is its only referencer, so the mod runs fine without EMI installed.
 */
@EmiEntrypoint
public final class MicroverseEmiPlugin implements EmiPlugin {
    private static final int TEXT_COLOR = 0x3F3F3F;

    public static final EmiRecipeCategory PROJECTOR = new EmiRecipeCategory(
            id("microverse_projector"), EmiStack.of(MicroverseBlocks.MICROVERSE_PROJECTOR.get()));
    public static final EmiRecipeCategory CATALYZER = new EmiRecipeCategory(
            id("singularity_catalyzer"), EmiStack.of(MicroverseBlocks.SINGULARITY_CATALYZER.get()));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(PROJECTOR);
        registry.addCategory(CATALYZER);
        registry.addWorkstation(PROJECTOR, EmiStack.of(MicroverseBlocks.MICROVERSE_PROJECTOR.get()));
        registry.addWorkstation(CATALYZER, EmiStack.of(MicroverseBlocks.SINGULARITY_CATALYZER.get()));
        for (int tier = 1; tier <= MicroverseItems.MATTERS.size(); tier++) {
            registry.addRecipe(new ProjectorRecipe(tier));
        }
        for (int i = 0; i < MicroverseItems.SINGULARITIES.size(); i++) {
            registry.addRecipe(new CatalyzerRecipe(i));
        }
        registry.addRecipe(new EmiInfoRecipe(
                List.of(EmiStack.of(MicroverseItems.HEART_OF_A_NONEXISTENT_WORLD.get())),
                List.of(Component.translatable("emi.mi_nested_infinity.heart.line1"),
                        Component.translatable("emi.mi_nested_infinity.heart.line2")),
                id("heart_of_a_nonexistent_world")));
    }

    /** The projector multiblock: heart + battery + 4 TDUs + 12 singularities -> matter. */
    private static final class ProjectorRecipe implements EmiRecipe {
        private static final int WIDTH = 162;
        private static final int HEIGHT = 114;

        private final int tier;
        private final List<EmiIngredient> inputs;
        private final EmiStack output;

        ProjectorRecipe(int tier) {
            this.tier = tier;
            List<EmiIngredient> in = new ArrayList<>();
            in.add(EmiStack.of(MicroverseItems.HEART_OF_A_NONEXISTENT_WORLD.get()));
            in.add(EmiStack.of(NIItems.TRANSURANIC_BATTERY.get(), 64));
            in.add(EmiStack.of(MicroverseBlocks.TDUS.get(tier - 1).get(), 4)); // the ring holds four
            for (var kind : MicroverseItems.SINGULARITIES) {
                in.add(EmiStack.of(kind.item().get()));
            }
            this.inputs = List.copyOf(in);
            int baseTicks = MicroverseProjectorBlockEntity.baseTicks(tier);
            int perRun = tier * tier + baseTicks / MicroverseProjectorBlockEntity.ITEM_INTERVAL_TICKS;
            this.output = EmiStack.of(MicroverseItems.MATTERS.get(tier - 1).get(), perRun);
        }

        @Override
        public EmiRecipeCategory getCategory() {
            return PROJECTOR;
        }

        @Override
        public ResourceLocation getId() {
            return id("microverse_projector/t" + tier);
        }

        @Override
        public List<EmiIngredient> getInputs() {
            return inputs;
        }

        @Override
        public List<EmiStack> getOutputs() {
            return List.of(output);
        }

        @Override
        public int getDisplayWidth() {
            return WIDTH;
        }

        @Override
        public int getDisplayHeight() {
            return HEIGHT;
        }

        @Override
        public boolean supportsRecipeTree() {
            return false; // a multiblock ceremony, not a craftable recipe
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            int x0 = (WIDTH - 8 * 18) / 2;
            // row 1: the heart, the battery, the four time dilation units, five coreflames
            for (int i = 0; i < 8; i++) {
                widgets.addSlot(inputs.get(i), x0 + i * 18, 0);
            }
            // row 2: the remaining seven coreflames
            for (int i = 8; i < inputs.size(); i++) {
                widgets.addSlot(inputs.get(i), x0 + (i - 8) * 18, 22);
            }
            widgets.addFillingArrow(WIDTH / 2 - 12, 46, 20000);
            widgets.addSlot(output, WIDTH / 2 - 9, 66);
            int seconds = MicroverseProjectorBlockEntity.baseTicks(tier) / 20;
            widgets.addText(Component.translatable("emi.mi_nested_infinity.projector.time", seconds),
                    6, 88, TEXT_COLOR, false);
            widgets.addText(Component.translatable("emi.mi_nested_infinity.projector.rate", tier * tier),
                    6, 98, TEXT_COLOR, false);
            widgets.addText(Component.translatable("emi.mi_nested_infinity.projector.energy"),
                    6, 108, TEXT_COLOR, false);
        }
    }

    /** The catalyzer: a stack of the kind's catalyst + its ritual -> singularity. */
    private static final class CatalyzerRecipe implements EmiRecipe {
        private static final int WIDTH = 168;
        private static final int HEIGHT = 71;

        private final int index;
        private final EmiStack catalyst;
        private final EmiStack output;

        CatalyzerRecipe(int index) {
            this.index = index;
            var item = SingularityCatalyzerBlockEntity.CATALYSTS.get(index);
            long amount = Math.min(SingularityCatalyzerBlockEntity.CRAFT_AMOUNT,
                    new net.minecraft.world.item.ItemStack(item).getMaxStackSize());
            this.catalyst = EmiStack.of(item, amount);
            this.output = EmiStack.of(MicroverseItems.SINGULARITIES.get(index).item().get());
        }

        /** A few graphical hints of what the ritual involves (icons only). */
        private static List<EmiIngredient> ritualIcons(int index) {
            return switch (index) {
                case 0 -> List.of(EmiStack.of(Fluids.LAVA, 1000), EmiStack.of(Fluids.LAVA, 1000)); // two faces
                case 1 -> List.of(EmiIngredient.of(ItemTags.LOGS), EmiIngredient.of(ItemTags.LEAVES));
                case 2 -> List.of(EmiStack.of(Items.BONE)); // a death nearby
                case 3 -> List.of(EmiStack.of(Items.CANDLE), EmiStack.of(Items.TORCH),
                        EmiStack.of(Items.CAMPFIRE)); // the snuffable flames
                case 4 -> List.of(EmiStack.of(Items.GOLD_BLOCK)); // spirited away unseen
                case 5 -> List.of(EmiStack.of(Items.BEDROCK)); // the world floor
                case 6 -> List.of(EmiStack.of(Items.SPYGLASS)); // open sky at the top
                case 7 -> List.of(EmiStack.of(Items.SEA_LANTERN)); // light >= 13
                case 8 -> List.of(EmiStack.of(Items.BOW)); // struck by an arrow
                case 9 -> List.of(EmiIngredient.of(java.util.Arrays.asList( // four different kinds
                        EmiStack.of(Items.STONE), EmiStack.of(Items.GRANITE), EmiStack.of(Items.DIORITE),
                        EmiStack.of(Items.ANDESITE), EmiStack.of(Items.DEEPSLATE), EmiStack.of(Items.TUFF))));
                case 10 -> List.of(EmiIngredient.of(ItemTags.RAILS));
                case 11 -> List.of(EmiIngredient.of(ItemTags.DOORS));
                default -> List.of();
            };
        }

        @Override
        public EmiRecipeCategory getCategory() {
            return CATALYZER;
        }

        @Override
        public ResourceLocation getId() {
            return id("singularity_catalyzer/" + MicroverseItems.SINGULARITIES.get(index).key());
        }

        @Override
        public List<EmiIngredient> getInputs() {
            return List.of(catalyst);
        }

        @Override
        public List<EmiStack> getOutputs() {
            return List.of(output);
        }

        @Override
        public int getDisplayWidth() {
            return WIDTH;
        }

        @Override
        public int getDisplayHeight() {
            return HEIGHT;
        }

        @Override
        public boolean supportsRecipeTree() {
            return false; // the ritual is the real ingredient
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            widgets.addSlot(catalyst, 52, 0);
            widgets.addFillingArrow(74, 1, 20000);
            widgets.addSlot(output, 98, 0);
            List<EmiIngredient> icons = ritualIcons(index);
            for (int i = 0; i < icons.size(); i++) {
                widgets.addSlot(icons.get(i), 6 + i * 18, 22);
            }
            widgets.addText(Component.translatable("emi.mi_nested_infinity.catalyzer.time"),
                    6, 44, TEXT_COLOR, false);
            widgets.addText(Component.translatable("emi.mi_nested_infinity.catalyzer.condition."
                    + MicroverseItems.SINGULARITIES.get(index).key()), 6, 53, TEXT_COLOR, false);
            widgets.addText(Component.translatable("emi.mi_nested_infinity.catalyzer."
                    + (eventRitual(index) ? "once" : "state")), 6, 62, TEXT_COLOR, false);
        }

        private static boolean eventRitual(int index) {
            return index == SingularityCatalyzerBlockEntity.KIND_SHADOW
                    || index == SingularityCatalyzerBlockEntity.KIND_JUSTICE
                    || index == SingularityCatalyzerBlockEntity.KIND_WHIMSY
                    || index == SingularityCatalyzerBlockEntity.KIND_FURY;
        }
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NestedInfinity.MODID, path);
    }

    public MicroverseEmiPlugin() {}
}
