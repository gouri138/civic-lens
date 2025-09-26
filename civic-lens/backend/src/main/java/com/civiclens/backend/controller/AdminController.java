package com.civiclens.backend.controller;

import com.civiclens.backend.dto.UserDTO;
import com.civiclens.backend.entity.Admin;
import com.civiclens.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<Admin> register(@RequestBody UserDTO userDTO) {
        Admin admin = adminService.registerAdmin(userDTO);
        return ResponseEntity.ok(admin);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        // Fixed admin credentials
        if ("admin@civiclens.com".equals(userDTO.getEmail()) && "admin123".equals(userDTO.getPassword())) {
            // Create a dummy admin object for response
            Admin admin = new Admin();
            admin.setId(1L);
            admin.setName("Admin");
            admin.setEmail("admin@civiclens.com");
            admin.setRole("ADMIN");
            return ResponseEntity.ok(admin);
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        Optional<Admin> admin = adminService.getAdminById(id);
        return admin.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
