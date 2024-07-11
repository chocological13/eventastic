package com.miniproject.eventastic.responses;

import com.miniproject.eventastic.event.entity.dto.EventResponseDto;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.domain.Page;
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

  public static <T> ResponseEntity<Response<T>> successfulResponse(String statusMessage) {
    return successfulResponse(statusMessage, null);
  }

  public static <T> ResponseEntity<Response<T>> successfulResponse(T data) {
    return successfulResponse(HttpStatus.OK.value(), "Process has executed successfully", data);
  }

  // Region - Events Page

  public static ResponseEntity<Response<Map<String, Object>>> responseMapper(int statusCode, String message,
      Page<EventResponseDto> eventPage) {
    if (eventPage != null) {
      Map<String, Object> response = new HashMap<>();
      response.put("currentPage", eventPage.getNumber());
      response.put("totalPages", eventPage.getTotalPages());
      response.put("totalElements", eventPage.getTotalElements());
      response.put("events", eventPage.getContent());
      return Response.successfulResponse(statusCode, message, response);
    } else {
      return Response.failedResponse("No events found!");
    }
  }

}
