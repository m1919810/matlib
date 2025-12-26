package me.matl114.matlib.algorithms.designs.serialize;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface JsonCodec<T> extends JsonSerializer<T>, JsonDeserializer<T> {}
