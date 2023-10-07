package com.Veggie.Cart.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.Veggie.Cart.Entity.Cart;

public interface CartRepo extends JpaRepository<Cart,Integer>{
	List<Cart> findByEmail(String email);
//	@Modifying
//    @Query("DELETE FROM Cart c WHERE c.name = :name")
//    void deleteByName(@Param("name") String name);
    boolean deleteByName(String name);
}
