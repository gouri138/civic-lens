package com.civiclens.backend.controller;

import com.civiclens.backend.dto.ComplaintDTO;
import com.civiclens.backend.dto.UpdateStatusRequest;
import com.civiclens.backend.entity.Complaint;
import com.civiclens.backend.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<Complaint> submitComplaint(@RequestBody ComplaintDTO complaintDTO) {
        Complaint complaint = complaintService.submitComplaint(complaintDTO);
        return ResponseEntity.ok(complaint);
    }

    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        List<Complaint> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Complaint>> getComplaintsByUser(@PathVariable Long userId) {
        List<Complaint> complaints = complaintService.getComplaintsByUser(userId);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Complaint>> getComplaintsByDepartment(@PathVariable Long departmentId) {
        List<Complaint> complaints = complaintService.getComplaintsByDepartment(departmentId);
        return ResponseEntity.ok(complaints);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Complaint> updateComplaintStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        Complaint complaint = complaintService.updateComplaintStatus(id, request.getStatus(), null);
        if (complaint != null) {
            return ResponseEntity.ok(complaint);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/assign/{departmentId}")
    public ResponseEntity<Complaint> assignDepartment(@PathVariable Long id, @PathVariable Long departmentId) {
        Complaint complaint = complaintService.updateComplaintStatus(id, null, departmentId);
        if (complaint != null) {
            return ResponseEntity.ok(complaint);
        }
        return ResponseEntity.notFound().build();
    }
}
