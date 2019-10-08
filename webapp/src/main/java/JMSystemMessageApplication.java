package jm;

import jm.test.TestDataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class JMSystemMessageApplication {
    private static final Logger logger = LoggerFactory.getLogger(JMSystemMessageApplication.class);

    public static void main(String[] args) {
        //SpringApplication.run(JMSystemMessageApplication.class);

        //Для тестовой инициализации данных в базе
        ApplicationContext applicationContext = SpringApplication.run(JMSystemMessageApplication.class, args);
        UserService userService = applicationContext.getBean(UserService.class);
        RoleDAO roleDAO = applicationContext.getBean(RoleDAO.class);
        ChannelDAO channelDAO = applicationContext.getBean(ChannelDAO.class);

        TestDataInitializer testDataInitializer = new TestDataInitializer();
        testDataInitializer.checkDataInitialisation(roleDAO, channelDAO, userService);
    }
}
