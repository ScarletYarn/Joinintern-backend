package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.mapper.VideoClickMapper;
import com.joininterngroup.joinintern.mapper.VideoDynamicSqlSupport;
import com.joininterngroup.joinintern.mapper.VideoMapper;
import com.joininterngroup.joinintern.model.Video;
import com.joininterngroup.joinintern.model.VideoClick;
import com.joininterngroup.joinintern.utils.Authority;
import com.joininterngroup.joinintern.utils.FileFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/video")
@Slf4j
public class VideoController {

    private VideoMapper videoMapper;

    private FileFetcher fileFetcher;

    private Authority authority;

    private VideoClickMapper videoClickMapper;

    public VideoController(
            VideoMapper videoMapper,
            FileFetcher fileFetcher,
            Authority authority,
            VideoClickMapper videoClickMapper
    ) {
        this.videoMapper = videoMapper;
        this.fileFetcher = fileFetcher;
        this.authority = authority;
        this.videoClickMapper = videoClickMapper;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/get")
    List<Video> getAllVideos() {
        return this.videoMapper.select(c -> c);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/test")
    void testFetch(@RequestParam String url) {
        String[] strings = url.split("/");
        this.fileFetcher.getFile(url, "test", strings[strings.length - 1]);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/upload")
    void uploadVideo(
            @RequestParam String url,
            @RequestParam(required = false) String videoDescription,
            @RequestParam(required = false) String userId
    ) {
        String[] strings = url.split("/");
        String path = this.fileFetcher.getFile(url, "video", strings[strings.length - 1]);

        Video video = new Video();
        video.setVideoDescription(videoDescription);
        video.setVideoPath(path);
        video.setChecked("unchecked");
        video.setPosterId(userId);
        video.setPostDate(new Date());

        this.videoMapper.insert(video);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/update")
    boolean updateVideo(
            @RequestParam Integer id,
            @RequestParam(required = false) String videoDescription,
            @RequestParam String user_id
    ) {
        Optional<Video> video = this.videoMapper.selectOne(c -> c.where(VideoDynamicSqlSupport.videoId, isEqualTo(id)));
        if (!video.isPresent()) return false;
        if (!video.get().getPosterId().equals(user_id) && !this.authority.checkAdmin(user_id)) return false;
        this.videoMapper.update(c -> c.set(VideoDynamicSqlSupport.videoDescription).equalToWhenPresent(videoDescription));
        return true;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/check")
    boolean checkVideo(
            @RequestParam String user_id,
            @RequestParam Integer id,
            @RequestParam Boolean pass
    ) {
        String status = pass ? "pass" : "reject";
        if (!this.authority.checkAdmin(user_id)) return false;
        this.videoMapper.update(c -> c.set(VideoDynamicSqlSupport.checked)
                .equalTo(status)
                .set(VideoDynamicSqlSupport.checkerId)
                .equalTo(user_id)
                .set(VideoDynamicSqlSupport.checkDate)
                .equalTo(new Date())
                .where(VideoDynamicSqlSupport.videoId, isEqualTo(id))
        );

        return true;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/click")
    void clickVideo(
            @RequestParam String user_id,
            @RequestParam Integer id
    ) {
        VideoClick videoClick = new VideoClick();

        videoClick.setClickerId(user_id);
        videoClick.setVideoClickTime(new Date());
        videoClick.setVideoId(id);

        this.videoClickMapper.insert(videoClick);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/hits")
    Long getHits(
            @RequestParam Integer videoId
    ) {
        return this.videoClickMapper.count(c -> c.where(VideoDynamicSqlSupport.videoId, isEqualTo(videoId)));
    }
}
