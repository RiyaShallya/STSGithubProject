package com.jts.rediscache.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import com.jts.rediscache.Order;

public interface OrderRepository extends JpaRepository< Order, Integer> {

}
