package prajwal.in.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import prajwal.in.dto.LoginRequest;
import prajwal.in.entity.CaseWorker;
import prajwal.in.service.CaseWorkerService;

@RestController
@RequestMapping("/api/caseworkers")
public class CaseWorkerController {

    @Autowired
    private CaseWorkerService caseWorkerService;

    @PostMapping("/register")
    public ResponseEntity<CaseWorker> registerCaseWorker(@RequestBody CaseWorker worker) {
        CaseWorker saved = caseWorkerService.createCaseWorker(worker);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        return caseWorkerService.login(request);
    }

    @GetMapping("/all")
    public List<CaseWorker> getAllCaseWorkers() {
        return caseWorkerService.findAll();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody String newPassword) {
        return caseWorkerService.resetPassword(token, newPassword);
    }
}
