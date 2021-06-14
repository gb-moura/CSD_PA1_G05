package Server.Replication;

import bftsmart.tom.AsynchServiceProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@PropertySource("classpath:application.properties")
@Configuration
 public class AppConfig {

    @Value("${replica.id}")
    private int ID;


    @Bean
    public AsynchServiceProxy AsynchServiceProxy() {
        return new AsynchServiceProxy(ID);
    }

 }