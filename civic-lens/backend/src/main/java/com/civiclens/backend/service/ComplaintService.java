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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public Complaint submitComplaint(ComplaintDTO complaintDTO, MultipartFile image) throws IOException {
        Complaint complaint = new Complaint();
        complaint.setCategory(complaintDTO.getCategory());
        complaint.setLocation(complaintDTO.getLocation());
        complaint.setDescription(complaintDTO.getDescription());
        complaint.setStatus("Pending");
        complaint.setLatitude(complaintDTO.getLatitude());
        complaint.setLongitude(complaintDTO.getLongitude());
        complaint.setRegion(complaintDTO.getRegion());

        Optional<User> user = userRepository.findById(complaintDTO.getUserId());
        if (user.isPresent()) {
            complaint.setUser(user.get());
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            complaint.setImageUrl(imageUrl);
        }

        return complaintRepository.save(complaint);
    }

    private String saveImage(MultipartFile image) throws IOException {
        String uploadsDir = "uploads/";
        Path uploadPath = Paths.get(uploadsDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath);
        return "/uploads/" + fileName;
    }

    public Complaint uploadProofImage(Long complaintId, MultipartFile proofImage) throws IOException {
        Optional<Complaint> complaintOpt = complaintRepository.findById(complaintId);
        if (complaintOpt.isPresent()) {
            Complaint complaint = complaintOpt.get();
            if (proofImage != null && !proofImage.isEmpty()) {
                String proofImageUrl = saveImage(proofImage);
                complaint.setProofImageUrl(proofImageUrl);
                return complaintRepository.save(complaint);
            }
        }
        return null;
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
