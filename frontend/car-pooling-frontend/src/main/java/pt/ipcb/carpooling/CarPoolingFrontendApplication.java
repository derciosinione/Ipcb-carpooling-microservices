package pt.ipcb.carpooling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CarPoolingFrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarPoolingFrontendApplication.class, args);
    }

}
