package com.nbufe.community;

import com.nbufe.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Resource
    private MailClient mailClient;

    @Resource
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("1779596898@qq.com","TEST","Hello World!");
    }

    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username","wmp");

        String content=templateEngine.process("/mail/mail-demo",context);
        System.out.println(content);

        mailClient.sendMail("1779596898@qq.com","HTML",content);
    }

}
