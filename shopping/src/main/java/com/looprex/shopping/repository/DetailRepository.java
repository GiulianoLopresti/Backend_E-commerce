package com.looprex.shopping.repository;

import com.looprex.shopping.model.Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailRepository extends JpaRepository<Detail, Long> {
    
    @Query("SELECT d FROM Detail d WHERE d.buyId = :buyId")
    List<Detail> findByBuyId(@Param("buyId") Long buyId);
    
    @Query("SELECT d FROM Detail d WHERE d.productId = :productId")
    List<Detail> findByProductId(@Param("productId") Long productId);
}