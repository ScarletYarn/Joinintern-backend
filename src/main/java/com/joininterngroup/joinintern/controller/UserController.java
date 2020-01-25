package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.mapper.*;
import com.joininterngroup.joinintern.model.MyUser;
import com.joininterngroup.joinintern.utils.Authority;
import com.joininterngroup.joinintern.utils.WeixinController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private MyUserMapper myUserMapper;

    private WeixinController weixinController;

    private Authority authority;

    public UserController(
            MyUserMapper myUserMapper,
            WeixinController weixinController,
            Authority authority) {
        this.myUserMapper = myUserMapper;
        this.weixinController = weixinController;
        this.authority = authority;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/get")
    List<MyUser> getAllUser() {
        return this.myUserMapper.select(c -> c);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/admin/get")
    List<MyUser> getAdmin() {
        return this.myUserMapper.select(c ->
                c.where(MyUserDynamicSqlSupport.userIdentity, isEqualTo("admin")));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/login")
    String login(
            @RequestParam String code,
            @RequestParam String nickname,
            @RequestParam String avatar
    ) {
        this.myUserMapper.update(c ->
                c.set(MyUserDynamicSqlSupport.nickname).equalTo(nickname)
                        .set(MyUserDynamicSqlSupport.avatar).equalTo(avatar));

        log.info(String.format("User %s login", nickname));

        return this.weixinController.getOpenid(code);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/register")
    String register(
            @RequestParam String code,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) Integer major,
            @RequestParam(required = false) String cardPhotoPath,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String avatar,
            @RequestParam(required = false) Integer enterpriseTypeId
    ) {
        String openid = this.weixinController.getOpenid(code);
        MyUser myUser = new MyUser();
        if (gender != null) myUser.setGender(gender);
        if (level != null) myUser.setLevel(level);
        if (major != null) myUser.setMajor(major);
        if (cardPhotoPath != null) myUser.setCardPhotoPath(cardPhotoPath);
        myUser.setChecked("unchecked");
        myUser.setUserIdentity("stu");
        if (nickname != null) myUser.setNickname(nickname);
        if (avatar != null) myUser.setAvatar(avatar);
        if (enterpriseTypeId != null) myUser.setEnterpriseTypeId(enterpriseTypeId);
        this.myUserMapper.insert(myUser);
        log.info(String.format("User %s registers", myUser.getNickname()));
        return openid;
    }

    /**
     * Check the registration.
     * @param id The open id of the user to be checked.
     * @param op The check operation: pass or reject.
     * @return whether success or not.
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/check")
    boolean checkUser(
            @RequestParam String id,
            @RequestParam String op
    ) {
        if (!this.authority.checkAdmin(id)) return false;
        Optional<MyUser> res = this.myUserMapper.selectOne(c ->
                c.where(MyUserDynamicSqlSupport.userId, isEqualTo(id)));

        if (res.isPresent()) {
            if (op.equals("pass")) {
                this.myUserMapper.update(c -> c.set(MyUserDynamicSqlSupport.checked).equalTo("pass")
                        .where(MyUserDynamicSqlSupport.userId, isEqualTo(id)));
                return true;
            }
            else if (op.equals("reject")) {
                this.myUserMapper.update(c -> c.set(MyUserDynamicSqlSupport.checked).equalTo("reject")
                        .where(MyUserDynamicSqlSupport.userId, isEqualTo(id)));
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/update")
    boolean updateInformation(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) Integer major,
            @RequestParam(required = false) Integer enterpriseTypeId,
            @RequestParam String id
    ) {
        this.myUserMapper.update(c -> c.set(MyUserDynamicSqlSupport.gender)
                .equalToWhenPresent(gender)
                .set(MyUserDynamicSqlSupport.level)
                .equalToWhenPresent(level)
                .set(MyUserDynamicSqlSupport.major)
                .equalToWhenPresent(major)
                .set(MyUserDynamicSqlSupport.enterpriseTypeId)
                .equalToWhenPresent(enterpriseTypeId)
                .where(MyUserDynamicSqlSupport.userId, isEqualTo(id))
        );
        return true;
    }

    @RequestMapping("/admin/grant")
    @ResponseBody
    boolean grantAdminPrivileges(
            @RequestParam String admin_id,
            @RequestParam String open_id
    ) {
        if (!this.authority.checkAdmin(admin_id)) return false;
        this.myUserMapper.update(c ->
                c.set(MyUserDynamicSqlSupport.userIdentity)
                        .equalTo("admin")
                        .where(MyUserDynamicSqlSupport.userId, isEqualTo(open_id)));
        return true;
    }
}
