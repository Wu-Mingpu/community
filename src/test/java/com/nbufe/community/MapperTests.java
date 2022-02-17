package com.nbufe.community;

import com.nbufe.community.dao.DiscussPostMapper;
import com.nbufe.community.dao.LoginTicketMapper;
import com.nbufe.community.dao.UserMapper;
import com.nbufe.community.entity.DiscussPost;
import com.nbufe.community.entity.LoginTicket;
import com.nbufe.community.entity.User;
import com.nbufe.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private DiscussPostMapper discussPostMapper;

    @Resource
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelectUser(){
        User user=userMapper.selectById(101);
        System.out.println(user);

        user=userMapper.selectByName("liubei");
        System.out.println(user);

        user=userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user=new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows=userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void upadteUser(){
        int rows=userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows=userMapper.updateHeader(150,"http://nowcoder.com/102.png");
        System.out.println(rows);

        rows=userMapper.updatePassword(150,"hello");
        System.out.println(rows);
    }


    @Test
    public void selectDiscussPosts(){
        List<DiscussPost> list=discussPostMapper.selectDiscussPosts(149,0,10,0);
        for(DiscussPost post:list){
            System.out.println(post);
        }

        int rows=discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testPassword(){
        String password= CommunityUtil.md5("564200c2605");
        System.out.println(password);
    }

}
