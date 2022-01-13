package com.nbufe.community;

import net.minidev.json.JSONUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {
    private static final Logger logger= LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger(){
        System.out.println(logger.getName());

        logger.debug("debug logger");
        logger.info("info logger");
        logger.warn("warn logger");
        logger.error("error logger");
    }
}
