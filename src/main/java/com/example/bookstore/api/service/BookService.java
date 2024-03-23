package com.example.bookstore.api.service;

import com.example.bookstore.api.mapper.BookMapper;
import com.example.bookstore.api.repository.BookRepository;
import com.example.bookstore.api.util.IdGenerator;
import com.example.bookstore.api.util.ResponseUtils;
import com.example.bookstore.proto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@RequiredArgsConstructor
public class BookService extends ReactorBookServiceGrpc.BookServiceImplBase {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  private final IdGenerator<String> idGenerator;

  @Override
  public Flux<Book> getAll(Mono<GetAllBooks> request) {
    return bookRepository.findAll().map(bookMapper::toProto);
  }

  @Override
  public Mono<GetResponse> getById(Mono<BookId> request) {
    return request
        .map(BookId::getId)
        .flatMap(bookRepository::findById)
        .map(bookMapper::toProto)
        .map(book -> GetResponse.newBuilder().setBook(book).setResponse(ResponseUtils.OK).build())
        .defaultIfEmpty(GetResponse.newBuilder().setResponse(ResponseUtils.NOT_FOUND).build())
        .onErrorResume(
            Throwable.class,
            error ->
                Mono.just(
                    GetResponse.newBuilder()
                        .setResponse(ResponseUtils.error(error.getMessage()))
                        .build()));
  }

  @Override
  public Mono<Book> create(Mono<CreateBook> request) {
    return request
        .map(bookMapper::toModel)
        .map(book -> book.setId(idGenerator.generate()))
        .flatMap(bookRepository::save)
        .map(bookMapper::toProto)
        .single();
  }

  @Override
  public Mono<UpdateResponse> update(Mono<Book> request) {
    return request
        .map(Book::getId)
        .map(BookId::getId)
        .flatMap(bookRepository::findById)
        .zipWith(request)
        .map(Tuple2::getT2)
        .map(bookMapper::toModel)
        .flatMap(bookRepository::save)
        .map(book -> UpdateResponse.newBuilder().setResponse(ResponseUtils.OK).build())
        .defaultIfEmpty(UpdateResponse.newBuilder().setResponse(ResponseUtils.NOT_FOUND).build())
        .onErrorResume(
            Throwable.class,
            error ->
                Mono.just(
                    UpdateResponse.newBuilder()
                        .setResponse(ResponseUtils.error(error.getMessage()))
                        .build()));
  }

  @Override
  public Mono<DeleteResponse> delete(Mono<BookId> request) {
    return request
        .map(BookId::getId)
        .flatMap(bookRepository::findById)
        .flatMap(book -> bookRepository.deleteById(book.getId()).thenReturn(book))
        .map(bookMapper::toProto)
        .map(
            book -> DeleteResponse.newBuilder().setResponse(ResponseUtils.OK).setBook(book).build())
        .defaultIfEmpty(DeleteResponse.newBuilder().setResponse(ResponseUtils.NOT_FOUND).build())
        .onErrorResume(
            Throwable.class,
            error ->
                Mono.just(
                    DeleteResponse.newBuilder()
                        .setResponse(ResponseUtils.error(error.getMessage()))
                        .build()));
  }
}
