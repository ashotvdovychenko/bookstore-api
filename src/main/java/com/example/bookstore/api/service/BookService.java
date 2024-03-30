package com.example.bookstore.api.service;

import com.example.bookstore.api.mapper.BookMapper;
import com.example.bookstore.api.repository.BookRepository;
import com.example.bookstore.api.util.IdGenerator;
import com.example.bookstore.api.util.ResponseUtils;
import com.example.bookstore.proto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class BookService extends ReactorBookServiceGrpc.BookServiceImplBase {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  private final IdGenerator<String> idGenerator;

  @Override
  public Mono<GetAllResponse> getAll(Mono<GetAllBooks> request) {
    return bookRepository
        .findAll()
        .log()
        .map(bookMapper::toProto)
        .collectList()
        .map(
            books ->
                GetAllResponse.newBuilder()
                    .addAllBooks(books)
                    .setResponse(ResponseUtils.OK)
                    .build())
        .doOnError(error -> log.error(error.getMessage()))
        .onErrorResume(
            Throwable.class,
            error ->
                Mono.just(
                    GetAllResponse.newBuilder()
                        .setResponse(ResponseUtils.error(error.getMessage()))
                        .build()));
  }

  @Override
  public Mono<BookResponse> getById(Mono<BookId> request) {
    return request
        .map(BookId::getId)
        .flatMap(bookRepository::findById)
        .map(bookMapper::toProto)
        .map(book -> BookResponse.newBuilder().setBook(book).setResponse(ResponseUtils.OK).build())
        .defaultIfEmpty(BookResponse.newBuilder().setResponse(ResponseUtils.NOT_FOUND).build())
        .doOnError(error -> log.error(error.getMessage()))
        .onErrorResume(
            Throwable.class,
            error ->
                Mono.just(
                    BookResponse.newBuilder()
                        .setResponse(ResponseUtils.error(error.getMessage()))
                        .build()));
  }

  @Override
  public Mono<BookResponse> create(Mono<CreateBook> request) {
    return request
        .log()
        .map(bookMapper::toModel)
        .map(book -> book.setId(idGenerator.generate()))
        .flatMap(bookRepository::save)
        .map(bookMapper::toProto)
        .map(book -> BookResponse.newBuilder().setBook(book).setResponse(ResponseUtils.OK).build())
        .single()
        .doOnError(error -> log.error(error.getMessage()))
        .onErrorResume(
            Throwable.class,
            error ->
                Mono.just(
                    BookResponse.newBuilder()
                        .setResponse(ResponseUtils.error(error.getMessage()))
                        .build()));
  }

  @Override
  public Mono<UpdateResponse> update(Mono<Book> request) {
    return request
        .log()
        .map(book -> book.getId().getId())
        .flatMap(bookRepository::findById)
        .zipWith(request)
        .map(Tuple2::getT2)
        .map(book -> bookMapper.toModel(book).setNew(false))
        .flatMap(bookRepository::save)
        .map(book -> UpdateResponse.newBuilder().setResponse(ResponseUtils.OK).build())
        .defaultIfEmpty(UpdateResponse.newBuilder().setResponse(ResponseUtils.NOT_FOUND).build())
        .doOnError(error -> log.error(error.getMessage()))
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
        .log()
        .map(BookId::getId)
        .flatMap(bookRepository::findById)
        .flatMap(book -> bookRepository.deleteById(book.getId()).thenReturn(book))
        .map(bookMapper::toProto)
        .map(
            book -> DeleteResponse.newBuilder().setResponse(ResponseUtils.OK).setBook(book).build())
        .defaultIfEmpty(DeleteResponse.newBuilder().setResponse(ResponseUtils.NOT_FOUND).build())
        .doOnError(error -> log.error(error.getMessage()))
        .onErrorResume(
            Throwable.class,
            error ->
                Mono.just(
                    DeleteResponse.newBuilder()
                        .setResponse(ResponseUtils.error(error.getMessage()))
                        .build()));
  }
}
