/*
 *
 * Copyright 2020-2020 HuiFer All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.huifer.crud.common.service.facade;

import com.github.huifer.crud.common.annotation.entity.CacheKeyEntity;
import com.github.huifer.crud.common.intefaces.CrudTemplate;
import com.github.huifer.crud.common.intefaces.id.StrIdInterface;
import com.github.huifer.crud.common.runner.CrudScanPackageRunner;
import com.google.gson.Gson;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CrudEntityFacade<T> implements CrudTemplate<T, StrIdInterface<String>> {


  Gson gson = new Gson();
  @Autowired
  private StringRedisTemplate redisTemplate;

  private static Object getFiled(Object o, String filed) {
    Class<?> aClass = o.getClass();
    Field[] fields = aClass.getDeclaredFields();
    Object res = null;
    for (int i = 0; i < fields.length; i++) {
      fields[i].setAccessible(true);
      if (fields[i].getName().equals(filed)) {
        try {
          res = fields[i].get(o);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return res;
  }


  private static String key(Object o, String method) {

    try {
      Class<?> aClass = o.getClass();
      if (!StringUtils.isEmpty(method)) {

        Method method1 = aClass.getDeclaredMethod(method);
        method1.setAccessible(true);
        Object invoke = method1.invoke(o);
        return String.valueOf(invoke);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  private static String key(Object o, String filed, String method) {

    String res = "";
    // 1. 执行method函数
    if (StringUtils.isEmpty(res)) {
      res = key(o, method);
    }
    // 如果函数存在结果直接返回
    if (!StringUtils.isEmpty(res)) {
      return res;
    }

    // 2. 从 filed 获取
    Object k1 = getFiled(o, filed);
    res = String.valueOf(k1);

    if (StringUtils.isEmpty(res)) {
      throw new NullPointerException("key 不能为空");
    }
    return res;

  }

  @Override
  public boolean insert(T t) {

    CacheKeyEntity cacheKeyEntity = CrudScanPackageRunner.cacheKeyEntity(t.getClass());

    redisTemplate.opsForHash()
        .put(cacheKeyEntity.getKey(),
            String.valueOf(key(t, cacheKeyEntity.getIdFiled(), cacheKeyEntity.getIdMethod())),
            gson.toJson(t));

    return true;
  }

  @Override
  public T byId(StrIdInterface<String> stringStrIdInterface, Class<?> c) {
    CacheKeyEntity cacheKeyEntity = CrudScanPackageRunner.cacheKeyEntity(c);

    String o = (String) redisTemplate.opsForHash()
        .get(cacheKeyEntity.getKey(), stringStrIdInterface.id());
    return (T) gson.fromJson(o, c);
  }

  @Override
  public boolean del(StrIdInterface<String> stringStrIdInterface, Class<?> c) {
    CacheKeyEntity cacheKeyEntity = CrudScanPackageRunner.cacheKeyEntity(c);

    redisTemplate.opsForHash().delete(cacheKeyEntity.getKey(), stringStrIdInterface.id());
    return true;
  }

  @Override
  public boolean editor(T t) {
    CacheKeyEntity cacheKeyEntity = CrudScanPackageRunner.cacheKeyEntity(t.getClass());

    redisTemplate.opsForHash()
        .put(cacheKeyEntity.getKey(),
            key(t, cacheKeyEntity.getIdFiled(), cacheKeyEntity.getIdMethod()),
            gson.toJson(t));
    return true;
  }
}