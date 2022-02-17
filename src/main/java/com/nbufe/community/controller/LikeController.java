package com.nbufe.community.controller;

import com.nbufe.community.entity.Event;
import com.nbufe.community.entity.User;
import com.nbufe.community.event.EventProducer;
import com.nbufe.community.service.LikeService;
import com.nbufe.community.util.CommunityConstant;
import com.nbufe.community.util.CommunityUtil;
import com.nbufe.community.util.HostHolder;
import com.nbufe.community.util.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Resource
    private LikeService likeService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private EventProducer eventProducer;

    @Resource
    private RedisTemplate redisTemplate;

    @PostMapping("like")
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId, int postId){
        User user=hostHolder.getUser();
        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);

        //点赞数
        long likeCount=likeService.findEntityLikeCount(entityType,entityId);
        //是否点赞
        int likeStatus=likeService.findEntityLikeStatus(user.getId(),entityType,entityId);

        //返回结果
        Map<String,Object> map=new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        // 触发点赞事件
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        if(entityType==ENTITY_TYPE_POST){
            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }

        return CommunityUtil.getJSONString(0,null,map);
    }
}
