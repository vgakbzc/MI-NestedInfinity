package com.nestedinfinity.mod.datagen;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nestedinfinity.mod.NestedInfinity;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

/**
 * Recipe code generator: recipes are written in Java and emitted as JSON by
 * {@code gradlew runData} — no more hand-written files under data/.
 * <pre>
 * r.machine("assembler", 512_000, 1200)
 *     .itemIn("modern_industrialization:he_mox_fuel_rod_quad", 1)
 *     .tagIn("c:plates/iridium", 32)
 *     .itemOut(NIItems.NEUTRON_SOURCE.getId().toString(), 32)
 *     .save("naquadah/neutron_source");
 * </pre>
 * save() paths are relative to data/&lt;modid&gt;/recipe/.
 */
public final class NIRecipes {
    private final PackOutput output;
    /** Paths relative to data/ -> JSON payloads, written out in one go. */
    private final Map<String, JsonObject> pending = new LinkedHashMap<>();

    public NIRecipes(PackOutput output) {
        this.output = output;
    }

    public MachineBuilder machine(String type, int eu, int duration) {
        return new MachineBuilder(type, eu, duration);
    }

    public void shaped(String path, String result, int count, String[] pattern, Object... symbolAndIngredient) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");
        JsonArray rows = new JsonArray();
        for (String row : pattern) {
            rows.add(row);
        }
        json.add("pattern", rows);
        JsonObject key = new JsonObject();
        for (int i = 0; i < symbolAndIngredient.length; i += 2) {
            String symbol = String.valueOf((Character) symbolAndIngredient[i]);
            key.add(symbol, ingredient(symbolAndIngredient[i + 1]));
        }
        json.add("key", key);
        json.add("result", result(result, count));
        emit(NestedInfinity.MODID + "/recipe/" + path, json);
    }

    public void shapeless(String path, String result, int count, Object... ingredients) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shapeless");
        JsonArray list = new JsonArray();
        for (Object ingredient : ingredients) {
            list.add(ingredient(ingredient));
        }
        json.add("ingredients", list);
        json.add("result", result(result, count));
        emit(NestedInfinity.MODID + "/recipe/" + path, json);
    }

    /** Emits a c:/minecraft: item tag JSON, e.g. tag("c:dusts/naquadah", "..."). */
    public void tag(String tagId, String... values) {
        emitTag("item", tagId, values);
    }

    /** Emits a fluid tag JSON under data/&lt;ns&gt;/tags/fluid/, e.g. fluidTag("mi_nested_infinity:algae_cultures", ...). */
    public void fluidTag(String tagId, String... values) {
        emitTag("fluid", tagId, values);
    }

    private void emitTag(String kind, String tagId, String... values) {
        JsonObject json = new JsonObject();
        json.addProperty("replace", false);
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(value);
        }
        json.add("values", array);
        String namespace = tagId.substring(0, tagId.indexOf(':'));
        String tagPath = tagId.substring(tagId.indexOf(':') + 1);
        emit(namespace + "/tags/" + kind + "/" + tagPath, json);
    }

    public final class MachineBuilder {
        private final JsonObject json;

        private MachineBuilder(String type, int eu, int duration) {
            this.json = new JsonObject();
            this.json.addProperty("type", "modern_industrialization:" + type);
            this.json.addProperty("eu", eu);
            this.json.addProperty("duration", duration);
        }

        public MachineBuilder itemIn(String itemId, int amount) {
            return ingredient("item_inputs", "item", itemId, amount, 1.0);
        }

        /** Input consumed only with the given independent probability per craft (MI: chance of consumption). */
        public MachineBuilder itemIn(String itemId, int amount, double probability) {
            return ingredient("item_inputs", "item", itemId, amount, probability);
        }

        public MachineBuilder tagIn(String tagId, int amount) {
            return ingredient("item_inputs", "tag", tagId, amount, 1.0);
        }

        /** Tag input consumed only with the given independent probability per craft (MI: chance of consumption). */
        public MachineBuilder tagIn(String tagId, int amount, double probability) {
            return ingredient("item_inputs", "tag", tagId, amount, probability);
        }

        public MachineBuilder fluidIn(String fluidId, int amount) {
            return ingredient("fluid_inputs", "fluid", fluidId, amount, 1.0);
        }

        public MachineBuilder fluidTagIn(String tagId, int amount) {
            return ingredient("fluid_inputs", "tag", tagId, amount, 1.0);
        }

        public MachineBuilder itemOut(String itemId, int amount) {
            return ingredient("item_outputs", "item", itemId, amount, 1.0);
        }

        /** Output produced with the given independent roll probability (MI machine recipe format). */
        public MachineBuilder itemOut(String itemId, int amount, double probability) {
            return ingredient("item_outputs", "item", itemId, amount, probability);
        }

        public MachineBuilder fluidOut(String fluidId, int amount) {
            return ingredient("fluid_outputs", "fluid", fluidId, amount, 1.0);
        }

        public void save(String path) {
            emit(NestedInfinity.MODID + "/recipe/" + path, json);
        }

        private MachineBuilder ingredient(String section, String kind, String id, int amount, double probability) {
            JsonObject entry = new JsonObject();
            entry.addProperty(kind, id);
            entry.addProperty("amount", amount);
            if (probability < 1.0) {
                entry.addProperty("probability", probability);
            }
            array(section).add(entry);
            return this;
        }

        private JsonArray array(String section) {
            if (!json.has(section)) {
                json.add(section, new JsonArray());
            }
            return json.getAsJsonArray(section);
        }
    }

    /** Tag ingredient; build with NIRecipes.tag("c:plates/iridium"). */
    public record Tag(String id) {}

    public static Tag tag(String id) {
        return new Tag(id);
    }

    private static JsonObject ingredient(Object spec) {
        JsonObject entry = new JsonObject();
        if (spec instanceof Tag t) {
            entry.addProperty("tag", t.id());
        } else {
            entry.addProperty("item", (String) spec);
        }
        return entry;
    }

    private static JsonObject result(String result, int count) {
        JsonObject entry = new JsonObject();
        entry.addProperty("item", result);
        if (count != 1) {
            entry.addProperty("count", count);
        }
        return entry;
    }

    private void emit(String pathUnderData, JsonObject json) {
        JsonObject existing = pending.put(pathUnderData, json);
        if (existing != null) {
            throw new IllegalStateException("Duplicate generation path: " + pathUnderData);
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * Writes the files directly AND registers them with the CachedOutput.
     * Writing without registering gets the files deleted by datagen's stale sweep;
     * going through saveStable alone is silently dropped on NeoForge 21.1 — so do both.
     */
    public void writeAll(CachedOutput cachedOutput) {
        for (Map.Entry<String, JsonObject> entry : pending.entrySet()) {
            Path file = output.getOutputFolder().resolve("data").resolve(entry.getKey() + ".json");
            byte[] bytes = (GSON.toJson(entry.getValue()) + "\n").getBytes(StandardCharsets.UTF_8);
            try {
                Files.createDirectories(file.getParent());
                Files.write(file, bytes);
                cachedOutput.writeIfNeeded(file, bytes, Hashing.sha1().hashBytes(bytes));
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to generate: " + entry.getKey(), e);
            }
        }
    }
}
