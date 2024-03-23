package com.example.bookstore.api.domain;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("books")
@Builder(builderMethodName = "newBuilder", setterPrefix = "set")
@Accessors(chain = true)
public class Book {
  @Id private UUID id;
  private String title;
  private String author;
  private String isbn;
  private Integer quantity;
}
