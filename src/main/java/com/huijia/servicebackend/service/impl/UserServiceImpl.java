package com.huijia.servicebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huijia.servicebackend.model.domain.User;
import com.huijia.servicebackend.service.UserService;
import com.huijia.servicebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author huijia
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2022-10-31 17:16:18
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    private static final String USER_LOGIN_STATE = "userLoginState";

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {


        //1。校验

        //用户名、密码等是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }


        //用户名不能小于4位
        if (userAccount.length() < 4) {
            return -1;
        }

        //密码不能小于8位
        if (userPassword.length() < 8 || checkPassword.length() < 8) {

            return -1;
        }



        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }

        //密码和校验码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }

        //用户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        int count = this.count(queryWrapper);
        if (count > 0) {
            return -1;
        }

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //插入数据
        User user = new User();
        user.setUseraccount(userAccount);
        user.setUserpassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }


        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {


        //1。校验

        //用户名、密码等是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }


        //用户名不能小于4位
        if (userAccount.length() < 4) {
            return null;
        }

        //密码不能小于8位
        if (userPassword.length() < 8) {

            return null;
        }



        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);

        //用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }

        //用户脱敏
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUseraccount(user.getUseraccount());
        safetyUser.setAvatarurl(user.getAvatarurl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserstatus(user.getUserstatus());
        safetyUser.setCreatetime(user.getCreatetime());

        //记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);



        return safetyUser;
    }
}




