package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.mapper.*;
import com.joininterngroup.joinintern.model.UserFavVideo;
import com.joininterngroup.joinintern.model.Video;
import com.joininterngroup.joinintern.model.VideoHit;
import com.joininterngroup.joinintern.utils.Authority;
import com.joininterngroup.joinintern.utils.FileService;
import com.joininterngroup.joinintern.utils.JoinInternEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/video")
@Slf4j
public class VideoController {

    private VideoMapper videoMapper;

    private FileService fileService;

    private Authority authority;

    private VideoHitMapper videoHitMapper;

    private JoinInternEnvironment joinInternEnvironment;

    private UserFavVideoMapper userFavVideoMapper;

    public VideoController(
            VideoMapper videoMapper,
            FileService fileService,
            Authority authority,
            VideoHitMapper videoHitMapper,
            JoinInternEnvironment joinInternEnvironment,
            UserFavVideoMapper userFavVideoMapper
    ) {
        this.videoMapper = videoMapper;
        this.fileService = fileService;
        this.authority = authority;
        this.videoHitMapper = videoHitMapper;
        this.joinInternEnvironment = joinInternEnvironment;
        this.userFavVideoMapper = userFavVideoMapper;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/query")
    Video query(
            @RequestParam Integer videoId
    ) {
        Optional<Video> video = this.videoMapper.selectOne(c -> c.where(VideoDynamicSqlSupport.videoId, isEqualTo(videoId)));
        return video.orElse(null);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/delete")
    boolean delete(
            @RequestParam String uid,
            @RequestParam Integer id
    ) {
        if (!this.authority.checkAdmin(uid)) return false;
        Optional<Video> video = this.videoMapper.selectOne(
                c -> c.where(VideoDynamicSqlSupport.videoId, isEqualTo(id))
        );
        if (!video.isPresent()) return false;
        this.fileService.deleteFile(video.get().getVideoPath());
        int n = this.videoMapper.delete(
                c -> c.where(VideoDynamicSqlSupport.videoId, isEqualTo(id))
        );
        if (n > 0) log.info(String.format("Video %d with path %s is deleted", id, video.get().getVideoPath()));
        return n > 0;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/get")
    List<Video> getAllVideos() {
        return this.videoMapper.select(c -> c);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/get/validate")
    List<Video> getValidate() {
        return this.videoMapper.select(c -> c
                .where(VideoDynamicSqlSupport.validation, isEqualTo("validate"))
        );
    }

    @RequestMapping(method = RequestMethod.POST, path = "/test")
    void testFetch(@RequestParam String url) {
        String[] strings = url.split("/");

        this.fileService.getFile(url, "test", strings[strings.length - 1]);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/upload")
    boolean uploadVideo(
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String videoTitle,
            @RequestParam(required = false) String videoDescription,
            @RequestParam(required = false) String userId
    ) {

        String dir = "media/";
        if (!this.joinInternEnvironment.isProd()) dir += "dev/";
        String path = this.fileService.saveFile(dir + "video/", file);

        Video video = new Video();
        video.setVideoTitle(videoTitle);
        video.setVideoDescription(videoDescription);
        video.setVideoPath(path);
        video.setValidation("unvalidated");
        video.setPosterId(userId);
        video.setPostDate(new Date());

        int n = this.videoMapper.insert(video);
        if (n > 0) log.info(String.format("Video %d is uploaded with path %s", video.getVideoId(), video.getVideoPath()));
        return n > 0;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/update")
    boolean updateVideo(
            @RequestParam Integer id,
            @RequestParam(required = false) String videoTitle,
            @RequestParam(required = false) String videoDescription,
            @RequestParam String user_id
    ) {
        Optional<Video> video = this.videoMapper.selectOne(c -> c.where(VideoDynamicSqlSupport.videoId, isEqualTo(id)));
        if (!video.isPresent()) return false;
        if (!video.get().getPosterId().equals(user_id) && !this.authority.checkAdmin(user_id)) return false;
        int n = this.videoMapper.update(c ->
                c.set(VideoDynamicSqlSupport.videoDescription)
                        .equalToWhenPresent(videoDescription)
                .set(VideoDynamicSqlSupport.videoTitle)
                .equalToWhenPresent(videoTitle)
        );
        if (n > 0) log.info(String.format("String video %d is updated", id));
        return n > 0;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/validate")
    boolean validateVideo(
            @RequestParam String user_id,
            @RequestParam Integer id,
            @RequestParam Boolean pass
    ) {
        String status = pass ? "validate" : "invalidate";
        if (!this.authority.checkAdmin(user_id)) return false;
        this.videoMapper.update(c -> c.set(VideoDynamicSqlSupport.validation)
                .equalTo(status)
                .set(VideoDynamicSqlSupport.validatorId)
                .equalTo(user_id)
                .set(VideoDynamicSqlSupport.validateDate)
                .equalTo(new Date())
                .where(VideoDynamicSqlSupport.videoId, isEqualTo(id))
        );

        return true;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/hit")
    void hitVideo(
            @RequestParam String user_id,
            @RequestParam Integer id
    ) {
        VideoHit videoClick = new VideoHit();

        videoClick.setHitterId(user_id);
        videoClick.setVideoHitTime(new Date());
        videoClick.setVideoId(id);

        this.videoHitMapper.insert(videoClick);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/hits")
    Long getHits(
            @RequestParam Integer videoId
    ) {
        return this.videoHitMapper.count(c -> c.where(VideoDynamicSqlSupport.videoId, isEqualTo(videoId)));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/fav")
    Boolean fav(
            @RequestParam String uid,
            @RequestParam Integer videoId
    ) {
        UserFavVideo userFavVideo = new UserFavVideo();
        userFavVideo.setUserId(uid);
        userFavVideo.setVideoId(videoId);
        int n = this.userFavVideoMapper.insert(userFavVideo);
        return n > 0;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/favoured")
    List<Video> favoured(
            @RequestParam String uid
    ) {
        SelectStatementProvider selectStatementProvider = selectDistinct(
                VideoDynamicSqlSupport.videoId,
                VideoDynamicSqlSupport.validateDate,
                VideoDynamicSqlSupport.validation,
                VideoDynamicSqlSupport.validatorId,
                VideoDynamicSqlSupport.videoTitle,
                VideoDynamicSqlSupport.videoPath,
                VideoDynamicSqlSupport.videoThumb,
                VideoDynamicSqlSupport.videoDescription,
                VideoDynamicSqlSupport.postDate
        )
                .from(VideoDynamicSqlSupport.video)
                .join(UserFavVideoDynamicSqlSupport.userFavVideo)
                .on(VideoDynamicSqlSupport.videoId, equalTo(UserFavVideoDynamicSqlSupport.videoId))
                .where(UserFavVideoDynamicSqlSupport.userId, isEqualTo(uid))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return this.videoMapper.selectMany(selectStatementProvider);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, path = "/cancelFav")
    Boolean cancelFav(
            @RequestParam String uid,
            @RequestParam Integer videoId
    ) {
        int n = this.userFavVideoMapper.delete(c -> c
                .where(UserFavVideoDynamicSqlSupport.userId, isEqualTo(uid))
                .and(UserFavVideoDynamicSqlSupport.videoId, isEqualTo(videoId))
        );

        return n > 0;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/isFav")
    Boolean isFav(
            @RequestParam String uid,
            @RequestParam Integer videoId
    ) {
        List<UserFavVideo> userFavVideo = this.userFavVideoMapper.select(c -> c
                .where(UserFavVideoDynamicSqlSupport.userId, isEqualTo(uid))
                .and(UserFavVideoDynamicSqlSupport.videoId, isEqualTo(videoId))
        );

        return !userFavVideo.isEmpty();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/history")
    List<Video> history(
            @RequestParam String uid
    ) {
        SelectStatementProvider selectStatementProvider = selectDistinct(
                VideoHitDynamicSqlSupport.videoId
        )
                .from(VideoHitDynamicSqlSupport.videoHit)
                .where(VideoHitDynamicSqlSupport.hitterId, isEqualTo(uid))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        List<VideoHit> videoHits = this.videoHitMapper.selectMany(selectStatementProvider);
        ArrayList<Integer> idList = new ArrayList<>();
        videoHits.forEach(e -> idList.add(e.getVideoId()));

        if (idList.isEmpty()) return new ArrayList<>();

        return this.videoMapper.select(c -> c
                .where(VideoDynamicSqlSupport.videoId, isIn(idList))
        );
    }
}
