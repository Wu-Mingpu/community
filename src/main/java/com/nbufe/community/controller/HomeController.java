package com.nbufe.community.controller;

import com.nbufe.community.entity.DiscussPost;
import com.nbufe.community.entity.Page;
import com.nbufe.community.entity.User;
import com.nbufe.community.service.DiscussPostService;
import com.nbufe.community.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private UserService userService;

    @GetMapping("index")
    public String getIndexPage(Model model, Page page){
        /*方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入到Model中，所以在thymeleaf中可以直接访问Page对象中的数据。*/
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");


        List<DiscussPost> list=discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> discussPosts=new ArrayList<>();

        if(list!=null){
            for(DiscussPost post:list){
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                User user=userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);

            }
        }

        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }
}
