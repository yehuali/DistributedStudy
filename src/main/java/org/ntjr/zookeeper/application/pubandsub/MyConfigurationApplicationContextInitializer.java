package org.ntjr.zookeeper.application.pubandsub;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public class MyConfigurationApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        ZkConfigManager  zkManager = new ZkConfigManager();
        Properties properties = new Properties();
        Config config = zkManager.getConfig();
        properties.put("jdbc.url",config.getJdbcUrl());
        properties.put("jdbc.username",config.getJdbcUserName());
        properties.put("jdbc.password",config.getJdbcPassword());
        PropertiesPropertySource propertiesPropertySource = new PropertiesPropertySource("myProperties",properties);
        configurableApplicationContext.getEnvironment().getPropertySources().addFirst(propertiesPropertySource);
    }
}
