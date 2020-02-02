package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.helpers.PostFilterObject;
import com.joininterngroup.joinintern.mapper.PostMapper;
import com.joininterngroup.joinintern.model.*;
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

    private PostHitMapper postHitMapper;

    private MajorMapper majorMapper;

    private LabelMapper labelMapper;

    private PostLabelMapper postLabelMapper;

    private PostMajorMapper postMajorMapper;

    public PostController(
            PostMapper postMapper,
            Authority authority,
            PostHitMapper postHitMapper,
            MajorMapper majorMapper,
            LabelMapper labelMapper,
            PostLabelMapper postLabelMapper,
            PostMajorMapper postMajorMapper
    ) {
        this.postMapper = postMapper;
        this.authority = authority;
        this.postHitMapper = postHitMapper;
        this.majorMapper = majorMapper;
        this.labelMapper = labelMapper;
        this.postLabelMapper = postLabelMapper;
        this.postMajorMapper = postMajorMapper;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/get")
    List<Post> getAllPost() {
        return this.postMapper.select(c -> c);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/query")
    Post query(
            @RequestParam Integer id
    ) {
        Optional<Post> post = this.postMapper.selectOne(c -> c
                .where(PostDynamicSqlSupport.postId, isEqualTo(id))
        );
        return post.orElse(null);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/delete")
    boolean delete(
            @RequestParam String uid,
            @RequestParam Integer id
    ) {
        if (!this.authority.checkAdmin(uid)) return false;
        int n = this.postMapper.delete(c -> c.where(PostDynamicSqlSupport.postId, isEqualTo(id)));
        if (n > 0) log.info(String.format("Post with id %d is deleted by %s", id, uid));
        return n > 0;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/create")
    boolean createPost(
            @RequestParam(required = false) String postTitle,
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
        post.setPostTitle(postTitle);
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
        post.setPostDate(new Date());
        int n = this.postMapper.insert(post);
        if (n > 0) log.info(String.format("Post with id %d is created", post.getPostId()));
        return n > 0;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/filter")
    List<Post> filter(@RequestBody PostFilterObject postFilterObject) {
        SelectStatementProvider selectStatement = selectDistinct(
                PostDynamicSqlSupport.postTitle,
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
                .and(PostDynamicSqlSupport.postTitle, isEqualToWhenPresent(postFilterObject.getTitle()))
                .and(PostDynamicSqlSupport.duration, isGreaterThanOrEqualToWhenPresent(postFilterObject.getMinDuration()))
                .and(PostDynamicSqlSupport.distancezb, isLessThanOrEqualToWhenPresent(postFilterObject.getDistanceZB()))
                .and(PostDynamicSqlSupport.distancemh, isLessThanOrEqualToWhenPresent(postFilterObject.getDistanceMH()))
                .and(PostMajorDynamicSqlSupport.majorId, isInWhenPresent(postFilterObject.getMajors()))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return this.postMapper.selectMany(selectStatement);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/majors")
    List<Major> getMajors(
            @RequestParam Integer id
    ) {
        SelectStatementProvider selectStatementProvider = selectDistinct(
                MajorDynamicSqlSupport.majorId,
                MajorDynamicSqlSupport.majorName
        )
                .from(PostDynamicSqlSupport.post)
                .join(PostMajorDynamicSqlSupport.postMajor)
                .on(PostDynamicSqlSupport.postId, equalTo(PostMajorDynamicSqlSupport.postId))
                .join(MajorDynamicSqlSupport.major)
                .on(PostMajorDynamicSqlSupport.majorId, equalTo(MajorDynamicSqlSupport.majorId))
                .where(PostDynamicSqlSupport.postId, isEqualTo(id))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return this.majorMapper.selectMany(selectStatementProvider);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/labels")
    List<Label> getLabels(
            @RequestParam Integer id
    ) {
        SelectStatementProvider selectStatementProvider = selectDistinct(
                LabelDynamicSqlSupport.labelId,
                LabelDynamicSqlSupport.labelContent
        )
                .from(PostDynamicSqlSupport.post)
                .join(PostLabelDynamicSqlSupport.postLabel)
                .on(PostDynamicSqlSupport.postId, equalTo(PostLabelDynamicSqlSupport.postId))
                .join(LabelDynamicSqlSupport.label)
                .on(PostLabelDynamicSqlSupport.labelId, equalTo(LabelDynamicSqlSupport.labelId))
                .where(PostDynamicSqlSupport.postId, isEqualTo(id))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return this.labelMapper.selectMany(selectStatementProvider);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/label/add")
    boolean addLabel(
            @RequestParam Integer labelId,
            @RequestParam Integer postId
    ) {
        PostLabel postLabel = new PostLabel();
        postLabel.setLabelId(labelId);
        postLabel.setPostId(postId);
        int n = this.postLabelMapper.insert(postLabel);
        return n > 0;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/major/add")
    boolean addMajor(
            @RequestParam Integer postId,
            @RequestParam Integer majorId
    ) {
        PostMajor postMajor = new PostMajor();
        postMajor.setMajorId(majorId);
        postMajor.setPostId(postId);
        int n = this.postMajorMapper.insert(postMajor);
        return n > 0;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/update")
    @ResponseBody
    boolean updatePost(
            @RequestParam Integer postId,
            @RequestParam String openId,
            @RequestParam(required = false) String postTitle,
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
        int n = this.postMapper.update(c -> c.set(PostDynamicSqlSupport.duration)
                .equalToWhenPresent(duration)
                .set(PostDynamicSqlSupport.postTitle)
                .equalToWhenPresent(postTitle)
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
        return n > 0;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/complete")
    boolean completePost(
            @RequestParam Integer id
    ) {
        int n = this.postMapper.update(c -> c.set(PostDynamicSqlSupport.completed)
                .equalTo(true)
                .where(PostDynamicSqlSupport.postId, isEqualTo(id)));
        if (n > 0) log.info(String.format("Post %d is completed", id));
        return n > 0;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/hit")
    void hitPost(
            @RequestParam String user_id,
            @RequestParam Integer postId
    ) {
        PostHit postHit = new PostHit();

        postHit.setHitterId(user_id);
        postHit.setPostId(postId);
        postHit.setPostHitTime(new Date());

        this.postHitMapper.insert(postHit);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/hits")
    Long getHits(
            @RequestParam Integer postId
    ) {
        return this.postHitMapper.count(c -> c.where(PostDynamicSqlSupport.postId, isEqualTo(postId)));
    }
}
