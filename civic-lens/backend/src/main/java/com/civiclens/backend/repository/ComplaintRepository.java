package com.civiclens.backend.repository;

import com.civiclens.backend.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserId(Long userId);

    List<Complaint> findByDepartmentId(Long departmentId);

    List<Complaint> findByStatus(String status);

    @Query("SELECT c.location, COUNT(c) as cnt FROM Complaint c GROUP BY c.location ORDER BY cnt DESC")
    List<Object[]> findTopLocations();
}
