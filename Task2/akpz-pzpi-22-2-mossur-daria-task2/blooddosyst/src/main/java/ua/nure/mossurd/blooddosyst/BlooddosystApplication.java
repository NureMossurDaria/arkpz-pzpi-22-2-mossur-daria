package ua.nure.mossurd.blooddosyst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ua.nure.mossurd.blooddosyst")
public class BlooddosystApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlooddosystApplication.class, args);
	}

}
