package com.example.agencerecrutement.repository;

import com.example.agencerecrutement.model.DemandeurEmploi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandeurEmploiRepository extends JpaRepository<DemandeurEmploi, Long> {
}


