package microservices.composite.gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("se.magnus")
public class GymCompositeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymCompositeServiceApplication.class, args);
	}

}
