package se.magnus.microservices.core.gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("se.magnus")
public class GymServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymServiceApplication.class, args);
	}

}
