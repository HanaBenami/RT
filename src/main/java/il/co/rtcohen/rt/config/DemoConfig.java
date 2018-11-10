package il.co.rtcohen.rt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@Profile("demo")
@ComponentScan(value = {"il.co.rtcohen.rt"})
public class DemoConfig {

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.driverClassName}") String driverClassName,
            @Value("${spring.datasource.url}") String connectionUrl,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(connectionUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

}

