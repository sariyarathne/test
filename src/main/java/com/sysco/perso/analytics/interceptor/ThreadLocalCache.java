package com.sysco.perso.analytics.interceptor;


import java.util.HashMap;
import java.util.Map;

public class ThreadLocalCache {

  private ThreadLocalCache() {
  }

  private static ThreadLocal<Map<String, Object>> threadLocalStore = new ThreadLocal<>();

  static void put(String key, Object value) {
    Map<String, Object> map = threadLocalStore.get();
    if (map == null) {
      map = new HashMap<>();
      threadLocalStore.set(map);
    }
    map.put(key, value);
  }

  public static Object get(String key) {
    Map<String, Object> map = threadLocalStore.get();
    if ((map != null) && (key != null)) {
      return map.get(key);
    } else {
      return null;
    }
  }

  public static void setCache(Map<String, Object> map) {
    threadLocalStore.set(map);
  }

  public static Map<String, Object> getMap() {
    return threadLocalStore.get();
  }

  public static void clear() {
    threadLocalStore.remove();
  }
}