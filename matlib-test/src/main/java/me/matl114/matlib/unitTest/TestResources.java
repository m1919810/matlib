package me.matl114.matlib.unitTest;

import com.google.gson.*;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.matl114.matlib.algorithms.algorithm.FileUtils;
import me.matl114.matlib.algorithms.algorithm.IterUtils;
import me.matl114.matlib.algorithms.dataStructures.struct.IndexEntry;

public interface TestResources {
    static TestResources I = new TestResources() {};

    default String readStr(String path) {
        return FileUtils.readResourceString("tests/" + path);
    }

    default JsonElement readJson(String path) {
        return FileUtils.readResourceJson("tests/" + path);
    }

    default Iterable<IndexEntry<JsonElement>> getTestCases(String paths) {
        JsonElement element = readJson(paths);
        if (element.isJsonArray()) {
            return IterUtils.fastEnumerate(element.getAsJsonArray().asList());
        } else {
            return List.of(IndexEntry.immutable0(element));
        }
    }

    default JsonElement getInput(JsonElement json) {
        try {
            return ((JsonObject) json).get("input");
        } catch (ClassCastException e) {
            throw new RuntimeException("Test json file format error!" + json);
        }
    }

    default JsonElement getOutput(JsonElement json) {
        try {
            return ((JsonObject) json).get("output");
        } catch (ClassCastException e) {
            throw new RuntimeException("Test json file format error!" + json);
        }
    }

    default JsonElement getArg(JsonElement json, String arg) {
        try {
            return ((JsonObject) json).get(arg);
        } catch (ClassCastException e) {
            throw new RuntimeException("Test json file format error!" + json + " has no member " + arg);
        }
    }

    default JsonElement getArgs(JsonElement element, String paths) {
        JsonElement el = element;
        String[] path = paths.split("[.]");
        for (int i = 0; i < path.length; ++i) {
            el = ((JsonObject) el).get(path[i]);
        }
        return el;
    }

    default JsonObject map(JsonElement element) {
        return (JsonObject) element;
    }

    default Set<Map.Entry<String, JsonElement>> entrySet(JsonElement element) {
        return map(element).asMap().entrySet();
    }

    default JsonArray buildTestcase(JsonArray origin, JsonElement input, JsonElement output) {
        if (origin == null) {
            origin = new JsonArray();
        }
        JsonObject obj = new JsonObject();
        obj.add("input", input);
        obj.add("output", output);
        origin.add(obj);
        return origin;
    }

    default void writeJson(String name, JsonElement json) throws Throwable {
        writeStr(name, new Gson().toJson(json));
    }

    default void writeStr(String name, String element) throws Throwable {
        File parentFile = MatlibTest.getInstance().getDataFolder();
        File f1 = FileUtils.getOrCreateFile(new File(parentFile, name));
        Files.writeString(f1.toPath(), element);
    }
}
