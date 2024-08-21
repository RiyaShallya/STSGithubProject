package com.jts.rediscache.repo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jts.rediscache.OrderNotFoundException;
import com.jts.rediscache.controller.RedisController;

@Service
public class OrderService {
	
    @Autowired
    private OrderRepository repository;
    Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    public Order saveData(Order data) {
    	LOGGER.info("[OrderService] - Inside save method");
        return repository.save(data);
    
    }  
}
	
	
