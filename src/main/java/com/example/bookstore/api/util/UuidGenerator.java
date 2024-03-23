package com.example.bookstore.api.util;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidGenerator implements IdGenerator<String> {
  @Override
  public String generate() {
    return UUID.randomUUID().toString();
  }
}
