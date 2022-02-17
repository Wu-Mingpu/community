package com.nbufe.community.service;

import com.nbufe.community.dao.DiscussPostMapper;
import com.nbufe.community.entity.DiscussPost;
import com.nbufe.community.util.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DiscussPostService {

    @Resource
    private DiscussPostMapper discussPostMapper;

    @Resource
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit,int orderMode){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderMode);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        

        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost selectDiscussPost(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);

    }

    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}


