package com.example.bookstore.api.utils;

import com.example.bookstore.api.domain.Book;
import com.example.bookstore.proto.BookId;
import com.example.bookstore.proto.CreateBook;
import com.example.bookstore.proto.GetAllBooks;

public final class TestData {

  public static final String ID = "ID";
  public static final String AUTHOR = "Author";
  public static final String TITLE = "Title";
  public static final String ISBN = "ISBN";
  public static final Integer QUANTITY = 10;
  public static final Book BOOK = getBook(ID);
  public static final com.example.bookstore.proto.Book PROTO_BOOK = getProtoBook(ID);
  public static final BookId BOOK_ID = getBookId(ID);

  public static final CreateBook CREATE_BOOK = getCreateBook();

  public static final GetAllBooks GET_ALL_BOOKS = getGetAllBooks();

  private TestData() {}

  public static Book getBook(String id) {
    return Book.newBuilder()
        .setId(id)
        .setAuthor(AUTHOR)
        .setTitle(TITLE)
        .setIsbn(ISBN)
        .setQuantity(QUANTITY)
        .build();
  }

  public static com.example.bookstore.proto.Book getProtoBook(String id) {
    return com.example.bookstore.proto.Book.newBuilder()
        .setId(BookId.newBuilder().setId(id).build())
        .setAuthor(AUTHOR)
        .setTitle(TITLE)
        .setIsbn(ISBN)
        .setQuantity(QUANTITY)
        .build();
  }

  public static BookId getBookId(String id) {
    return BookId.newBuilder().setId(id).build();
  }

  public static CreateBook getCreateBook() {
    return CreateBook.newBuilder()
        .setAuthor(AUTHOR)
        .setTitle(TITLE)
        .setIsbn(ISBN)
        .setQuantity(QUANTITY)
        .build();
  }

  public static GetAllBooks getGetAllBooks() {
    return GetAllBooks.newBuilder().build();
  }
}
