package microservices.core.program;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("se.magnus")
public class ProgramServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgramServiceApplication.class, args);
	}

}
