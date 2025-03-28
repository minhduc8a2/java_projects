package com.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entities.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
