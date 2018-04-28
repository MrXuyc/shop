package com.mrxuyc.shop.util;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.util.JSONPObject;
import org.codehaus.jackson.map.util.JSONWrappedObject;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: mrxuyc
 * Date: 2018-04-17
 * Time: 9:39
 */
@Slf4j
public final class JacksonUtil {

    private static ObjectMapper objectMapper=new ObjectMapper();

    static {
        //限制对象属性是否转换  ALWAYS则是全部null就为“null” non_null为非null字段
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        //取消默认将时间转换为时间戳
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //日期格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
        //忽略json中存在，但是java对象不存在对应属性的情况，防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    /**
     * 对象转换成json字符串
     *
     * @param obj
     * @return
     */
    public static <T> String toJson(T obj) {
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj :  objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }


    /**
     * json字符串转成对象
     *
     * @param str
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String str, Class<T> clazz) {
        if(StringUtils.isEmpty(str) || clazz == null){
            return null;
        }

        try {
            return clazz.equals(String.class)? (T)str : objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    /**
     * json字符串转成JsonObject
     *
     * @param str
     * @return
     */
    public static JsonObject fromJsonToJsonObject(String str) {
        return  new JsonParser().parse(str).getAsJsonObject();
    }

    /**
     * json字符串转成JsonArray
     *
     * @param str
     * @return
     */
    public static JsonArray fromJsonToJsonArray(String str) {
        return  new JsonParser().parse(str).getAsJsonArray();
    }

    /**
     * json字符串转成集合
     * @param str
     * @param collectionClass  集合类型 List
     * @param elementClasses  集合内部的类型 User
     * @param <T>
     * @return
     */
    public static <T> T fromJsonToCollection(String str, Class<?> collectionClass,Class<?>... elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }

    /**
     *
     * @param str
     * @param typeReference new TypeReference<List<User>>() {}
     * @param <T>
     * @return
     */
    public static <T> T stringToObj(String str, TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str) || typeReference == null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)? str : objectMapper.readValue(str,typeReference));
        } catch (Exception e) {
            log.warn("Parse String to Object error",e);
            return null;
        }
    }


}
