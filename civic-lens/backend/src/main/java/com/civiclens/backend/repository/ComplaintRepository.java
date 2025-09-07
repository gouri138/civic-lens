package com.civiclens.backend.repository;

import com.civiclens.backend.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserId(Long userId);

    List<Complaint> findByDepartmentId(Long departmentId);

    List<Complaint> findByStatus(String status);
}
