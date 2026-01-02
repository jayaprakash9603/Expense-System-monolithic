
package com.jaya.paymentservice.repository;

import com.jaya.paymentservice.models.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {

    Optional<PaymentMethod> findByUserIdAndId(Integer userId, Integer id);


    List<PaymentMethod> findByUserId(@Param("userId") Integer userId);

    // Fix: Add proper spacing
    List<PaymentMethod> findByUserIdAndNameAndType(Integer userId, String name, String type);

    List<PaymentMethod> findByName(String name);

    // Fix: Add proper spacing
    List<PaymentMethod> findByNameAndType(String name, String type);

    List<PaymentMethod> findByUserIdAndName(Integer userId, String name);

    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.name = :name AND pm.type = :type AND pm.isGlobal = true")
    List<PaymentMethod> findByNameAndTypeAndIsGlobalTrue(@Param("name") String name, @Param("type") String type);

    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.name = :name AND pm.userId = :userId")
    Optional<PaymentMethod> findByNameAndUserId(@Param("name") String name, @Param("userId") Integer userId);

    @Query("SELECT pm FROM PaymentMethod pm WHERE LOWER(pm.name) = LOWER(:name) AND pm.userId = :userId")
    Optional<PaymentMethod> findByNameIgnoreCaseAndUserId(@Param("name") String name, @Param("userId") Integer userId);

    List<PaymentMethod> findByIsGlobalTrue();

    /**
     * Reporting/serialization helper: loads element collections eagerly in the same query,
     * avoiding LazyInitializationException when the entity is used to build response maps.
     */
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = { "userIds", "editUserIds" })
    @Query("SELECT pm FROM PaymentMethod pm WHERE LOWER(pm.name) = LOWER(:name) AND pm.userId = :userId")
    Optional<PaymentMethod> findReportByNameIgnoreCaseAndUserId(@Param("name") String name,
                                                               @Param("userId") Integer userId);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = { "userIds", "editUserIds" })
    @Query("SELECT pm FROM PaymentMethod pm WHERE LOWER(pm.name) = LOWER(:name) AND pm.isGlobal = true")
    List<PaymentMethod> findReportByNameIgnoreCaseAndIsGlobalTrue(@Param("name") String name);
}