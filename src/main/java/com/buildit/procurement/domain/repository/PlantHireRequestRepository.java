package com.buildit.procurement.domain.repository;

import com.buildit.procurement.domain.model.PlantHireRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Rybachello on 3/21/2017.
 */
@Repository
public interface PlantHireRequestRepository extends JpaRepository<PlantHireRequest, String> {
    @Query("SELECT p FROM PlantHireRequest p WHERE p.purchaseOrder.purchaseOrderId = ?1")
    PlantHireRequest findByPOID(String POID);
}
