package com.nbufe.community.dao;

import com.nbufe.community.entity.Comment;
import com.nbufe.community.util.CommunityConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

   Comment selectCommentById(int id);

}
