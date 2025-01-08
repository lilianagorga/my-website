package dev.lilianagorga.mywebsite.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ErrorMessage {
  private int statusCode;
  private Date timestamp;
  private String message;
  private Map<String, String> details;
}