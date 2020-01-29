package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.mapper.LabelDynamicSqlSupport;
import com.joininterngroup.joinintern.mapper.LabelMapper;
import com.joininterngroup.joinintern.mapper.PostDynamicSqlSupport;
import com.joininterngroup.joinintern.mapper.PostLabelDynamicSqlSupport;
import com.joininterngroup.joinintern.model.Label;
import com.joininterngroup.joinintern.utils.Authority;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@RestController
@RequestMapping("/label")
public class LabelController {

    private LabelMapper labelMapper;

    private Authority authority;

    public LabelController(
            LabelMapper labelMapper,
            Authority authority
    ) {
        this.labelMapper = labelMapper;
        this.authority = authority;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/get")
    List<Label> getLabels() {
        return this.labelMapper.select(c -> c);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/add")
    boolean addLabel(
            @RequestParam String user_id,
            @RequestParam String name
    ) {
        if (!this.authority.checkAdmin(user_id)) return false;
        Label label = new Label();
        label.setLabelContent(name);

        this.labelMapper.insert(label);
        return true;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/get-post")
    List<Label> getPostLabels(@RequestParam Integer id) {
        SelectStatementProvider selectStatement = selectDistinct(
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

        return this.labelMapper.selectMany(selectStatement);
    }
}
