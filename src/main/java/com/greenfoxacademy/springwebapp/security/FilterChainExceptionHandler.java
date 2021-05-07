package com.greenfoxacademy.springwebapp.security;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class FilterChainExceptionHandler extends OncePerRequestFilter {

  private static final Logger logger = Logger.getLogger(FilterChainExceptionHandler.class);

  @Autowired
  @Qualifier("handlerExceptionResolver")
  private HandlerExceptionResolver resolver;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

    try {
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      logger.warn(e);
      resolver.resolveException(request, response, null, e);
    }
  }
}
