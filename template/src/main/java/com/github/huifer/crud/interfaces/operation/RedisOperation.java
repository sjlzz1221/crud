package com.github.huifer.crud.interfaces.operation;


import com.github.huifer.crud.interfaces.id.IdInterface;

public interface RedisOperation<T, I extends IdInterface> {

  void insert(T t);


  void update(I i, T t);

  void del(I i);

  T byId(I i);

  Class<?> type();

  String key();
}