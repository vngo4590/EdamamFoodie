package au.edu.sydney.soft3202.majorproject.model.utils;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class ModelUtils {
  private static boolean isGetter(Method method) {
    return (method.getName().startsWith("get") || method.getName().startsWith("is"))
        && method.getParameterCount() == 0
        && !method.getReturnType().equals(void.class);
  }

  private static boolean isSetter(Method method) {
    return method.getName().startsWith("set")
        && method.getParameterCount() == 1
        && method.getReturnType().equals(void.class);
  }

  public static Method getGetterMethod(Class<?> clazz, String fieldName) {
    Method[] methods = clazz.getDeclaredMethods();
    String newFieldName = fieldName.replace("_", "").replace(".", "");
    for (Method method : methods) {
      if (isGetter(method)
          && Pattern.compile("^.*" + Pattern.quote(newFieldName) + "$", Pattern.CASE_INSENSITIVE)
              .matcher(method.getName())
              .find()) {

        return method;
      }
    }
    return null;
  }

  public static Method getSetterMethod(Class<?> clazz, String fieldName) {
    Method[] methods = clazz.getDeclaredMethods();
    String newFieldName = fieldName.replace("_", "").replace(".", "");

    for (Method method : methods) {
      if (isSetter(method)
          && Pattern.compile("^.*" + Pattern.quote(newFieldName) + "$", Pattern.CASE_INSENSITIVE)
              .matcher(method.getName())
              .find()) {
        return method;
      }
    }
    return null;
  }

}
