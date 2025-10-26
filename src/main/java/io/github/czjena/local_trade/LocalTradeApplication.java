package io.github.czjena.local_trade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Slf4j
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
public class LocalTradeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalTradeApplication.class, args);
		log.info("Local Trade Application Started");
	}
}
