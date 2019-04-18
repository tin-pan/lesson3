package org.level.up.json;
//
public interface JsonDeserialised<T> {

    T deserialize(String json);
}
