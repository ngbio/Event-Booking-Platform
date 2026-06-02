package com.group3.configs;

import java.util.Properties;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import static org.hibernate.cfg.JdbcSettings.DIALECT;
import static org.hibernate.cfg.JdbcSettings.SHOW_SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@Configuration
@PropertySource("classpath:database.properties")
public class HibernateConfigs {

    @Autowired
    private Environment env;

    @Bean
    public LocalSessionFactoryBean getSessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setPackagesToScan(new String[]{"com.group3.pojo"});
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setPoolName(env.getProperty("HIKARI_POOL_NAME", "EventBookingHikariPool"));
        config.setDriverClassName(env.getProperty("hibernate.connection.driverClass"));
        config.setJdbcUrl(env.getProperty("DB_URL", env.getProperty("hibernate.connection.url")));
        config.setUsername(env.getProperty("DB_USERNAME", env.getProperty("hibernate.connection.username")));
        config.setPassword(env.getProperty("DB_PASSWORD", env.getProperty("hibernate.connection.password")));
        config.setMaximumPoolSize(getIntProperty("HIKARI_MAXIMUM_POOL_SIZE", "hikari.maximumPoolSize", 10));
        config.setMinimumIdle(getIntProperty("HIKARI_MINIMUM_IDLE", "hikari.minimumIdle", 2));

        return new HikariDataSource(config);
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.put(DIALECT, env.getProperty("hibernate.dialect"));
        props.put(SHOW_SQL, env.getProperty("hibernate.showSql"));
        return props;
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(getSessionFactory().getObject());
        
        return transactionManager;
    }

    private int getIntProperty(String envKey, String propertyKey, int defaultValue) {
        return Integer.parseInt(env.getProperty(envKey, env.getProperty(propertyKey, String.valueOf(defaultValue))));
    }
}
