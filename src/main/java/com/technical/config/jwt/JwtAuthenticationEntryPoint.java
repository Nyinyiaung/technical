package com.technical.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technical.commonutil.DateUtil;
import com.technical.config.MessageConfig;
import com.technical.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final MessageConfig messageConfig;

  @Override
  public void commence(HttpServletRequest httpServletRequest,
      HttpServletResponse response, AuthenticationException e)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setDateFormat(new SimpleDateFormat(DateUtil.RESPONSE_TIMESTAMP_DATE_FORMAT));

    response.setContentType("application/json");
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.getWriter().write(mapper.writeValueAsString(
            new ErrorResponse(HttpStatus.UNAUTHORIZED.getReasonPhrase()
                    , messageConfig.getMessage("invalid.jwt.token"), e.getMessage())));
  }
}
