package com.example.bookstore.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bookstore.api.TestData;
import org.junit.jupiter.api.Test;

class BookMapperTest {

  @Test
  void toProto() {
    assertThat(BookMapper.INSTANCE.toProto(TestData.BOOK)).isEqualTo(TestData.PROTO_BOOK);
  }

  @Test
  void toModelFromCreateBook() {
    assertThat(BookMapper.INSTANCE.toModel(TestData.CREATE_BOOK)).isEqualTo(TestData.getBook(null));
  }

  @Test
  void toModelFromProtoBook() {
    assertThat(BookMapper.INSTANCE.toModel(TestData.PROTO_BOOK)).isEqualTo(TestData.BOOK);
  }
}
