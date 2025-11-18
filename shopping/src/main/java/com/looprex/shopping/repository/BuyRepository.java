package com.looprex.shopping.repository;

import com.looprex.shopping.model.Buy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuyRepository extends JpaRepository<Buy, Long> {
    
    Optional<Buy> findByOrderNumber(String orderNumber);
    
    @Query("SELECT b FROM Buy b WHERE b.userId = :userId ORDER BY b.buyDate DESC")
    List<Buy> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT b FROM Buy b WHERE b.statusId = :statusId ORDER BY b.buyDate DESC")
    List<Buy> findByStatusId(@Param("statusId") Long statusId);
    
    @Query("SELECT b FROM Buy b ORDER BY b.buyDate DESC")
    List<Buy> findAllOrderByBuyDateDesc();
}