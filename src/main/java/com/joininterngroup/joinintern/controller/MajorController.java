package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.mapper.MajorDynamicSqlSupport;
import com.joininterngroup.joinintern.mapper.MajorMapper;
import com.joininterngroup.joinintern.model.*;
import com.joininterngroup.joinintern.utils.Authority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/major")
@Slf4j
public class MajorController {

    private MajorMapper majorMapper;

    private Authority authority;

    public MajorController(
            MajorMapper majorMapper,
            Authority authority
    ) {
        this.majorMapper = majorMapper;
        this.authority = authority;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/get")
    List<Major> getAllMajor() {
        return this.majorMapper.select(c -> c.orderBy(MajorDynamicSqlSupport.majorId));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/add")
    boolean addMajor(
            @RequestParam String id,
            @RequestParam String name
    ) {
        if (!this.authority.checkAdmin(id)) return false;
        Major major = new Major();
        major.setMajorName(name);
        this.majorMapper.insert(major);
        log.info(String.format("Major %s is added, with id %d", major.getMajorName(), major.getMajorId()));
        return true;
    }
}
