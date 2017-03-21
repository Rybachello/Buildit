package com.buildit.rental.domain.repository;

import com.buildit.rental.domain.model.PlantHireRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Rybachello on 3/21/2017.
 */
@Repository
public interface PlantHireRequestRepository extends JpaRepository<PlantHireRequest, String> {

}
