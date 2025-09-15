package com.civiclens.backend.service;

import com.civiclens.backend.dto.ComplaintDTO;
import com.civiclens.backend.entity.Complaint;
import com.civiclens.backend.entity.Department;
import com.civiclens.backend.entity.User;
import com.civiclens.backend.repository.ComplaintRepository;
import com.civiclens.backend.repository.DepartmentRepository;
import com.civiclens.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public Complaint submitComplaint(ComplaintDTO complaintDTO) {
        Complaint complaint = new Complaint();
        complaint.setCategory(complaintDTO.getCategory());
        complaint.setLocation(complaintDTO.getLocation());
        complaint.setDescription(complaintDTO.getDescription());
        complaint.setStatus("Pending");

        Optional<User> user = userRepository.findById(complaintDTO.getUserId());
        if (user.isPresent()) {
            complaint.setUser(user.get());
        }

        return complaintRepository.save(complaint);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public List<Complaint> getComplaintsByUser(Long userId) {
        return complaintRepository.findByUserId(userId);
    }

    public List<Complaint> getComplaintsByDepartment(Long departmentId) {
        return complaintRepository.findByDepartmentId(departmentId);
    }

    public Complaint updateComplaintStatus(Long id, String status, Long departmentId) {
        Optional<Complaint> complaintOpt = complaintRepository.findById(id);
        if (complaintOpt.isPresent()) {
            Complaint complaint = complaintOpt.get();
            if (status != null) {
                complaint.setStatus(status);
            }
            if (departmentId != null) {
                Optional<Department> department = departmentRepository.findById(departmentId);
                if (department.isPresent()) {
                    complaint.setDepartment(department.get());
                }
            }
            return complaintRepository.save(complaint);
        }
        return null;
    }

    public List<Object[]> getTopLocations() {
        return complaintRepository.findTopLocations();
    }
}
