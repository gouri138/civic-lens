package com.civiclens.backend.service;

import com.civiclens.backend.dto.UserDTO;
import com.civiclens.backend.entity.Admin;
import com.civiclens.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Admin registerAdmin(UserDTO userDTO) {
        Admin admin = new Admin();
        admin.setName(userDTO.getName());
        admin.setEmail(userDTO.getEmail());
        admin.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        admin.setRole("ADMIN");
        return adminRepository.save(admin);
    }

    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    public boolean authenticateAdmin(String email, String password) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            return passwordEncoder.matches(password, admin.getPassword());
        }
        return false;
    }
}
