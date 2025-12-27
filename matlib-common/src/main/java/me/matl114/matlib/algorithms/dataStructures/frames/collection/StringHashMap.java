package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.matl114.matlib.algorithms.algorithm.CollectionUtils;

public class StringHashMap implements Map<String, String>, Cloneable {
    public HashMap<String, String> data;
    private static StringHashMap instance = new StringHashMap(null);
    //    public static StringHashMap of(HashMap<String,String> map){
    //        StringHashMap map2= instance.clone();
    //        map2.data=map==null?new HashMap<>():map;
    //        return map2;
    //    }
    public static StringHashMap ofNewMap(Map<String, String> map) {
        StringHashMap map2 = instance.clone();
        map2.data = map == null ? new HashMap<>() : new HashMap<>(map);
        return map2;
    }

    public StringHashMap() {
        this.data = new HashMap<>();
    }

    private StringHashMap(HashMap<String, String> map) {
        this.data = map == null ? new HashMap<>() : new HashMap<>(map);
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    public String get(Object key) {
        return data.get(key);
    }

    public String put(String key, String value) {
        return data.put(key, value);
    }

    public String remove(Object key) {
        return data.remove(key);
    }

    public void putAll(Map<? extends String, ? extends String> m) {
        data.putAll(m);
    }

    public void clear() {
        data.clear();
    }

    public Set<String> keySet() {
        return data.keySet();
    }

    public Collection<String> values() {
        return data.values();
    }

    public Set<Entry<String, String>> entrySet() {
        return data.entrySet();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StringHashMap strmap) {
            return CollectionUtils.compareMap(this.data, strmap.data);
        } else {
            return false;
        }
    }

    public StringHashMap clone() {
        try {
            StringHashMap map = (StringHashMap) super.clone();
            map.data = this.data;
            return map;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
