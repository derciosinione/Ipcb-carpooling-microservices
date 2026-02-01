package pt.ipcb.carpooling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {
        pt.ipcb.carpooling.clients.IdentityClient.class,
        pt.ipcb.carpooling.clients.VehicleClient.class,
        pt.ipcb.carpooling.clients.TripsClient.class,
        pt.ipcb.carpooling.clients.GpsClient.class
})
public class CarPoolingFrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarPoolingFrontendApplication.class, args);
    }

}
