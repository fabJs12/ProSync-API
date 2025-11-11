package com.example.projectapi.repository;

import com.example.projectapi.model.Integration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IntegrationRepository extends JpaRepository<Integration, Integer> {
    List<Integration> findByUserId(Integer userId);
    Optional<Integration> findByUserIdAndServiceName(Integer userId, String serviceName);
    List<Integration> findByServiceName(String serviceName);
}