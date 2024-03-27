package com.example.bookstore.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("books")
@Builder(builderMethodName = "newBuilder", setterPrefix = "set")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Book implements Persistable<String> {
  @Id private String id;
  private String title;
  private String author;
  private String isbn;
  private Integer quantity;

  @ReadOnlyProperty @Builder.Default private boolean isNew = true;

  public Book(String id, String title, String author, String isbn, Integer quantity) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.quantity = quantity;
  }

  @Override
  public boolean isNew() {
    return isNew;
  }
}
