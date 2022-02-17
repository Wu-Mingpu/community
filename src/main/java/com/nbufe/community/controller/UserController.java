package com.nbufe.community.controller;

import com.nbufe.community.annotation.LoginRequired;
import com.nbufe.community.entity.User;
import com.nbufe.community.service.FollowService;
import com.nbufe.community.service.LikeService;
import com.nbufe.community.service.UserService;
import com.nbufe.community.util.CommunityConstant;
import com.nbufe.community.util.CommunityUtil;
import com.nbufe.community.util.HostHolder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController implements CommunityConstant {
    private static Logger logger= LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Resource
    private UserService userService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private LikeService likeService;

    @Resource
    private FollowService followService;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @LoginRequired
    @GetMapping("setting")
    public String getSettingPage(Model model){
        // 上传文件名称
        String fileName = CommunityUtil.generateUUID();
        // 设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        // 生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);

        return "/site/setting";
    }

    // 更新头像路径
    @PostMapping("/header/url")
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJSONString(1, "文件名不能为空!");
        }

        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeaderUrl(hostHolder.getUser().getId(), url);

        return CommunityUtil.getJSONString(0);
    }


    //废弃
    @LoginRequired
    @PostMapping("upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","您还没有选择图片！");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }

        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() +"."+ suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeaderUrl(user.getId(), headerUrl);

        return "redirect:/index";

    }

    //废弃
    @GetMapping("header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    @LoginRequired
    @PostMapping("password")
    public String updatePwd(Model model,String oldPwd,String  newPwd,String confirmPwd,@CookieValue("ticket") String ticket){
        Map<String,Object> map=userService.updatePassword(oldPwd,newPwd,confirmPwd);
        if(map==null||map.isEmpty()){
            userService.logout(ticket);
            model.addAttribute("msg", "密码修改成功，将跳转至登录界面！");
            model.addAttribute("target", "/login");
            return "/site/operate-result";
        }else{
            model.addAttribute("oldPwdMsg",map.get("oldPwdMsg"));
            model.addAttribute("newPwdMsg",map.get("newPwdMsg"));
            model.addAttribute("confirmPwdMsg",map.get("confirmPwdMsg"));
            return "/site/setting";
        }
    }

    //个人主页
    @GetMapping("profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user=userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在！");

        }

        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount=likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";

    }
}
