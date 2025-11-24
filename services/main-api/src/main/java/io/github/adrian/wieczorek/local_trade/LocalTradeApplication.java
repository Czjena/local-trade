package io.github.adrian.wieczorek.local_trade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Slf4j
@SpringBootApplication
@EnableCaching
public class LocalTradeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalTradeApplication.class, args);
		log.info("Local Trade Application Started");
	}
}
