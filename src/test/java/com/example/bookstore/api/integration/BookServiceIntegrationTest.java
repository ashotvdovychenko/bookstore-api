package com.example.bookstore.api.integration;

import static com.example.bookstore.api.util.ResponseUtils.NOT_FOUND;
import static com.example.bookstore.api.util.ResponseUtils.OK;
import static com.example.bookstore.api.utils.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.citrusframework.actions.ExecuteSQLAction.Builder.sql;

import com.example.bookstore.api.config.CitrusTestConfiguration;
import com.example.bookstore.api.util.ResponseUtils;
import com.example.bookstore.proto.*;
import java.io.File;
import java.util.List;
import javax.sql.DataSource;
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
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@CitrusSpringSupport
@ContextConfiguration(classes = {CitrusSpringConfig.class, CitrusTestConfiguration.class})
public class BookServiceIntegrationTest {

  @CitrusResource private TestActionRunner runner;

  @Autowired private DataSource dataSource;

  @Autowired private ReactorBookServiceGrpc.ReactorBookServiceStub client;

  @AfterEach
  public void clean() {
    runner.$(sql().dataSource(dataSource).statement("delete from BOOKS"));
  }

  @Container
  private static final ComposeContainer dockerComposeContainer =
      new ComposeContainer(new File("docker-compose.yml"))
          .withExposedService("api", 9090)
          .withExposedService("postgres", 5432);

  @Test
  @CitrusTest
  public void getAll() {
    runner.$(sql(dataSource).sqlResource("create-book.sql"));

    var actual = client.getAll(GET_ALL_BOOKS).block();
    var expected =
        GetAllResponse.newBuilder()
            .addAllBooks(List.of(PROTO_BOOK))
            .setResponse(ResponseUtils.OK)
            .build();

    assertThat(actual).isEqualTo(expected);
    runner.$(
        sql(dataSource)
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
  public void getByIdIfPresent() {
    runner.$(sql(dataSource).sqlResource("create-book.sql"));

    var actual = client.getById(BOOK_ID).block();
    var expected = BookResponse.newBuilder().setResponse(OK).setBook(PROTO_BOOK).build();

    assertThat(actual).isEqualTo(expected);
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
    var expected = BookResponse.newBuilder().setResponse(NOT_FOUND).build();

    assertThat(actual).isEqualTo(expected);
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
    assertThat(actual.getBook()).isNotNull();
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
  public void updateIfPresent() {
    runner.$(sql(dataSource).sqlResource("create-book.sql"));

    var actual = client.update(PROTO_BOOK).block();
    var expected = UpdateResponse.newBuilder().setResponse(OK).build();

    assertThat(actual).isEqualTo(expected);
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
    var expected = UpdateResponse.newBuilder().setResponse(NOT_FOUND).build();

    assertThat(actual).isEqualTo(expected);
    runner.$(
        sql()
            .dataSource(dataSource)
            .query()
            .statement("select count(*) as ROWCOUNT from BOOKS")
            .validate("ROWCOUNT", "0"));
  }

  @Test
  @CitrusTest
  public void deleteIfPresent() {
    runner.$(sql(dataSource).sqlResource("create-book.sql"));

    var actual = client.delete(BookId.newBuilder().setId(ID).build()).block();
    var expected = DeleteResponse.newBuilder().setBook(PROTO_BOOK).setResponse(OK).build();

    assertThat(actual).isEqualTo(expected);
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
    var expected = DeleteResponse.newBuilder().setResponse(NOT_FOUND).build();

    assertThat(actual).isEqualTo(expected);
    runner.$(
        sql()
            .dataSource(dataSource)
            .query()
            .statement("select count(*) as ROWCOUNT from BOOKS where ID = '%s'".formatted(ID))
            .validate("ROWCOUNT", "0"));
  }
}
