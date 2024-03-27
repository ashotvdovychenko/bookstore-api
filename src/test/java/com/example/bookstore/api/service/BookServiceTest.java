package com.example.bookstore.api.service;

import static com.example.bookstore.api.utils.TestData.*;
import static org.mockito.Mockito.*;

import com.example.bookstore.api.mapper.BookMapper;
import com.example.bookstore.api.repository.BookRepository;
import com.example.bookstore.api.util.IdGenerator;
import com.example.bookstore.api.util.ResponseUtils;
import com.example.bookstore.proto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
  @Mock private BookRepository bookRepository;

  @Mock private BookMapper bookMapper;

  @Mock private IdGenerator<String> idGenerator;

  @InjectMocks private BookService bookService;

  @Test
  void getAll() {
    var firstId = "FirstId";
    var secondId = "SecondId";
    var firstBook = getBook(firstId);
    var secondBook = getBook(secondId);
    var firstProtoBook = getProtoBook(firstId);
    var secondProtoBook = getProtoBook(secondId);

    when(bookRepository.findAll()).thenReturn(Flux.just(firstBook, secondBook));
    when(bookMapper.toProto(firstBook)).thenReturn(firstProtoBook);
    when(bookMapper.toProto(secondBook)).thenReturn(secondProtoBook);

    var actual = bookService.getAll(Mono.just(GetAllBooks.newBuilder().build()));

    StepVerifier.create(actual)
        .expectNext(firstProtoBook, secondProtoBook)
        .expectComplete()
        .verify();
  }

  @Test
  void getByIdIfPresent() {
    when(bookRepository.findById(ID)).thenReturn(Mono.just(BOOK));
    when(bookMapper.toProto(BOOK)).thenReturn(PROTO_BOOK);

    var actual = bookService.getById(Mono.just(BookId.newBuilder().setId(ID).build()));
    var expected =
        GetResponse.newBuilder().setBook(PROTO_BOOK).setResponse(ResponseUtils.OK).build();

    StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
  }

  @Test
  void getByIdIfNotFound() {
    when(bookRepository.findById(ID)).thenReturn(Mono.empty());

    var actual = bookService.getById(Mono.just(BookId.newBuilder().setId(ID).build()));
    var expected = GetResponse.newBuilder().setResponse(ResponseUtils.NOT_FOUND).build();

    StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
  }

  @Test
  void getByIdIfError() {
    var message = "Exception Message";
    var exception = new RuntimeException(message);
    when(bookRepository.findById(ID)).thenThrow(exception);

    var actual = bookService.getById(Mono.just(BookId.newBuilder().setId(ID).build()));
    var expected = GetResponse.newBuilder().setResponse(ResponseUtils.error(message)).build();

    StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
  }

  @Test
  void create() {
    when(bookRepository.save(BOOK)).thenReturn(Mono.just(BOOK));
    when(bookMapper.toProto(BOOK)).thenReturn(PROTO_BOOK);
    when(bookMapper.toModel(CREATE_BOOK)).thenReturn(BOOK);
    when(idGenerator.generate()).thenReturn(ID);

    var actual = bookService.create(Mono.just(CREATE_BOOK));

    StepVerifier.create(actual).expectNext(PROTO_BOOK).expectComplete().verify();
  }

  @Test
  void updateIfPresent() {
    when(bookRepository.findById(ID)).thenReturn(Mono.just(BOOK));
    when(bookMapper.toModel(PROTO_BOOK)).thenReturn(BOOK);
    when(bookRepository.save(BOOK)).thenReturn(Mono.just(BOOK));

    var actual = bookService.update(Mono.just(PROTO_BOOK));
    var expected = UpdateResponse.newBuilder().setResponse(ResponseUtils.OK).build();

    StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
  }

  @Test
  void updateIfNotFound() {
    when(bookRepository.findById(ID)).thenReturn(Mono.empty());

    var actual = bookService.update(Mono.just(PROTO_BOOK));
    var expected = UpdateResponse.newBuilder().setResponse(ResponseUtils.NOT_FOUND).build();

    StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
  }

  @Test
  void updateIfError() {
    var message = "Exception Message";
    var exception = new RuntimeException(message);
    when(bookRepository.findById(ID)).thenThrow(exception);

    var actual = bookService.update(Mono.just(PROTO_BOOK));
    var expected = UpdateResponse.newBuilder().setResponse(ResponseUtils.error(message)).build();

    StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
  }

  @Test
  void deleteIfPresent() {
    when(bookRepository.findById(ID)).thenReturn(Mono.just(BOOK));
    when(bookRepository.deleteById(ID)).thenReturn(Mono.empty().then());
    when(bookMapper.toProto(BOOK)).thenReturn(PROTO_BOOK);

    var actual = bookService.delete(Mono.just(BookId.newBuilder().setId(ID).build()));
    var expected =
        DeleteResponse.newBuilder().setBook(PROTO_BOOK).setResponse(ResponseUtils.OK).build();

    StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
  }

  @Test
  void deleteIfNotFound() {
    when(bookRepository.findById(ID)).thenReturn(Mono.empty());

    var actual = bookService.delete(Mono.just(BookId.newBuilder().setId(ID).build()));
    var expected = DeleteResponse.newBuilder().setResponse(ResponseUtils.NOT_FOUND).build();

    StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
  }

  @Test
  void deleteIfError() {
    var message = "Exception Message";
    var exception = new RuntimeException(message);
    when(bookRepository.findById(ID)).thenThrow(exception);

    var actual = bookService.delete(Mono.just(BookId.newBuilder().setId(ID).build()));
    var expected = DeleteResponse.newBuilder().setResponse(ResponseUtils.error(message)).build();

    StepVerifier.create(actual).expectNext(expected).expectComplete().verify();
  }
}
