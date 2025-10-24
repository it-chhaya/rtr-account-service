package kh.edu.cstad.account;

import kh.edu.cstad.account.config.props.DatabaseProps;
import kh.edu.cstad.account.config.props.ServiceInfoProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@EnableConfigurationProperties(value = {
        DatabaseProps.class,
        ServiceInfoProps.class
})
@SpringBootApplication
public class AccountApp {

    public static void main(String[] args) {
        SpringApplication.run(AccountApp.class, args);
    }

}
