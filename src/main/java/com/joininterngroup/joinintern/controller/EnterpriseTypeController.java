package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.mapper.EnterpriseTypeDynamicSqlSupport;
import com.joininterngroup.joinintern.mapper.EnterpriseTypeMapper;
import com.joininterngroup.joinintern.model.EnterpriseType;
import com.joininterngroup.joinintern.utils.Authority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enterprise-type")
@Slf4j
public class EnterpriseTypeController {

    private EnterpriseTypeMapper enterpriseTypeMapper;

    private Authority authority;

    public EnterpriseTypeController(
            EnterpriseTypeMapper enterpriseTypeMapper,
            Authority authority
    ) {
        this.enterpriseTypeMapper = enterpriseTypeMapper;
        this.authority = authority;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/get")
    List<EnterpriseType> getAllType() {
        return this.enterpriseTypeMapper.select(c -> c.orderBy(EnterpriseTypeDynamicSqlSupport.enterpriseTypeId));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/add")
    boolean addType(
            @RequestParam String id,
            @RequestParam String name
    ) {
        if (!this.authority.checkAdmin(id)) return false;
        EnterpriseType enterpriseType = new EnterpriseType();
        enterpriseType.setEnterpriseTypeName(name);
        this.enterpriseTypeMapper.insert(enterpriseType);
        log.info(String.format("Enterprise type %s if added, with id %d",
                enterpriseType.getEnterpriseTypeName(),
                enterpriseType.getEnterpriseTypeId()));
        return true;
    }
}
