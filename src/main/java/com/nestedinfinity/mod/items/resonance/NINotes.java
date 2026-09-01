package com.nestedinfinity.mod.items.resonance;

import com.nestedinfinity.mod.NestedInfinity;

import java.util.List;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * The eight tuning notes of the resonant program, one per element of the
 * quaternion group Q8 = {±1, ±i, ±j, ±k}, in the user's color order
 * 白红黄蓝绿青紫黑: white = 1, red = i, yellow = j, blue = k,
 * green = -k, cyan = -i, purple = -j, black = -1.
 *
 * <p>Complementary color pairs are inverse elements (red × cyan = white,
 * red × red = black), and the group is non-abelian (red × yellow = blue but
 * yellow × red = green); black is central, white is the identity. The
 * resonance attuner multiplies the tuning block's state with the inserted
 * note and adopts the note's color (see the block entity in
 * {@code blocks.resonance.ResonanceAttunerBlockEntity}).
 *
 * <p>This table is mirrored by {@code tools/gen_algae_assets.py}, which
 * generates the note models, textures and lang entries from the same colors.
 */
public enum NINotes {
    WHITE(1, 0, "white", 0xFFFFFF),
    RED(1, 1, "red", 0xF53D3D),
    YELLOW(1, 2, "yellow", 0xF5E94D),
    BLUE(1, 3, "blue", 0x4D6DF5),
    GREEN(-1, 3, "green", 0x4DF56D),
    CYAN(-1, 1, "cyan", 0x4DF5F5),
    PURPLE(-1, 2, "purple", 0x9A4DF5),
    BLACK(-1, 0, "black", 0x26262E);

    /** Number of notes = |Q8|; ordinal follows the user's color order. */
    public static final int GROUP_SIZE = values().length;

    /** +1 or -1 coefficient of the quaternion element. */
    public final int sign;
    /** 0 = 1, 1 = i, 2 = j, 3 = k. */
    public final int unit;
    private final String colorName;
    /** Base color for the asset generator. */
    public final int tint;
    public final DeferredItem<Item> item;

    NINotes(int sign, int unit, String colorName, int tint) {
        this.sign = sign;
        this.unit = unit;
        this.colorName = colorName;
        this.tint = tint;
        this.item = NestedInfinity.ITEMS.registerSimpleItem("note_" + colorName, new Item.Properties());
    }

    public String colorName() {
        return colorName;
    }

    /** Full item id, e.g. {@code mi_nested_infinity:note_white}. */
    public String itemId() {
        return NestedInfinity.MODID + ":note_" + colorName;
    }

    /**
     * The quaternion product {@code this × other}, computed from the
     * (sign, unit) encoding: equal nonzero units square to -1, distinct
     * nonzero units give the third unit with a + sign on the cyclic pairs
     * (i·j=k, j·k=i, k·i=j) and a − sign otherwise.
     */
    public NINotes times(NINotes other) {
        int s = sign * other.sign;
        int u;
        if (unit == 0 || other.unit == 0) {
            u = unit + other.unit;
        } else if (unit == other.unit) {
            s = -s;
            u = 0;
        } else {
            u = 6 - unit - other.unit;
            if ((unit % 3) + 1 != other.unit) {
                s = -s;
            }
        }
        final int fs = s;
        final int fu = u;
        return List.of(values()).stream()
                .filter(n -> n.sign == fs && n.unit == fu)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Q8 element for (" + fs + "," + fu + ")"));
    }

    /** The note enum whose registered item this is, or null. */
    public static NINotes byItem(Item item) {
        for (NINotes note : values()) {
            if (note.item.get() == item) {
                return note;
            }
        }
        return null;
    }

    /** All notes in color order (ordinal order). */
    public static final List<NINotes> ALL = List.of(values());

    public static void init() {
        // Q8 consistency smoke test: fails fast at load if the table is wrong.
        if (RED.times(YELLOW) != BLUE || YELLOW.times(RED) != GREEN
                || RED.times(CYAN) != WHITE || RED.times(RED) != BLACK
                || WHITE.times(BLUE) != BLUE || BLACK.times(RED) != CYAN
                || BLUE.times(GREEN) != WHITE || GREEN.times(GREEN) != BLACK) {
            throw new IllegalStateException("Q8 multiplication table is inconsistent");
        }
    }
}
