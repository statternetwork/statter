package com.statter.statter.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JSONUtils {

    public static String toJson(Object object) {
        return toJSONObjectWriteNullValue(object).toJSONString();
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static JSONObject toJSONObject(Object object) {
        return (JSONObject) JSONObject.toJSON(object);
    }

    public static JSONObject toJSONObjectWriteNullValue(Object object) {
        return JSON.parseObject(JSON.toJSONString(object, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero));
    }

}
