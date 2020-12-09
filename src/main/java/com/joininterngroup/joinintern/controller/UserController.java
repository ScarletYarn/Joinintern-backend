package com.joininterngroup.joinintern.controller;

import com.joininterngroup.joinintern.helpers.UserEssential;
import com.joininterngroup.joinintern.mapper.*;
import com.joininterngroup.joinintern.model.MyUser;
import com.joininterngroup.joinintern.utils.Authority;
import com.joininterngroup.joinintern.utils.FileService;
import com.joininterngroup.joinintern.utils.JoinInternEnvironment;
import com.joininterngroup.joinintern.utils.WeixinController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    private FileService fileService;

    private JoinInternEnvironment joinInternEnvironment;

    public UserController(
            MyUserMapper myUserMapper,
            WeixinController weixinController,
            Authority authority,
            FileService fileService,
            JoinInternEnvironment joinInternEnvironment) {
        this.myUserMapper = myUserMapper;
        this.weixinController = weixinController;
        this.authority = authority;
        this.fileService = fileService;
        this.joinInternEnvironment = joinInternEnvironment;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/get")
    List<MyUser> getAllUser() {
        return this.myUserMapper.select(c -> c);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, path = "/query")
    UserEssential query(
            @RequestParam String user_id
    ) {
        Optional<MyUser> user = this.myUserMapper.selectOne(c ->
                c.where(MyUserDynamicSqlSupport.userId, isEqualTo(user_id)));
        if (!user.isPresent()) return null;
        UserEssential userEssential = new UserEssential();
        userEssential.setUserId(user_id);
        userEssential.setNickname(user.get().getNickname());
        userEssential.setAvatar(user.get().getAvatar());
        return userEssential;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/find")
    MyUser find(
            @RequestParam String uid
    ) {
        Optional<MyUser> user = this.myUserMapper.selectOne(c ->
                c.where(MyUserDynamicSqlSupport.userId, isEqualTo(uid)));
        return user.orElse(null);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/super/login")
    boolean superLogin(
            @RequestParam String uid,
            @RequestParam String password
    ) {
        String code;
        code = DigestUtils.md5DigestAsHex(password.getBytes());
        boolean res =  uid.equals("ultra master") && code.equals("541fec3d28258cde4d1a9f92fb687f39");
        if (res) log.info("Super master login");
        return res;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/admin/get")
    List<MyUser> getAdmin() {
        return this.myUserMapper.select(c -> c.where(MyUserDynamicSqlSupport.userIdentity, isEqualTo("admin")));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/openid")
    String getOpenId(@RequestParam String code) {
        return this.weixinController.getOpenid(code);
    }

    @Deprecated
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/login")
    String login(
            @RequestParam String code,
            @RequestParam String nickname,
            @RequestParam String avatar
    ) {
        this.myUserMapper.update(c -> c
                .set(MyUserDynamicSqlSupport.nickname).equalTo(nickname)
                .set(MyUserDynamicSqlSupport.avatar).equalTo(avatar)
                .where(MyUserDynamicSqlSupport.userId, isEqualTo(this.weixinController.getOpenid(code)))
        );

        log.info(String.format("User %s login", nickname));

        return this.weixinController.getOpenid(code);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/register")
    MyUser register(
            @RequestParam String code,
            @RequestParam String stuId,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) Integer major,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String avatar,
            @RequestParam(required = false) Integer enterpriseTypeId,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer type
    ) {
        String dir = "media/";
        if (!this.joinInternEnvironment.isProd()) dir += "dev/";
        String cardPhotoPath = this.fileService.saveFile(dir + "photo/", file);

        String openid = this.weixinController.getOpenid(code);
        MyUser myUser = new MyUser();
        myUser.setUserId(openid);
        myUser.setStudentId(stuId);
        if (gender != null) myUser.setGender(gender);
        if (level != null) myUser.setLevel(level);
        if (major != null) myUser.setMajor(major);
        myUser.setEnterpriseTypeId(type);
        if (cardPhotoPath != null) myUser.setCardPhotoPath(cardPhotoPath);
        myUser.setValidation("unvalidated");
        myUser.setUserIdentity("stu");
        if (nickname != null) myUser.setNickname(nickname);
        if (avatar != null) myUser.setAvatar(avatar);
        if (enterpriseTypeId != null) myUser.setEnterpriseTypeId(enterpriseTypeId);
        myUser.setDescription(description);
        this.myUserMapper.insert(myUser);
        log.info(String.format("User %s registers", myUser.getNickname()));
        return myUser;
    }

    /**
     * validate the registration.
     * @param id The open id of the user to be validated.
     * @param op The check operation: pass or reject.
     * @return whether success or not.
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/validate")
    boolean validateUser(
            @RequestParam String id,
            @RequestParam String op
    ) {
//        if (!this.authority.checkAdmin(id)) return false;
        Optional<MyUser> res = this.myUserMapper.selectOne(c ->
                c.where(MyUserDynamicSqlSupport.userId, isEqualTo(id)));

        if (res.isPresent()) {
            if (op.equals("validate")) {
                this.myUserMapper.update(c -> c.set(MyUserDynamicSqlSupport.validation).equalTo("validate")
                        .where(MyUserDynamicSqlSupport.userId, isEqualTo(id)));
                return true;
            }
            else if (op.equals("invalidate")) {
                this.myUserMapper.update(c -> c.set(MyUserDynamicSqlSupport.validation).equalTo("invalidate")
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
        int n = this.myUserMapper.update(c -> c
                .set(MyUserDynamicSqlSupport.gender)
                .equalToWhenPresent(gender)
                .set(MyUserDynamicSqlSupport.level)
                .equalToWhenPresent(level)
                .set(MyUserDynamicSqlSupport.major)
                .equalToWhenPresent(major)
                .set(MyUserDynamicSqlSupport.enterpriseTypeId)
                .equalToWhenPresent(enterpriseTypeId)
                .where(MyUserDynamicSqlSupport.userId, isEqualTo(id))
        );
        return n > 0;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/admin/grant")
    @ResponseBody
    boolean grantAdminPrivileges(
            @RequestParam String admin_id,
            @RequestParam String open_id
    ) {
        if (!this.authority.checkAdmin(admin_id)) return false;
        int n = this.myUserMapper.update(c -> c
                .set(MyUserDynamicSqlSupport.userIdentity)
                .equalTo("admin")
                .where(MyUserDynamicSqlSupport.userId, isEqualTo(open_id))
        );
        return n > 0;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/gra")
    @ResponseBody
    boolean grantGra(
            @RequestParam String admin_id,
            @RequestParam String open_id
    ) {
        if (!this.authority.checkAdmin(admin_id)) return false;
        int n = this.myUserMapper.update(c -> c
                .set(MyUserDynamicSqlSupport.userIdentity)
                .equalTo("gra")
                .where(MyUserDynamicSqlSupport.userId, isEqualTo(open_id))
        );
        return n > 0;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, path = "/graList")
    List<MyUser> graList() {
        return this.myUserMapper.select(c -> c
                .where(MyUserDynamicSqlSupport.userIdentity, isEqualTo("gra"))
        );
    }
}
