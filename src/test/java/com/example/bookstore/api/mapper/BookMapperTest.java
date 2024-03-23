package com.example.bookstore.api.mapper;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.bookstore.api.domain.Book;
import com.example.bookstore.proto.BookId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookMapperTest {

  @Test
  void toProtoSuccessful() {
    var id = UUID.randomUUID();
    var title = "Title";
    var isbn = "Isbn";
    var author = "Ivan Kyrylov";
    var quantity = 5;
    var book =
        Book.builder().id(id).author(author).isbn(isbn).title(title).quantity(quantity).build();

    var expected =
        com.example.bookstore.proto.Book.newBuilder()
            .setId(BookId.newBuilder().setId(id.toString()).build())
            .setAuthor(author)
            .setIsbn(isbn)
            .setTitle(title)
            .setQuantity(quantity)
            .build();
    var actual = BookMapper.INSTANCE.toProto(book);

    assertEquals(actual, expected);
  }

  @Test
  void toProtoFailed() {
    var id = UUID.randomUUID();
    var title = "Title";
    var isbn = "Isbn";
    var author = "Ivan Kyrylov";
    var quantity = 5;
    var book =
        Book.builder().id(id).author(author).isbn(isbn).title(title).quantity(quantity).build();

    var expected =
        com.example.bookstore.proto.Book.newBuilder()
            .setId(BookId.newBuilder().setId("").build())
            .setAuthor(author)
            .setIsbn(isbn)
            .setTitle(title)
            .setQuantity(quantity)
            .build();
    var actual = BookMapper.INSTANCE.toProto(book);

    assertThat(actual).isNotEqualTo(expected);
  }
}
