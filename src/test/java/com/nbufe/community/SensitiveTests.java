package com.nbufe.community;

import com.nbufe.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFliter(){
        String text="cnm,你个sb,怎么学会嫖娼和吸毒了！";
        text=sensitiveFilter.filter(text);
        System.out.println(text);

        text="cn¤¤m,你个¤s¤b¤,怎么学会¤嫖娼¤和吸¤毒¤了！";
        text=sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
