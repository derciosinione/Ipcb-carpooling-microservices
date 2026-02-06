package pt.ipcb.car.pooling.trips;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TripsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TripsApplication.class, args);
    }

}
