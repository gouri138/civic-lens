package com.civiclens.backend.controller;

import com.civiclens.backend.dto.ComplaintDTO;
import com.civiclens.backend.dto.UpdateStatusRequest;
import com.civiclens.backend.entity.Complaint;
import com.civiclens.backend.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Complaint> submitComplaint(
            @RequestParam("userId") Long userId,
            @RequestParam("category") String category,
            @RequestParam("location") String location,
            @RequestParam("description") String description,
            @RequestParam("status") String status,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        ComplaintDTO complaintDTO = new ComplaintDTO();
        complaintDTO.setUserId(userId);
        complaintDTO.setCategory(category);
        complaintDTO.setLocation(location);
        complaintDTO.setDescription(description);
        complaintDTO.setStatus(status);
        complaintDTO.setLatitude(latitude);
        complaintDTO.setLongitude(longitude);
        complaintDTO.setRegion(region);
        Complaint complaint = complaintService.submitComplaint(complaintDTO, image);
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

    @PostMapping("/{id}/proof")
    public ResponseEntity<Complaint> uploadProofImage(@PathVariable Long id, @RequestParam("proofImage") MultipartFile proofImage) throws IOException {
        Complaint complaint = complaintService.uploadProofImage(id, proofImage);
        if (complaint != null) {
            return ResponseEntity.ok(complaint);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/top-locations")
    public ResponseEntity<List<Object[]>> getTopLocations() {
        List<Object[]> topLocations = complaintService.getTopLocations();
        return ResponseEntity.ok(topLocations);
    }

    @GetMapping("/top-districts")
    public ResponseEntity<List<Object[]>> getTopDistricts() {
        List<Object[]> topDistricts = complaintService.getTopDistricts();
        return ResponseEntity.ok(topDistricts);
    }
}
