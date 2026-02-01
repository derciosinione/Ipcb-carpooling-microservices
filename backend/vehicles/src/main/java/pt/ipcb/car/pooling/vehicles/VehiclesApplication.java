package pt.ipcb.car.pooling.vehicles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VehiclesApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehiclesApplication.class, args);
    }

}
