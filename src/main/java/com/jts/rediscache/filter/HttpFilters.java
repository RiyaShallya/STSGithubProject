package com.jts.rediscache.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.annotation.Order;
import javax.crypto.Cipher;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Order(1) // Lower value, higher priority
@Component
public class HttpFilters implements Filter {

	Logger LOGGER = LoggerFactory.getLogger(HttpFilters.class);
	
	private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "6H6SIdt3MdgaaO0MN/Qcrw=="; // should have only 24 letter
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static SecretKey getSecretKey() { 
    	return new SecretKeySpec(Base64.getDecoder().decode(SECRET_KEY), ALGORITHM);
    }
    
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestURI = httpRequest.getRequestURI();


	        if ("/api/save".equals(requestURI)) {
	            LOGGER.info("[HttpFilters] - Inside doFilter method - save");

	            try {
	                // Step 1: Extract encrypted data (assuming JSON payload in POST request)
	                String encryptedData = null;

	                if ("POST".equalsIgnoreCase(httpRequest.getMethod())) {
	                    StringBuilder stringBuilder = new StringBuilder();
	                    String line;
	                    try (BufferedReader reader = httpRequest.getReader()) {
	                        while ((line = reader.readLine()) != null) {
	                            stringBuilder.append(line);
	                        }
	                    }
	                    String requestBody = stringBuilder.toString();
	                    // Extract the encryptedPayload from the JSON body
	                    encryptedData = objectMapper.readTree(requestBody).get("encryptedPayload").asText();
	                } else {
	                    // For GET or other methods, get it from the parameter
	                    encryptedData = httpRequest.getParameter("encryptedPayload");
	                }

	                if (encryptedData == null || encryptedData.isEmpty()) {
	                    LOGGER.error("Encrypted data is missing or empty");
	                    response.getWriter().write("Bad Request: Missing encrypted data");
	                    return;
	                }

	                // Step 2: Decrypt the data
	                String decryptedData = decryptData(encryptedData);

	                // Set the decrypted data as a request attribute for use in the controller
	                httpRequest.setAttribute("decryptedPayload", decryptedData);

	                // Continue with the filter chain
	                chain.doFilter(request, response);

	            } catch (Exception e) {
	                LOGGER.error("Error processing the request: ", e);
	                response.getWriter().write("Internal Server Error");
	            }
	        } else {
	        	LOGGER.info("[HttpFilters] - Inside doFilter method - encrypt/decrypt ");
	            // For other endpoints, just continue with the chain
	            chain.doFilter(request, response);
	        }
	        
	    }

	    private String decryptData(String encryptedData) throws Exception {
	        Cipher cipher = Cipher.getInstance(ALGORITHM);
	        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
	        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
	        byte[] decryptedData = cipher.doFinal(decodedData);
	        return new String(decryptedData);
	    }
	}