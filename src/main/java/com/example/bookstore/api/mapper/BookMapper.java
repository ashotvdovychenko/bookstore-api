package com.example.bookstore.api.mapper;

import com.example.bookstore.proto.Book;
import com.example.bookstore.proto.BookId;
import com.example.bookstore.proto.CreateBook;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {
  BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

  @Mapping(target = "id", expression = "java(toBookId(book.getId()))")
  Book toProto(com.example.bookstore.api.domain.Book book);

  @Mapping(target = "id", ignore = true)
  com.example.bookstore.api.domain.Book createToModel(CreateBook createBook);

  @Mapping(target = "id", expression = "java(toUuid(book.getId())")
  com.example.bookstore.api.domain.Book protoToModel(Book book);

  default BookId toBookId(UUID id) {
    return BookId.newBuilder().setId(id.toString()).build();
  }

  default UUID toUuid(BookId bookId) {
    return UUID.fromString(bookId.getId());
  }
}
