package io.github.czjena.local_trade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class LocalTradeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalTradeApplication.class, args);
		log.info("Local Trade Application Started");
		log.info(System.getenv("DB_PASSWORD"));
	}

}
