package com.example.bookstore.api.service;

import com.example.bookstore.api.mapper.BookMapper;
import com.example.bookstore.api.repository.BookRepository;
import com.example.bookstore.proto.*;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BookService extends ReactorBookServiceGrpc.BookServiceImplBase {

  private final BookRepository bookRepository;

  @Override
  public Flux<Book> getAll(Mono<GetAllBooks> request) {
    return bookRepository.findAll().map(BookMapper.INSTANCE::toProto);
  }

  @Override
  public Mono<GetResponse> getById(Mono<BookId> request) {
    return bookRepository
        .findById(getUuid(request))
        .map(BookMapper.INSTANCE::toProto)
        .map(book -> GetResponse.newBuilder().setBook(book).setStatus(ResponseStatus.OK).build())
        .defaultIfEmpty(GetResponse.newBuilder().setStatus(ResponseStatus.NOT_FOUND).build());
  }

  @Override
  public Mono<Book> create(Mono<CreateBook> request) {
    return bookRepository
        .saveAll(request.map(BookMapper.INSTANCE::createToModel))
        .map(BookMapper.INSTANCE::toProto)
        .single();
  }

  @Override
  public Mono<UpdateResponse> update(Mono<Book> request) {
    return bookRepository
        .findById(getUuid(request.map(Book::getId)))
        .then(request)
        .flatMap(book -> bookRepository.save(BookMapper.INSTANCE.protoToModel(book)))
        .thenReturn(UpdateResponse.newBuilder().setStatus(ResponseStatus.OK).build())
        .defaultIfEmpty(UpdateResponse.newBuilder().setStatus(ResponseStatus.NOT_FOUND).build());
  }

  @Override
  public Mono<DeleteResponse> delete(Mono<BookId> request) {
    return bookRepository
        .findById(getUuid(request))
        .flatMap(book -> bookRepository.deleteById(book.getId()).thenReturn(book))
        .map(BookMapper.INSTANCE::toProto)
        .map(book -> DeleteResponse.newBuilder().setStatus(ResponseStatus.OK).setBook(book).build())
        .defaultIfEmpty(DeleteResponse.newBuilder().setStatus(ResponseStatus.NOT_FOUND).build());
  }

  private static Mono<UUID> getUuid(Mono<BookId> request) {
    return request.map(BookId::getId).map(UUID::fromString);
  }
}
