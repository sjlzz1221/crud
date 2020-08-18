package com.github.huifer.crud.common.runner;


import com.github.huifer.crud.common.annotation.CacheKey;
import com.github.huifer.crud.common.annotation.CacheKeyEntity;
import com.github.huifer.crud.common.daotype.EnableCrudTemplateThreadLocal;
import com.github.huifer.crud.common.runner.utils.ScanUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class CrudScanPackageRunner implements CommandLineRunner, Ordered {


  public static Map<Class<?>, CacheKeyEntity> PACKAGE_CACHE_INFO = new HashMap<>();


  @Override
  public void run(String... args) throws Exception {
    scanPackages();
  }

  private void scanPackages() throws IOException {
    List<String> packages = EnableCrudTemplateThreadLocal.getPackages();
    for (String pack : packages) {
      Set<Class<?>> classes = ScanUtils.getClasses(pack);
      for (Class<?> aClass : classes) {
        keyEntity(aClass);
      }
    }
  }


  /**
   * put data
   *
   * @param cacheKeyEntity
   */
  private void put(CacheKeyEntity cacheKeyEntity) {
    if (cacheKeyEntity != null) {
      Class<?> type = cacheKeyEntity.getType();
      CacheKeyEntity ck = PACKAGE_CACHE_INFO.get(type);
      if (ck != null) {
        throw new RuntimeException("has a cache");
      } else {
        PACKAGE_CACHE_INFO.put(cacheKeyEntity.getType(), cacheKeyEntity);
      }
    }
  }

  /**
   * from clazz get {@code CacheKey} convert to {@code CacheKeyEntity}
   *
   * @param clazz class
   * @return CacheKey attr
   */
  private void keyEntity(Class<?> clazz) {
    CacheKey annotation = clazz.getAnnotation(CacheKey.class);
    if (annotation != null) {
      CacheKeyEntity cacheKeyEntity = new CacheKeyEntity();
      cacheKeyEntity.setKey(annotation.key());
      cacheKeyEntity.setType(annotation.type());
      cacheKeyEntity.setIdFiled(annotation.idFiled());
      put(cacheKeyEntity);
    }
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}