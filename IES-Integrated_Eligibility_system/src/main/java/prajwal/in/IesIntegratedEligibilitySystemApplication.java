package prajwal.in;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class IesIntegratedEligibilitySystemApplication {

	public static void main(String[] args) {
		 System.out.println(new BCryptPasswordEncoder().encode("prajwal@123"));
		SpringApplication.run(IesIntegratedEligibilitySystemApplication.class, args);
	}

}
