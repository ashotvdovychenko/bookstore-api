package com.example.bookstore.api.mapper;

import com.example.bookstore.proto.Book;
import com.example.bookstore.proto.BookId;
import com.example.bookstore.proto.CreateBook;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookMapper {
  BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

  @Mapping(target = "id", expression = "java(toBookId(book.getId()))")
  Book toProto(com.example.bookstore.api.domain.Book book);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isNew", ignore = true)
  com.example.bookstore.api.domain.Book toModel(CreateBook createBook);

  @Mapping(target = "id", source = "id.id")
  @Mapping(target = "isNew", ignore = true)
  com.example.bookstore.api.domain.Book toModel(Book book);

  default BookId toBookId(String id) {
    return BookId.newBuilder().setId(id).build();
  }
}
