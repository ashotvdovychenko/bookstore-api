package com.example.bookstore.api.testcontainers;

import com.google.common.io.Resources;
import lombok.SneakyThrows;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public abstract class Containers {

  public static PostgreSQLContainer<?> POSTGRES_CONTAINER = getPostgreSQLContainer();

  public static GenericContainer<?> APPLICATION_CONTAINER = getApplicationContainer();
  public static PostgreSQLContainer<?> getPostgreSQLContainer() {
    var container =
        new PostgreSQLContainer<>("postgres:16.2")
            .withPassword("test")
            .withUsername("test")
            .withDatabaseName("test")
            .withExposedPorts(5432)
            .withInitScript("postgres/init_script.sql");
    return container;
  }

  public static GenericContainer<?> getApplicationContainer() {
    var container =
        new GenericContainer<>()
            .withExposedPorts(8080)
            .withEnv(
                Map.of(
                    "SPRING_R2DBC_URL",
                    "jdbc:tc:postgresql://localhost:5432/test",
                    "SPRING_R2DBC_PASSWORD",
                    "test",
                    "SPRING_R2DBC_USERNAME",
                    "test"))
                .waitingFor(Wait.forLogMessage(".*Started Starter.*\n", 1));
    return container;
  }

  @SneakyThrows
  private static String getInitScript() {
    return Files.readString(Paths.get(Resources.getResource("postgres/init_script.sql").toURI()));
  }

  static {
    POSTGRES_CONTAINER.start();
    APPLICATION_CONTAINER.start();
  }
}
