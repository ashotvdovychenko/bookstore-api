package com.example.bookstore.api.config;

import com.example.bookstore.proto.ReactorBookServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class CitrusTestConfiguration {

  @Bean
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .type(DriverManagerDataSource.class)
        .password("localdb")
        .username("localdb")
        .driverClassName("org.postgresql.Driver")
        .url("jdbc:postgresql://localhost:5432/localdb")
        .build();
  }

  @Bean
  public ReactorBookServiceGrpc.ReactorBookServiceStub grpcClient() {
    return ReactorBookServiceGrpc.newReactorStub(
        ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build());
  }
}
