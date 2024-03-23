package com.example.bookstore.api.util;

import java.util.function.Supplier;

public interface IdGenerator<T> extends Supplier<T> {
  T generate();

  @Override
  default T get() {
    return generate();
  }
}
