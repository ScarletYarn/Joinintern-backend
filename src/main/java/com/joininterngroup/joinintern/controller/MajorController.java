package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.mapper.MajorMapper;
import com.joininterngroup.joinintern.model.Major;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/major")
public class MajorController {

    private MajorMapper majorMapper;

    public MajorController(MajorMapper majorMapper) {
        this.majorMapper = majorMapper;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/get")
    List<Major> getAllMajor() {
        return this.majorMapper.select(c -> c);
    }
}
