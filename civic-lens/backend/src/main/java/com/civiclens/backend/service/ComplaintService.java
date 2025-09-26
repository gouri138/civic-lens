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
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

        // Auto-detect district from coordinates
        if (complaintDTO.getLatitude() != null && complaintDTO.getLongitude() != null) {
            String district = getDistrictFromCoordinates(complaintDTO.getLatitude(), complaintDTO.getLongitude());
            complaint.setDistrict(district);
        }

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

    public List<Object[]> getTopDistricts() {
        return complaintRepository.findTopDistricts();
    }

    private String getDistrictFromCoordinates(Double latitude, Double longitude) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = String.format("https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f&zoom=10&addressdetails=1", latitude, longitude);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode address = root.path("address");

                // Try to get district, county, or state_district
                String district = address.path("county").asText();
                if (district.isEmpty()) {
                    district = address.path("state_district").asText();
                }
                if (district.isEmpty()) {
                    district = address.path("city").asText();
                }
                return district.isEmpty() ? "Unknown District" : district;
            }
        } catch (Exception e) {
            System.err.println("Error fetching district: " + e.getMessage());
        }
        return "Unknown District";
    }
}
