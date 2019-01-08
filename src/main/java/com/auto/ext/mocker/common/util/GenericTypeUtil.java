  package com.auto.ext.mocker.common.util;

  import java.lang.reflect.*;
  import java.util.Iterator;
  import java.util.Map;
  import java.util.Map.Entry;

  public class GenericTypeUtil
  {
    public static boolean isType(Object instance, Type expectedType)
    {
      if (instance == null) {
        return true;
      }
      Class<?> expectedClass = toClass(expectedType);
      if (instance.getClass().isArray())
      {
        Type componentType = getGenericType(expectedType, 0);
        Class<?> componentClass = toClass(componentType);
        if (componentClass.isAssignableFrom(instance.getClass().getComponentType())) {
          return (Array.getLength(instance) <= 0) || (isType(Array.get(instance, 0), componentType));
        }
      }
      boolean matched = false;
      TypeVariable[] expectedTypeVariables = expectedClass.getTypeParameters();
      TypeVariable[] actualTypeVariables = instance.getClass().getTypeParameters();
      if ((expectedClass.isInstance(instance)) && (actualTypeVariables.length == expectedTypeVariables.length))
      {
        matched = true;
        if ((instance instanceof Map))
        {
          Map mapInstance = (Map)instance;
          if (!mapInstance.isEmpty())
          {
            Entry entry = (Entry)mapInstance.entrySet().iterator().next();
            Object mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            return (isType(mapKey, getGenericType(expectedType, 0))) && (isType(mapValue, getGenericType(expectedType, 1)));
          }
        }
        else if ((instance instanceof Iterable))
        {
          Iterator iterator = ((Iterable)instance).iterator();
          if (iterator.hasNext())
          {
            Object element = iterator.next();
            return isType(element, getGenericType(expectedType, 0));
          }
        }
      }
      return matched;
    }

    public static Class toClass(Type type)
    {
      if ((type instanceof ParameterizedType)) {
        return toClass(((ParameterizedType)type).getRawType());
      }
      if ((type instanceof TypeVariable)) {
        return toClass(((TypeVariable)type).getBounds()[0]);
      }
      if ((type instanceof GenericArrayType)) {
        return toClass(((GenericArrayType)type).getGenericComponentType());
      }
      return (Class)type;
    }

    public static Class toClass(Type type, int i)
    {
      if ((type instanceof ParameterizedType)) {
        return getGenericClass((ParameterizedType)type, i);
      }
      if ((type instanceof TypeVariable)) {
        return toClass(((TypeVariable)type).getBounds()[0], 0);
      }
      if ((type instanceof GenericArrayType)) {
        return toClass(((GenericArrayType)type).getGenericComponentType());
      }
      return (Class)type;
    }

    public static Type getGenericType(Type type, int i)
    {
      if ((type instanceof ParameterizedType)) {
        return ((ParameterizedType)type).getActualTypeArguments()[i];
      }
      if ((type instanceof TypeVariable)) {
        return ((TypeVariable)type).getBounds()[i];
      }
      if ((type instanceof GenericArrayType)) {
        return ((GenericArrayType)type).getGenericComponentType();
      }
      return type;
    }

    public static Class getGenericClass(ParameterizedType parameterizedType, int i)
    {
      Object genericClass = parameterizedType.getActualTypeArguments()[i];
      if ((genericClass instanceof ParameterizedType)) {
        return (Class)((ParameterizedType)genericClass).getRawType();
      }
      if ((genericClass instanceof GenericArrayType)) {
        return (Class)((GenericArrayType)genericClass).getGenericComponentType();
      }
      if ((genericClass instanceof TypeVariable)) {
        return toClass(((TypeVariable)genericClass).getBounds()[0], 0);
      }
      return (Class)genericClass;
    }
  }


