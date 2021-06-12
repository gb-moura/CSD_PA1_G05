package Server.Replication;

import bftsmart.tom.ServiceProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import Server.Controller.WalletController;
import Server.Controller.WalletControllerImpl;

@PropertySource("classpath:application.properties")
@Configuration
 public class AppConfig {

    @Value("${replica.id}")
    private int ID;

     @Bean
     public ServiceProxy serviceProxy() {
         return new ServiceProxy(ID);
     }

 }