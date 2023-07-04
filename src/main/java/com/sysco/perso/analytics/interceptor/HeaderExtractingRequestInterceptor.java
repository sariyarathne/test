package com.sysco.perso.analytics.interceptor;


import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HeaderExtractingRequestInterceptor implements HandlerInterceptor {

  private static final String CORRELATION_ID = "Syy-Correlation-Id";
  private static final String REQUEST_ID = "Syy-Request-Id";

  /**
   * @param request  : [HttpServletRequest] HTTP Request object
   * @param response : [HttpServletResponse] HTTP Response object
   * @param handler  : [Object] Chosen handler to execute
   * @return : [Boolean] Status of the pre handler execution
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String correlationId = request.getHeader(CORRELATION_ID);
    String requestId = request.getHeader(REQUEST_ID);
    MDC.clear();
    MDC.put(CORRELATION_ID, correlationId);
    ThreadLocalCache.put(CORRELATION_ID, correlationId);
    MDC.put(REQUEST_ID, requestId);
    ThreadLocalCache.put(REQUEST_ID, requestId);
    return true;
  }
}
