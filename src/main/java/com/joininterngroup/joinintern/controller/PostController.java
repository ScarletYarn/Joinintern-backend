package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.helpers.PostFilterObject;
import com.joininterngroup.joinintern.mapper.PostMapper;
import com.joininterngroup.joinintern.model.Post;
import com.joininterngroup.joinintern.model.PostClick;
import com.joininterngroup.joinintern.utils.Authority;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.web.bind.annotation.*;

import com.joininterngroup.joinintern.mapper.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    private PostMapper postMapper;

    private Authority authority;

    private PostClickMapper postClickMapper;

    public PostController(
            PostMapper postMapper,
            Authority authority,
            PostClickMapper postClickMapper
    ) {
        this.postMapper = postMapper;
        this.authority = authority;
        this.postClickMapper = postClickMapper;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/get")
    List<Post> getAllPost() {
        return this.postMapper.select(c -> c);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/create")
    boolean createPost(
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Float distancezb,
            @RequestParam(required = false) Float distancemh,
            @RequestParam(required = false) String postContent,
            @RequestParam(required = false) Date expiration,
            @RequestParam String authorId,
            @RequestParam(required = false) Date startTime,
            @RequestParam(required = false) Date endTime
    ) {
        Post post = new Post();
        if (duration != null) post.setDuration(duration);
        if (location != null) post.setLocation(location);
        if (distancezb != null) post.setDistancezb(distancezb);
        if (distancemh != null) post.setDistancemh(distancemh);
        if (postContent != null) post.setPostContent(postContent);
        post.setCompleted(false);
        if (expiration != null) post.setExpiration(expiration);
        post.setAuthorId(authorId);
        if (startTime != null) post.setStartTime(startTime);
        if (endTime != null) post.setEndTime(endTime);
        this.postMapper.insert(post);
        return true;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/filter")
    List<Post> filter(@RequestBody PostFilterObject postFilterObject) {
        SelectStatementProvider selectStatement = selectDistinct(
                PostDynamicSqlSupport.postId,
                PostDynamicSqlSupport.duration,
                PostDynamicSqlSupport.location,
                PostDynamicSqlSupport.distancezb,
                PostDynamicSqlSupport.distancemh,
                PostDynamicSqlSupport.postContent,
                PostDynamicSqlSupport.completed,
                PostDynamicSqlSupport.expiration,
                PostDynamicSqlSupport.authorId,
                PostDynamicSqlSupport.startTime,
                PostDynamicSqlSupport.endTime
        )
                .from(PostDynamicSqlSupport.post)
                .join(PostMajorDynamicSqlSupport.postMajor)
                .on(PostDynamicSqlSupport.postId, equalTo(PostMajorDynamicSqlSupport.postId))
                .where(PostDynamicSqlSupport.duration, isLessThanOrEqualToWhenPresent(postFilterObject.getMaxDuration()))
                .and(PostDynamicSqlSupport.duration, isGreaterThanOrEqualToWhenPresent(postFilterObject.getMinDuration()))
                .and(PostDynamicSqlSupport.distancezb, isLessThanOrEqualToWhenPresent(postFilterObject.getDistanceZB()))
                .and(PostDynamicSqlSupport.distancemh, isLessThanOrEqualToWhenPresent(postFilterObject.getDistanceMH()))
                .and(PostMajorDynamicSqlSupport.majorId, isInWhenPresent(postFilterObject.getMajors()))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return this.postMapper.selectMany(selectStatement);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/update")
    @ResponseBody
    boolean updatePost(
            @RequestParam Integer postId,
            @RequestParam String openId,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Float distancezb,
            @RequestParam(required = false) Float distancemh,
            @RequestParam(required = false) String postContent,
            @RequestParam(required = false) Date expiration,
            @RequestParam(required = false) Date startTime,
            @RequestParam(required = false) Date endTime
    ) {
        Optional<Post> post = this.postMapper.selectOne(c -> c.where(PostDynamicSqlSupport.postId, isEqualTo(postId)));
        if (!post.isPresent()) return false;
        else if (!post.get().getAuthorId().equals(openId)) {
            if (!this.authority.checkAdmin(openId)) return false;
        }
        this.postMapper.update(c -> c.set(PostDynamicSqlSupport.duration)
                .equalToWhenPresent(duration)
                .set(PostDynamicSqlSupport.location)
                .equalToWhenPresent(location)
                .set(PostDynamicSqlSupport.distancezb)
                .equalToWhenPresent(distancezb)
                .set(PostDynamicSqlSupport.distancemh)
                .equalToWhenPresent(distancemh)
                .set(PostDynamicSqlSupport.postContent)
                .equalToWhenPresent(postContent)
                .set(PostDynamicSqlSupport.expiration)
                .equalToWhenPresent(expiration)
                .set(PostDynamicSqlSupport.startTime)
                .equalToWhenPresent(startTime)
                .set(PostDynamicSqlSupport.endTime)
                .equalToWhenPresent(endTime)
                .where(PostDynamicSqlSupport.postId, isEqualTo(postId))

        );
        return false;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/complete")
    boolean completePost(@RequestParam Integer id) {
        this.postMapper.update(c -> c.set(PostDynamicSqlSupport.completed)
                .equalTo(true)
                .where(PostDynamicSqlSupport.postId, isEqualTo(id)));
        return true;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/click")
    void clickPost(
            @RequestParam String user_id,
            @RequestParam Integer postId
    ) {
        PostClick postClick = new PostClick();

        postClick.setClickerId(user_id);
        postClick.setPostId(postId);
        postClick.setPostClickTime(new Date());

        this.postClickMapper.insert(postClick);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/hits")
    Long getHits(
            @RequestParam Integer postId
    ) {
        return this.postClickMapper.count(c -> c.where(PostDynamicSqlSupport.postId, isEqualTo(postId)));
    }
}
