package com.mrxuyc.shop.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.pojo.User;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: mrxuyc
 * Date: 2018-04-17
 * Time: 9:39
 */
public final class GsonUtil {

    private static GsonBuilder gsonBuilder=new GsonBuilder();

    static {
        //字段过滤处理  返回的是key值  和//@SerializedName("newName")   注解优先级高
        //@SerializedName(value = "emailAddress", alternate = {"email", "email_address"}) 有其中一个属性就会反序列化到对象中
        //全都出现时，以最后一个为准
        gsonBuilder.setFieldNamingStrategy(new FieldNamingStrategy() {
            @Override
            public String translateName(Field field) {
                return field.getName();
            }
        });
        gsonBuilder.setDateFormat(DateTimeUtil.STANDARD_FORMAT);
        //ignore字段 private transient String ignore; 关键字

        //自定义序列化器，当遇到User时，用自定义的UserSerialiser implements JsonSerializer
        //如果其包含子对象 JsonElement jsonAuthros = context.serialize(book.getAuthors());
        //.registerTypeAdapter(User.class, new UserSerialiser());

        //默认为null不进行序列化（没有key）
        //.serializeNulls()

        // 禁此序列化内部类
        //.disableInnerClassSerialization()

        //生成不可执行的Json（多了 )]}' 这4个字符）
       // .generateNonExecutableJson()

        //禁止转义html标签
    //    .disableHtmlEscaping()

        //格式化输出
      //  .setPrettyPrinting()
        //排除PRIVATE 修饰的字段 基于修饰符排除
        //.excludeFieldsWithModifiers(Modifier.PRIVATE)

    }

    private GsonUtil() {

    }

    /**
     * 对象转换成json字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        Gson gson = gsonBuilder.create();
        //可以加流参数
        return gson.toJson(obj);
    }

    /**
     * json字符串转成对象
     *
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Type type) {
        Gson gson = gsonBuilder.create();
        return gson.fromJson(str, type);
    }

    /**
     * json字符串转成对象
     *
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Class<T> type) {
        Gson gson = gsonBuilder.create();
        return gson.fromJson(str, type);
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
     * json字符串转成list<clazz>
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> fromJsonToCollection(String str, Class<T> clazz){
        Gson gson = gsonBuilder.create();
        Type type=new ParameterizedTypeHolder(List.class,new Class[]{clazz});
        List<T> list = gson.fromJson(str,type);
        return list;
    }


    /**
     * 转换为统一返回对象 其中data为List<clazz>
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<List<T>> fromJsonToServerResponseList(String str,Class<T> clazz) {
        Gson gson = gsonBuilder.create();
        Type listType=new ParameterizedTypeHolder(List.class,new Class[]{clazz});
        Type type = new ParameterizedTypeHolder(ServerResponse.class, new Type[]{listType});
        ServerResponse<List<T>> serverResponse = gson.fromJson(str,type);
        return serverResponse;
    }

    /**
     * 转换为统一返回对象 其中data为clazz
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> fromJsonToServerResponse(String str,Class<T> clazz) {
        Gson gson = gsonBuilder.create();
        Type type = new ParameterizedTypeHolder(ServerResponse.class, new Class[]{clazz});
        ServerResponse<T> serverResponse = gson.fromJson(str,type);
        return serverResponse;
    }

    public static void main(String[] args) {
        Gson gson=gsonBuilder.create();
        List<User> aDouble = fromJsonToCollection("[{\"id\":\"1\"},{\"id\":\"2\"}]",User.class) ;
        System.out.println(aDouble);
        ServerResponse<List<User>> users= fromJsonToServerResponseList("{\"code\":\"0\",\"message\":\"success\",\"data\":[{\"id\":\"1\"},{\"id\":\"2\"}]}",User.class);
        System.out.println(users);
        ServerResponse<User> user= fromJsonToServerResponse("{\"code\":\"0\",\"message\":\"success\",\"data\":{\"id\":\"1\"}}",User.class);
        System.out.println(users);
        User userInstance =new User();
        userInstance.setId(1);
        userInstance.setCreateTime(new Date());
        userInstance.setUsername("xuyanchun");
        System.out.println(toJson(userInstance));
    }
}
