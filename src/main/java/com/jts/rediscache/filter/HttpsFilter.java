package com.jts.rediscache.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
//import org.springframework.core.annotation.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jts.rediscache.repo.Order;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

//@Order(2)
@Component
public class HttpsFilter implements Filter {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpsFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
	
		HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        if ("/api/save".equals(requestURI)) {
            try {
                // Step 1: Retrieve decrypted payload from request attribute
                String decryptedPayload = (String) httpRequest.getAttribute("decryptedPayload");

                if (decryptedPayload == null) {
                    LOGGER.error("Decrypted payload not found");
                    response.getWriter().write("Bad Request: Decrypted payload not found");
                    return;
                }

                // Step 2: Replace the request body with the decrypted payload
                HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest) {
                    @Override
                    public BufferedReader getReader() throws IOException {
                        return new BufferedReader(new StringReader(decryptedPayload));
                    }

                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decryptedPayload.getBytes());

                        return new ServletInputStream() {
                            @Override
                            public boolean isFinished() {
                                return byteArrayInputStream.available() == 0;
                            }

                            @Override
                            public boolean isReady() {
                                return true;
                            }

                            @Override
                            public void setReadListener(ReadListener readListener) {
                                // No-op implementation for now
                            }

                            @Override
                            public int read() throws IOException {
                                return byteArrayInputStream.read();
                            }
                        };
                    }
                };

                // Continue with the filter chain with the modified request
                chain.doFilter(requestWrapper, response);

            } catch (Exception e) {
                LOGGER.error("Error processing the request in HttpsFilter: ", e);
                response.getWriter().write("Internal Server Error");
            }
        } else {
            // For other endpoints, continue with the chain
            chain.doFilter(request, response);
        }
    }
}
