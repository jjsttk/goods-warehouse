package com.jjsttk.goodswarehouse;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Goods Warehouse API",
                version = "1.0",
                description = "REST API для управления складом товаров"),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local server")
        }
)
public class GoodsWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsWarehouseApplication.class, args);
    }

}
