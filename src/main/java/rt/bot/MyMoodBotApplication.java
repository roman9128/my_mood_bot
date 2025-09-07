package rt.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import rt.bot.config.UpdateConsumerMultiThreadingConfiguration;

@SpringBootApplication
@EnableConfigurationProperties(UpdateConsumerMultiThreadingConfiguration.class)
@EnableScheduling
@EnableAsync

public class MyMoodBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyMoodBotApplication.class, args);
    }
}