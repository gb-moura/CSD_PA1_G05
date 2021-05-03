package Client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import Client.Annotations.AUTO;
import Client.Annotations.NONAUTO;

@SpringBootApplication
@NONAUTO
@ComponentScan(basePackages = {"Client"}, excludeFilters={
		@ComponentScan.Filter(type= FilterType.ANNOTATION,
				classes= AUTO.class)})

public class ClientApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ClientApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);

	}

}


