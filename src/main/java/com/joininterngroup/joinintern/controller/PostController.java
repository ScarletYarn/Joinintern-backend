package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.mapper.PostMapper;
import com.joininterngroup.joinintern.model.*;
import com.joininterngroup.joinintern.utils.Authority;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.web.bind.annotation.*;

import com.joininterngroup.joinintern.mapper.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import java.util.ArrayList;
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

    private UserFavPostMapper userFavPostMapper;

    private MyUserMapper myUserMapper;

    public PostController(
            PostMapper postMapper,
            Authority authority,
            PostHitMapper postHitMapper,
            MajorMapper majorMapper,
            LabelMapper labelMapper,
            PostLabelMapper postLabelMapper,
            PostMajorMapper postMajorMapper,
            UserFavPostMapper userFavPostMapper,
            MyUserMapper myUserMapper
    ) {
        this.postMapper = postMapper;
        this.authority = authority;
        this.postHitMapper = postHitMapper;
        this.majorMapper = majorMapper;
        this.labelMapper = labelMapper;
        this.postLabelMapper = postLabelMapper;
        this.postMajorMapper = postMajorMapper;
        this.userFavPostMapper = userFavPostMapper;
        this.myUserMapper = myUserMapper;
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

//    @ResponseBody
//    @RequestMapping(method = RequestMethod.POST, path = "/search")
//    List<Post>

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
    Boolean createPost(
            @RequestParam(required = false) String postTitle,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Float distancezb,
            @RequestParam(required = false) Float distancemh,
            @RequestParam(required = false) String postContent,
            @RequestParam(required = false) Date expiration,
            @RequestParam String authorId,
            @RequestParam(required = false) Date startTime,
            @RequestParam(required = false) Date endTime,
            @RequestParam(required = false) List<Integer> majors,
            @RequestParam(required = false) List<Integer> labels
    ) {
        Optional<MyUser> user = this.myUserMapper.selectOne(c -> c
                .where(MyUserDynamicSqlSupport.userId, isEqualTo(authorId))
        );
        if (!user.isPresent() || !user.get().getValidation().equals("validate")) return false;
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
        if (majors != null) {
            majors.forEach(e -> {
                PostMajor postMajor = new PostMajor();
                postMajor.setPostId(post.getPostId());
                postMajor.setMajorId(e);
                this.postMajorMapper.insert(postMajor);
            });
        }
        if (labels != null) {
            labels.forEach(e -> {
                PostLabel postLabel = new PostLabel();
                postLabel.setPostId(post.getPostId());
                postLabel.setLabelId(e);
                this.postLabelMapper.insert(postLabel);
            });
        }
        return n > 0;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/filter")
    List<Post> filter(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Float distanceZB,
            @RequestParam(required = false) Float distanceMH,
            @RequestParam(required = false) List<Integer> majors
    ) {
        String like = title == null ? null : "%" + title + "%";
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
                PostDynamicSqlSupport.endTime,
                PostDynamicSqlSupport.postDate
        )
                .from(PostDynamicSqlSupport.post)
                .join(PostMajorDynamicSqlSupport.postMajor)
                .on(PostDynamicSqlSupport.postId, equalTo(PostMajorDynamicSqlSupport.postId))
                .where(PostDynamicSqlSupport.duration, isLessThanOrEqualToWhenPresent(maxDuration))
                .and(PostDynamicSqlSupport.postTitle, isLikeWhenPresent(like))
                .and(PostDynamicSqlSupport.duration, isGreaterThanOrEqualToWhenPresent(minDuration))
                .and(PostDynamicSqlSupport.distancezb, isLessThanOrEqualToWhenPresent(distanceZB))
                .and(PostDynamicSqlSupport.distancemh, isLessThanOrEqualToWhenPresent(distanceMH))
                .and(PostMajorDynamicSqlSupport.majorId, isInWhenPresent(majors))
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

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/fav")
    Boolean fav(
            @RequestParam String uid,
            @RequestParam Integer postId
    ) {
        UserFavPost userFavPost = new UserFavPost();
        userFavPost.setUserId(uid);
        userFavPost.setPostId(postId);
        int n = this.userFavPostMapper.insert(userFavPost);

        return n > 0;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/favoured")
    List<Post> favoured(
            @RequestParam String uid
    ) {
        SelectStatementProvider selectStatementProvider = selectDistinct(
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
                .join(UserFavPostDynamicSqlSupport.userFavPost)
                .on(PostDynamicSqlSupport.postId, equalTo(UserFavPostDynamicSqlSupport.postId))
                .where(UserFavPostDynamicSqlSupport.userId, isEqualTo(uid))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return this.postMapper.selectMany(selectStatementProvider);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, path = "/cancelFav")
    Boolean cancelFav(
            @RequestParam String uid,
            @RequestParam Integer postId
    ) {
        int n = this.userFavPostMapper.delete(c -> c
                .where(UserFavPostDynamicSqlSupport.userId, isEqualTo(uid))
                .and(UserFavPostDynamicSqlSupport.postId, isEqualTo(postId))
        );
        return n > 0;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/isFav")
    Boolean isFav(
            @RequestParam String uid,
            @RequestParam Integer postId
    ) {
        List<UserFavPost> userFavPost = this.userFavPostMapper.select(c -> c
                .where(UserFavPostDynamicSqlSupport.userId, isEqualTo(uid))
                .and(UserFavPostDynamicSqlSupport.postId, isEqualTo(postId))
        );

        return !userFavPost.isEmpty();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/history")
    List<Post> history(
            @RequestParam String uid
    ) {
        SelectStatementProvider selectStatementProvider = selectDistinct(
                PostHitDynamicSqlSupport.postId
        )
                .from(PostHitDynamicSqlSupport.postHit)
                .where(PostHitDynamicSqlSupport.hitterId, isEqualTo(uid))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<PostHit> postHits = this.postHitMapper.selectMany(selectStatementProvider);
        List<Integer> idList = new ArrayList<>();
        postHits.forEach(e -> idList.add(e.getPostId()));

        if (idList.isEmpty()) return new ArrayList<>();

        return this.postMapper.select(c -> c
                .where(PostDynamicSqlSupport.postId, isIn(idList))
        );
    }
}
