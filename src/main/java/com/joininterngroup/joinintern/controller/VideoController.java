package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.mapper.VideoMapper;
import com.joininterngroup.joinintern.model.Video;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/video")
@Slf4j
public class VideoController {

    private VideoMapper videoMapper;

    public VideoController(VideoMapper videoMapper) {
        this.videoMapper = videoMapper;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/get")
    List<Video> getAllVideos() {
        return this.videoMapper.select(c -> c);
    }
}
