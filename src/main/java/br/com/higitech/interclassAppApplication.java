package br.com.higitech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // LIGA O MOTOR DE CACHE
public class interclassAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(interclassAppApplication.class, args);
	}

}
