package prajwal.in.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import prajwal.in.entity.Admin;
import prajwal.in.repo.AdminRepo;

@Component
public class AdminDataLoader implements CommandLineRunner {

    @Autowired
    private AdminRepo adminRepo;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin already exists
        if (adminRepo.count() == 0) {
            Admin admin = new Admin();
            admin.setName("prajwal sinkar");
            admin.setEmail("prajwalsinkar2003@gmail.com");
            admin.setPassword("prajwal@123");  // Save plain text password directly
            admin.setPhoneNumber("8468827648");
            adminRepo.save(admin);
            System.out.println("Initial Admin created.");
        } else {
            System.out.println("Admin(s) already exists, skipping initial insert.");
        }
    }
}
