package microservices.composite.gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan("se.magnus")
public class GymCompositeServiceApplication {
	
	@Bean
	RestTemplate restTemplate() {
	 return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(GymCompositeServiceApplication.class, args);
	}

}
