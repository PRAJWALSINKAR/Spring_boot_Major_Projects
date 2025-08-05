package prajwal.in.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import prajwal.in.dto.CaseWorkerRequest;
import prajwal.in.dto.LoginRequest;
import prajwal.in.entity.Admin;
import prajwal.in.service.AdminService;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<Admin> registerAdmin(@RequestBody Admin admin) {
        Admin saved = adminService.createAdmin(admin);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        return adminService.login(request);
    }



    @GetMapping("/all")
    public List<Admin> getAllAdmins() {
        return adminService.findAll();
    }
    
    @PostMapping("/caseworker")
    public ResponseEntity<String> createCaseWorker(@RequestBody CaseWorkerRequest request) {
        return adminService.createCaseWorker(request);
    }
}
