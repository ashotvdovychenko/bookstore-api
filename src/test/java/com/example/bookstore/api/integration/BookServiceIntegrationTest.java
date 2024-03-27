package com.example.bookstore.api.integration;

import com.example.bookstore.api.config.CitrusTestConfiguration;
import com.example.bookstore.api.testcontainers.Containers;
import com.example.bookstore.proto.*;
import org.citrusframework.TestActionRunner;
import org.citrusframework.annotations.CitrusResource;
import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.config.CitrusSpringConfig;
import org.citrusframework.junit.jupiter.spring.CitrusSpringSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

import static com.example.bookstore.api.util.ResponseUtils.NOT_FOUND;
import static com.example.bookstore.api.util.ResponseUtils.OK;
import static com.example.bookstore.api.utils.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.actions.ExecuteSQLAction.Builder.sql;

@Testcontainers
@SpringBootTest
@CitrusSpringSupport
@ContextConfiguration(classes = {CitrusSpringConfig.class, CitrusTestConfiguration.class})
public class BookServiceIntegrationTest extends Containers {

  @CitrusResource private TestActionRunner runner;

  @Autowired private DataSource dataSource;

  @Autowired private ReactorBookServiceGrpc.ReactorBookServiceStub client;

  @AfterEach
  public void clean() {
    runner.$(sql().dataSource(dataSource).statement("delete from BOOKS"));
  }

  @Test
  @CitrusTest
  @Sql("/create-book.sql")
  public void getAll() {
    var actual = client.getAll(GET_ALL_BOOKS).blockFirst();

    assertThat(actual).isEqualTo(PROTO_BOOK);
    runner.$(
        sql()
            .dataSource(dataSource)
            .query()
            .statement("select * from BOOKS")
            .validate("id", ID)
            .validate("title", TITLE)
            .validate("author", AUTHOR)
            .validate("isbn", ISBN)
            .validate("quantity", String.valueOf(QUANTITY)));
  }

  @Test
  @CitrusTest
  @Sql("/create-book.sql")
  public void getByIdIfPresent() {
    var actual = client.getById(BOOK_ID).block();

    assertThat(actual)
        .isEqualTo(GetResponse.newBuilder().setResponse(OK).setBook(PROTO_BOOK).build());
    runner.$(
        sql()
            .dataSource(dataSource)
            .query()
            .statement("select * from BOOKS where ID = %s".formatted(ID))
            .validate("id", ID)
            .validate("title", TITLE)
            .validate("author", AUTHOR)
            .validate("isbn", ISBN)
            .validate("quantity", String.valueOf(QUANTITY)));
  }

  @Test
  @CitrusTest
  public void getByIdIfNotFound() {
    var actual = client.getById(getBookId(ID)).block();

    assertThat(actual).isEqualTo(GetResponse.newBuilder().setResponse(NOT_FOUND).build());
    runner.$(
        sql()
            .dataSource(dataSource)
            .query()
            .statement("select count(*) as ROWCOUNT from BOOKS")
            .validate("ROWCOUNT", "0"));
  }

  @Test
  @CitrusTest
  public void create() {
    var actual = client.create(CREATE_BOOK).block();

    assertThat(actual).isNotNull();
    runner.$(
        sql()
            .dataSource(dataSource)
            .query()
            .statement("select * from BOOKS")
            .validate("title", TITLE)
            .validate("author", AUTHOR)
            .validate("isbn", ISBN)
            .validate("quantity", String.valueOf(QUANTITY)));
  }

  @Test
  @CitrusTest
  @Sql("/create-book.sql")
  public void updateIfPresent() {
    var actual = client.update(PROTO_BOOK).block();

    assertThat(actual).isEqualTo(UpdateResponse.newBuilder().setResponse(OK).build());
    runner.$(
        sql()
            .dataSource(dataSource)
            .query()
            .statement("select * from BOOKS where ID = %s".formatted(ID))
            .validate("id", ID)
            .validate("title", TITLE)
            .validate("author", AUTHOR)
            .validate("isbn", ISBN)
            .validate("quantity", String.valueOf(QUANTITY)));
  }

  @Test
  @CitrusTest
  public void updateIfNotFound() {
    var actual = client.update(getProtoBook(ID)).block();

    assertThat(actual).isEqualTo(UpdateResponse.newBuilder().setResponse(NOT_FOUND).build());
    runner.$(
        sql()
            .dataSource(dataSource)
            .query()
            .statement("select count(*) as ROWCOUNT from BOOKS")
            .validate("ROWCOUNT", "0"));
  }

  @Test
  @CitrusTest
  @Sql("/create-book.sql")
  public void deleteIfPresent() {
    var actual = client.delete(BookId.newBuilder().setId(ID).build()).block();

    assertThat(actual)
        .isEqualTo(DeleteResponse.newBuilder().setBook(PROTO_BOOK).setResponse(OK).build());
    runner.$(
        sql()
            .dataSource(dataSource)
            .query()
            .statement("select count(*) as ROWCOUNT from BOOKS where ID = '%s'".formatted(ID))
            .validate("ROWCOUNT", "0"));
  }

  @Test
  @CitrusTest
  public void deleteIfNotFound() {
    var actual = client.delete(BookId.newBuilder().setId(ID).build()).block();

    assertThat(actual).isEqualTo(DeleteResponse.newBuilder().setResponse(NOT_FOUND).build());
    runner.$(
        sql()
            .dataSource(dataSource)
            .query()
            .statement("select count(*) as ROWCOUNT from BOOKS where ID = '%s'".formatted(ID))
            .validate("ROWCOUNT", "0"));
  }
}
