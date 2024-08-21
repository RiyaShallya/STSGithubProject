package com.jts.rediscache.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jts.rediscache.filter.HttpFilters;
import com.jts.rediscache.repo.Order;
import com.jts.rediscache.repo.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Validated
public class RedisController {
	
	 	private final ObjectMapper objectMapper = new ObjectMapper();
	 	Logger LOGGER = LoggerFactory.getLogger(RedisController.class);
	 
	    @Autowired
	    private OrderService service;

	    @PostMapping("/encrypt")
	    public ResponseEntity<String> encryptData(@RequestBody String data) {
	    	LOGGER.info("[RedisController] - Inside encryptData method");
	    	try {
	            String encryptedData = EncryptionUtil.encrypt(data);
	            return ResponseEntity.ok(encryptedData);
	        } catch (Exception e) {
	            return ResponseEntity.status(500).body("Encryption error: " + e.getMessage());
	        }
	    }

	    @PostMapping("/decrypt")
	    public ResponseEntity<String> decryptData(@RequestBody String encryptedData) {
	    	LOGGER.info("[RedisController] - Inside decryptData method");
	    	try {
	            String decryptedData = EncryptionUtil.decrypt(encryptedData);
	            return ResponseEntity.ok(decryptedData);
	        } catch (Exception e) {
	            return ResponseEntity.status(500).body("Decryption error: " + e.getMessage());
	        }
	    }

	    @PostMapping("/save")
	    public ResponseEntity<String> saveData(@RequestBody @Valid Order order ) {
	    	try { 
	    		LOGGER.info("[RedisController] - Inside saveData method");
	            service.saveData(order);
	            return ResponseEntity.ok("Data saved successfully!");
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.badRequest().body("Invalid encrypted payload: " + e.getMessage());
	        }
	    }
}