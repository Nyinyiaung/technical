package com.technical.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuccessResponse {
  private String message;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object data;
}
