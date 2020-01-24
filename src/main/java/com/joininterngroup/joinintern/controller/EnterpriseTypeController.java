package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.mapper.EnterpriseTypeMapper;
import com.joininterngroup.joinintern.model.EnterpriseType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/enterprise-type")
public class EnterpriseTypeController {

    private EnterpriseTypeMapper enterpriseTypeMapper;

    public EnterpriseTypeController(EnterpriseTypeMapper enterpriseTypeMapper) {
        this.enterpriseTypeMapper = enterpriseTypeMapper;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/get")
    List<EnterpriseType> getAllType() {
        return this.enterpriseTypeMapper.select(c -> c);
    }
}
