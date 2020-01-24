package com.joininterngroup.joinintern.utils;

import com.joininterngroup.joinintern.mapper.MyUserMapper;
import com.joininterngroup.joinintern.model.MyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.joininterngroup.joinintern.mapper.MyUserDynamicSqlSupport;

import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Component
@Slf4j
public class Authority {

    private MyUserMapper myUserMapper;

    public Authority(MyUserMapper myUserMapper) {
        this.myUserMapper = myUserMapper;
    }

    public boolean checkAdmin(String open_id) {
        Optional<MyUser> myUser = this.myUserMapper.selectOne(c ->
                c.where(MyUserDynamicSqlSupport.userId, isEqualTo(open_id)));

        if (!myUser.isPresent()) {
            log.info(String.format("Nonexistent user %s", open_id));
            return false;
        } else if (myUser.get().getUserIdentity().equals("admin")) {
            return true;
        } else {
            log.info(String.format("User %s with openid %s attempted to be an administrator.",
                    myUser.get().getNickname(), myUser.get().getUserId()));
            return false;
        }
    }
}
