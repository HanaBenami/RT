package il.co.rtcohen.rt.config;

import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("!demo")
@ComponentScan(value = {"il.co.rtcohen.rt"})
public class MSSqlConfig {

    @Value("${sqlserver.url}")
    private String connectionUrl;

    @Autowired
    private PoolableConnectionFactory poolableConnectionFactory;

    @Bean
    @Autowired
    public PoolableConnectionFactory poolableConnectionFactory(ConnectionFactory connectionFactory) {
        return new PoolableConnectionFactory(connectionFactory, null);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new DriverManagerConnectionFactory(connectionUrl,null);
    }

    @Bean(destroyMethod = "close")
    @Autowired
    public DataSource dataSource(PoolableConnectionFactory poolableConnectionFactory) {
        ObjectPool<PoolableConnection> connectionPool =
                new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
        return new PoolingDataSource<>(connectionPool);
    }
}

