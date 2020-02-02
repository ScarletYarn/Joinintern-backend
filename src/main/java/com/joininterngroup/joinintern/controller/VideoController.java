package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.mapper.VideoHitMapper;
import com.joininterngroup.joinintern.mapper.VideoDynamicSqlSupport;
import com.joininterngroup.joinintern.mapper.VideoMapper;
import com.joininterngroup.joinintern.model.Video;
import com.joininterngroup.joinintern.model.VideoHit;
import com.joininterngroup.joinintern.utils.Authority;
import com.joininterngroup.joinintern.utils.FileService;
import com.joininterngroup.joinintern.utils.JoinInternEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

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


    public VideoController(VideoMapper videoMapper, FileService fileService, Authority authority, VideoHitMapper videoHitMapper, JoinInternEnvironment joinInternEnvironment) {
        this.videoMapper = videoMapper;
        this.fileService = fileService;
        this.authority = authority;
        this.videoHitMapper = videoHitMapper;
        this.joinInternEnvironment = joinInternEnvironment;
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
        dir += "video/";
        String path = this.fileService.saveFile(dir, file);

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
}
