package me.matl114.matlib.utils.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import lombok.Setter;
import me.matl114.matlib.utils.Debug;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Deprecated(forRemoval = true)
public class YamlConfig extends Config {
    File file;
    private static Yaml yaml;

    @Setter
    private boolean useFloatPrecision = false;

    @Setter
    private boolean useDoublePrecision = false;

    private List<Function<Object, Object>> loadValueHandlers = new ArrayList<>();

    public YamlConfig addLoadValueHandler(Function<Object, Object> handler) {
        loadValueHandlers.add(handler);
        return this;
    }

    private List<Function<Object, Object>> dumpValueHandlers = new ArrayList<>();

    public YamlConfig addDumpValueHandler(Function<Object, Object> handler) {
        dumpValueHandlers.add(handler);
        return this;
    }

    static {
        DumperOptions options = new DumperOptions();
        options.setIndent(2); // 设置缩进为 2 空格
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // 使用块风格
        options.setPrettyFlow(true); // 启用漂亮的流式显示
        yaml = new Yaml(options);
    }

    public YamlConfig(HashMap<String, Object> data) {
        super(data);
        this.file = null;
    }

    public YamlConfig(File file, HashMap<String, Object> data) {
        super(data);
        this.file = file;
    }

    public YamlConfig(File file) throws IOException {
        this(file, loadYaml(file));
    }

    public YamlConfig(InputStream stream) throws IOException {
        this(null, loadYaml(stream));
    }

    public static HashMap<String, Object> loadYaml(File file) throws IOException {
        Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        return yaml.load(reader);
    }

    public static HashMap<String, Object> loadYaml(InputStream inputStream) {
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return yaml.load(reader);
    }

    public static void saveYaml(File file, HashMap<String, Object> data) throws IOException {
        if (!file.exists()) {
            createFile(file);
        }
        yaml.dump(data, new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public void save(File ffile) {
        try {
            this.updateRawData();
            saveYaml(ffile, this.data);
        } catch (Throwable e) {
            Debug.severe("Exception while saving a Config file: ", e.getMessage());
        }
    }

    //    @Override
    //    public Object castWhenLoad(Object val) {
    //        for (Function<Object,Object> handler: loadValueHandlers) {
    //            val=handler.apply(val);
    //        }
    //        if(this.useDoublePrecision==this.useFloatPrecision){
    //            return val;
    //        }else if(this.useDoublePrecision){
    //            if(val instanceof Float f){
    //                return f.doubleValue();
    //            }
    //        }else {
    //            if(val instanceof Double d){
    //                return d.floatValue();
    //            }
    //        }
    //        return val;
    //    }
    //
    //    @Override
    //    public Object castWhenDump(Object val) {
    //        for (Function<Object,Object> handler: dumpValueHandlers) {
    //            val=handler.apply(val);
    //        }
    //        return val;
    //    }

    @Override
    public void reload() {
        try {
            HashMap<String, Object> data = loadYaml(this.file);
            reloadInternal(data);
        } catch (Throwable e) {
            Debug.severe("Exception while reloading a Config file: ", e.getMessage());
        }
    }
}
