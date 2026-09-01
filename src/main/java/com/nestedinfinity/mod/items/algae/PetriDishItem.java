package com.nestedinfinity.mod.items.algae;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * A petri dish hosting a fixed compound culture of one or more color-wheel algae.
 * The tooltip lists the strains, each tinted with its fluid color.
 */
public class PetriDishItem extends Item {
    private final List<NIAlgae> algae;

    public PetriDishItem(List<NIAlgae> algae, Properties properties) {
        super(properties);
        this.algae = List.copyOf(algae);
    }

    public List<NIAlgae> algae() {
        return algae;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        for (NIAlgae alga : algae) {
            tooltip.add(Component.translatable(alga.fluidKey())
                    .withStyle(style -> style.withColor(TextColor.fromRgb(alga.fluid().tint & 0xFFFFFF))));
        }
    }
}
