 package ext.mocker.common.util;

 import org.apache.commons.lang3.StringUtils;
 import org.apache.commons.lang3.Validate;
 import org.apache.commons.lang3.reflect.MethodUtils;

 import java.lang.annotation.Annotation;
 import java.lang.reflect.*;

 public class ReflectionUtil
 {
   public static Field getFieldByName(Object obj, String name)
   {
     Field f = null;
     try
     {
       f = obj.getClass().getDeclaredField(name);
     }
     catch (Exception localException) {}
     return f;
   }

   public static Field getDeclaredFieldByType(Object obj, Class type)
   {
     try
     {
       for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass())
       {
         Field[] fields = obj.getClass().getDeclaredFields();
         for (int i = 0; i < fields.length; i++)
         {
           Field curField = fields[i];
           if (curField.getType().equals(type)) {
             return curField;
           }
         }
       }
     }
     catch (Exception e)
     {
       throw new RuntimeException(e);
     }
     return null;
   }

   public static Method getMethodByName(Class<?> clazz, String name)
   {
     Method[] methods = clazz.getDeclaredMethods();
     for (Method m : methods) {
       if (m.getName().equals(name)) {
         return m;
       }
     }
     return null;
   }

   public static Object invokeGetterMethod(Object target, String propertyName)
     throws Exception
   {
     String getterMethodName = "get" + StringUtils.capitalize(propertyName);
     if (!isMethodExists(target, getterMethodName, new Class[0])) {
       getterMethodName = "is" + StringUtils.capitalize(propertyName);
     }
     return invokeMethod(target, getterMethodName, new Class[0], new Object[0]);
   }

   public static boolean isFieldAnnotationPresent(Object obj, String fieldName, Class clazz)
   {
     Field field = getFieldByName(obj, fieldName);
     try
     {
       return field.isAnnotationPresent(clazz);
     }
     catch (Exception localException) {}
     return false;
   }

   public static <A extends Annotation> A getAnnotation(Class clazz, Class<A> annotationClass)
   {
     for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass())
     {
       A annotation = superClass.getAnnotation(annotationClass);
       if (annotation != null) {
         return annotation;
       }
     }
     return null;
   }

   public static Method getGetterMethodByPropertyName(Object target, String propertyName)
   {
     String getterMethodName = "get" + StringUtils.capitalize(propertyName);
     if (!isMethodExists(target, getterMethodName, new Class[0])) {
       getterMethodName = "is" + StringUtils.capitalize(propertyName);
     }
     Method method = null;
     try
     {
       method = target.getClass().getDeclaredMethod(getterMethodName, (Class[])null);
     }
     catch (Exception localException) {}
     return method;
   }

   public static void invokeSetterMethod(Object target, String propertyName, Object value)
     throws Exception
   {
     invokeSetterMethod(target, propertyName, value, null);
   }

   public static void invokeSetterMethod(Object target, String propertyName, Object value, Class<?> propertyType)
     throws Exception
   {
     Class<?> type = propertyType != null ? propertyType : value.getClass();
     String setterMethodName = "set" + StringUtils.capitalize(propertyName);
     invokeMethod(target, setterMethodName, new Class[] { type }, new Object[] { value });
   }

   public static Object getFieldValue(Object object, String fieldName)
   {
     return operaFieldValue("getFieldValue", object, fieldName, null);
   }

   public static <T> T getFieldValueByType(Object obj, Class<T> type)
   {
     T result = null;
     Field field = getDeclaredFieldByType(obj, type);
     if (field != null) {
       try
       {
         field.setAccessible(true);
         result = (T) field.get(obj);
       }
       catch (IllegalAccessException e)
       {
         throw new RuntimeException(e);
       }
     }
     return result;
   }

   public static void setFieldValue(Object object, String fieldName, Object value)
   {
     operaFieldValue("setFieldValue", object, fieldName, value);
   }

   private static Object operaFieldValue(String optype, Object object, String fieldName, Object value)
   {
     Object result = null;
     Field field = getDeclaredField(object, fieldName);
     if (field == null) {
       throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
     }
     makeAccessible(field);
     try
     {
       if ("setFieldValue".equals(optype)) {
         field.set(object, value);
       } else {
         result = field.get(object);
       }
     }
     catch (IllegalAccessException e)
     {
       throw new RuntimeException(e);
     }
     return result;
   }

   public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object... parameters)
     throws Exception
   {
     Method method = getAccessbleMethod(object, methodName, parameterTypes);
     if (method == null) {
       throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
     }
     return method.invoke(object, parameters);
   }

   public static Object invokeStaticMethod(ClassLoader loader, String clazzName, String methodName, Class<?>[] parameterTypes, Object... parameters)
   {
     try
     {
       Class<?> clazz = Class.forName(clazzName, true, loader);
       return invokeStaticMethod(clazz, methodName, parameterTypes, parameters);
     }
     catch (Exception e)
     {
       throw convertReflectionExceptionToUnchecked(e);
     }
   }

   public static Object invokeStaticMethod(String clazzName, String methodName, Class<?>[] parameterTypes, Object... parameters)
   {
     return invokeStaticMethod(Thread.currentThread().getContextClassLoader(), clazzName, methodName, parameterTypes, parameters);
   }

   public static Object invokeStaticMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object... parameters)
   {
     try
     {
       Method method = clazz.getMethod(methodName, parameterTypes);
       return method.invoke(clazz, parameters);
     }
     catch (Exception e)
     {
       throw convertReflectionExceptionToUnchecked(e);
     }
   }

   private static boolean isMethodExists(Object object, String methodName, Class<?>[] parameterTypes)
   {
     Method method = getAccessbleMethod(object, methodName, parameterTypes);
     if (method == null) {
       return false;
     }
     return true;
   }

   public static Method getAccessbleMethod(Object object, String methodName, Class<?>... parameterTypes)
   {
     Validate.notNull(object);
     for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
       try
       {
         Method method = superClass.getDeclaredMethod(methodName, parameterTypes);
         method.setAccessible(true);
         return method;
       }
       catch (NoSuchMethodException localNoSuchMethodException) {}
     }
     return null;
   }

   public static Field getDeclaredField(Object object, String fieldName)
   {
     Validate.notNull(object);
     Validate.notNull(fieldName);
     for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
       try
       {
         return superClass.getDeclaredField(fieldName);
       }
       catch (NoSuchFieldException localNoSuchFieldException) {}
     }
     return null;
   }

   public static <T> Object getInvokeValue(T t, String methodName)
   {
     try
     {
       Method method = MethodUtils.getAccessibleMethod(t.getClass(), methodName, new Class[0]);
       return method.invoke(t, new Object[0]);
     }
     catch (Exception e) {}
     return null;
   }

   public static void makeAccessible(Field field)
   {
     if ((!Modifier.isPublic(field.getModifiers())) || (!Modifier.isPublic(field.getDeclaringClass().getModifiers()))) {
       field.setAccessible(true);
     }
   }

   public static RuntimeException convertReflectionExceptionToUnchecked(Exception e)
   {
     if (((e instanceof IllegalAccessException)) || ((e instanceof IllegalArgumentException)) || ((e instanceof NoSuchMethodException))) {
       return new IllegalArgumentException("Reflection Exception.", e);
     }
     if ((e instanceof InvocationTargetException)) {
       return new RuntimeException("Reflection Exception.", ((InvocationTargetException)e).getTargetException());
     }
     if ((e instanceof RuntimeException)) {
       return (RuntimeException)e;
     }
     return new RuntimeException("Unexpected Checked Exception.", e);
   }

   public static Class getSuperClassGenericType(Class clazz, int index)
   {
     Type genericType = clazz.getGenericSuperclass();
     if (!(genericType instanceof ParameterizedType)) {
       return Object.class;
     }
     Type[] paramTypes = ((ParameterizedType)genericType).getActualTypeArguments();
     if ((index > paramTypes.length) || (index < 0)) {
       return Object.class;
     }
     if (!(paramTypes[index] instanceof Class)) {
       return Object.class;
     }
     return (Class)paramTypes[index];
   }

   public static boolean hasMethod(Class clazz, Method method)
   {
     boolean matched = false;
     for (Class<?> superClass = clazz; (superClass != null) && (superClass != Object.class); superClass = superClass.getSuperclass()) {
       try
       {
         Method exitingMethod = superClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
         if ((exitingMethod != null) &&
           (exitingMethod.getReturnType().equals(method.getReturnType())))
         {
           matched = true;
           break;
         }
       }
       catch (NoSuchMethodException localNoSuchMethodException) {}
     }
     return matched;
   }
 }



