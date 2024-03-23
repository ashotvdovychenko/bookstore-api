package com.example.bookstore.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bookstore.api.domain.Book;
import com.example.bookstore.proto.BookId;
import com.example.bookstore.proto.CreateBook;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookMapperTest {

  @Test
  void toProto() {
    var id = UUID.randomUUID();
    var title = "Title";
    var isbn = "Isbn";
    var author = "Ivan Kyrylov";
    var quantity = 5;
    var book =
        Book.newBuilder()
            .setId(id)
            .setAuthor(author)
            .setIsbn(isbn)
            .setTitle(title)
            .setQuantity(quantity)
            .build();

    var expected =
        com.example.bookstore.proto.Book.newBuilder()
            .setId(BookId.newBuilder().setId(id.toString()).build())
            .setAuthor(author)
            .setIsbn(isbn)
            .setTitle(title)
            .setQuantity(quantity)
            .build();
    var actual = BookMapper.INSTANCE.toProto(book);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void toModelFromCreateBook() {
    var title = "Title";
    var isbn = "Isbn";
    var author = "Ivan Kyrylov";
    var quantity = 5;
    var createBook =
        CreateBook.newBuilder()
            .setAuthor(author)
            .setIsbn(isbn)
            .setTitle(title)
            .setQuantity(quantity)
            .build();

    var expected =
        Book.newBuilder()
            .setTitle(title)
            .setAuthor(author)
            .setIsbn(isbn)
            .setQuantity(quantity)
            .build();
    var actual = BookMapper.INSTANCE.toModel(createBook);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void toModelFromProtoBook() {
    var id = UUID.randomUUID();
    var title = "Title";
    var isbn = "Isbn";
    var author = "Ivan Kyrylov";
    var quantity = 5;
    var protoBook =
        com.example.bookstore.proto.Book.newBuilder()
            .setId(BookId.newBuilder().setId(id.toString()).build())
            .setAuthor(author)
            .setIsbn(isbn)
            .setTitle(title)
            .setQuantity(quantity)
            .build();

    var expected =
        Book.newBuilder()
            .setId(id)
            .setTitle(title)
            .setIsbn(isbn)
            .setAuthor(author)
            .setQuantity(quantity)
            .build();
    var actual = BookMapper.INSTANCE.toModel(protoBook);

    assertThat(actual).isEqualTo(expected);
  }
}
