package com.example.bookstore.api.util;

import com.example.bookstore.proto.Response;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResponseUtils {
  public Response OK = Response.newBuilder().setStatus(Response.Status.OK).build();
  public Response NOT_FOUND = Response.newBuilder().setStatus(Response.Status.NOT_FOUND).build();

  public Response error(String description) {
    return Response.newBuilder()
        .setStatus(Response.Status.ERROR)
        .setDescription(description)
        .build();
  }
}
