package tech.ewrey.cost_accounting_api.bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:messages/message_en.properties")
public class MessageEnProperties {

    @Autowired
    private Environment env;

    public String getProperty(String key) {
        return env.getProperty(key);
    }
}
