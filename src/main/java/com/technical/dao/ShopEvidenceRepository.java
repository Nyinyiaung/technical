package com.technical.dao;

import com.technical.entity.ShopEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopEvidenceRepository extends JpaRepository<ShopEvidence, Long> {
}
