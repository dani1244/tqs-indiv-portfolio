package com.zeromones.repository;

import com.zeromones.model.RequestStatus;
import com.zeromones.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    
    Optional<ServiceRequest> findByAccessToken(String accessToken);
    
    List<ServiceRequest> findByMunicipality(String municipality);
    
    List<ServiceRequest> findByCurrentStatus(RequestStatus status);
    
    List<ServiceRequest> findByMunicipalityAndCurrentStatus(String municipality, RequestStatus status);
    
    @Query("SELECT COUNT(sr) FROM ServiceRequest sr WHERE sr.collectionDate = :date AND sr.municipality = :municipality")
    long countByCollectionDateAndMunicipality(LocalDate date, String municipality);
    
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.collectionDate BETWEEN :startDate AND :endDate")
    List<ServiceRequest> findByCollectionDateBetween(LocalDate startDate, LocalDate endDate);
    
    boolean existsByAccessToken(String accessToken);
}
