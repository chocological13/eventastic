package com.miniproject.eventastic.responses;

import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@ToString
public class Response<T> {

  private int statusCode;
  private boolean success = false;
  private String statusMessage;
  private T data;

  // constructor
  public Response(int statusCode, String statusMessage) {
    this.statusCode = statusCode;
    this.statusMessage = statusMessage;
    if (statusCode == HttpStatus.OK.value()) {
      success = true;
    }
  }

  // Region - failed responses
  // ? this is the base failedResponse method and all other ones are gonna be recursively overloaded from this
  public static <T> ResponseEntity<Response<T>> failedResponse(int statusCode, String statusMessage, T data) {
    Response<T> response = new Response<>(statusCode, statusMessage);
    response.setSuccess(false);
    response.setData(data);
    return ResponseEntity.status(statusCode).body(response);
  }

  // overloaded method
  public static ResponseEntity<Response<Object>> failedResponse(int statusCode, String statusMessage) {
    return failedResponse(statusCode, statusMessage, null);
  }

  public static <T> ResponseEntity<Response<T>> failedResponse(int statusCode, T data) {
    return failedResponse(statusCode, "Process has failed to execute", data);
  }

  public static <T> ResponseEntity<Response<T>> failedResponse(String statusMessage, T data) {
    return failedResponse(HttpStatus.BAD_REQUEST.value(), statusMessage, data);
  }

  public static<T> ResponseEntity<Response<T>> failedResponse(String statusMessage) {
    return failedResponse(HttpStatus.BAD_REQUEST.value(), statusMessage, null);
  }

  public static <T> ResponseEntity<Response<T>> failedResponse(T data) {
    return failedResponse(HttpStatus.BAD_REQUEST.value(), "Process has failed to execute", data);
  }

  // Region - successful response
  // ? base method
  public static <T> ResponseEntity<Response<T>> successfulResponse(int statusCode, String statusMessage, T data) {
    Response<T> response = new Response<>(statusCode, statusMessage);
    response.setSuccess(true);
    response.setData(data);
    return ResponseEntity.status(statusCode).body(response);
  }

  public static <T> ResponseEntity<Response<T>> successfulResponse(String statusMessage, T data) {
    return successfulResponse(HttpStatus.OK.value(), statusMessage, data);
  }

  public static <T> ResponseEntity<Response<T>> successfulResponse(int statusCode, T data) {
    return successfulResponse(statusCode, "Process has executed successfully", data);
  }

  public static <T> ResponseEntity<Response<T>> successfulResponse(int statusCode, String statusMessage) {
    return successfulResponse(statusCode, statusMessage, null);
  }

  public static <T> ResponseEntity<Response<T>> successfulResponse(T data) {
    return successfulResponse(HttpStatus.OK.value(), "Process has executed successfully", data);
  }

}
