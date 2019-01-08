 package com.auto.ext.mocker.common.util;

 import com.alibaba.fastjson.JSON;
 import com.alibaba.fastjson.parser.Feature;
 import com.alibaba.fastjson.serializer.SerializeConfig;
 import com.alibaba.fastjson.serializer.SerializerFeature;
 import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
 import org.apache.commons.lang3.Validate;

 import java.lang.reflect.Method;
 import java.lang.reflect.Type;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;

 public class JsonHelper
 {
   private static SerializeConfig fastJsonSerializeConfig = new SerializeConfig();
   public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

   static
   {
     fastJsonSerializeConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
   }

   public static String toSimpleJson(Object obj)
   {
     return JSON.toJSONString(obj, fastJsonSerializeConfig, new SerializerFeature[] { SerializerFeature.WriteMapNullValue });
   }

   public static String toPrettyJson(Object obj)
   {
     return JSON.toJSONString(obj, fastJsonSerializeConfig, new SerializerFeature[] { SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue });
   }

   public static <T> T parseObject(String input, Type objectType)
   {
     return JSON.parseObject(input, objectType, new Feature[] { Feature.IgnoreNotMatch, Feature.AllowISO8601DateFormat });
   }

   public static Object parseText(String input)
   {
     return JSON.parse(input, new Feature[] { Feature.IgnoreNotMatch, Feature.AllowISO8601DateFormat });
   }

   public static List<Object> convertParams(Method method, String jsonParams)
   {
     Type[] paramTypes = method.getGenericParameterTypes();
     Object paramsJsonObject = parseText(jsonParams);
     if ((paramsJsonObject instanceof Iterable))
     {
       List<String> paramList = new ArrayList();
       for (Object jsonObject : (Iterable)paramsJsonObject)
       {
         String paramString = null;
         if (jsonObject != null) {
           paramString = (jsonObject instanceof String) ? (String)jsonObject : toSimpleJson(jsonObject);
         }
         paramList.add(paramString);
       }
       String[] params = new String[paramList.size()];
       return convertParams(method, (String[])paramList.toArray(params));
     }
     Validate.isTrue(1 == paramTypes.length, "param size not matched!", new Object[0]);
     return convertParams(method, jsonParams);
   }

   public static List<Object> convertParams(Method method, String... jsonParams)
   {
     Type[] paramTypes = method.getGenericParameterTypes();
     Validate.isTrue(jsonParams.length == paramTypes.length, "param size not matched!", new Object[0]);
     List<Object> params = new ArrayList();
     int index = 0;
     for (String jsonParam : jsonParams)
     {
       if (!jsonParam.equals("null"))
       {
         Type type = paramTypes[index];
         Object param;
          if (type == String.class) {
           param = jsonParam;
         } else {
           param = parseObject(jsonParam, type);
         }
         params.add(param);
       }
       index++;
     }
     return params;
   }
 }


