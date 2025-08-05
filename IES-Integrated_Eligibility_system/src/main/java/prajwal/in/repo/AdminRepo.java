package prajwal.in.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import prajwal.in.entity.Admin;

public interface AdminRepo extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
}
