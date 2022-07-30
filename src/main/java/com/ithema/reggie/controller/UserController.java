package com.ithema.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ithema.reggie.common.R;
import com.ithema.reggie.entity.User;
import com.ithema.reggie.service.UserService;
import com.ithema.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if (!StringUtils.isEmpty(phone)) {
            String s = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", s);

//            SMSUitls.sendMessage("瑞吉外卖","",phone,s);

            session.setAttribute(phone, s);

            return R.success("短信发送成功");
        }
        return R.error("短信发送失败");
    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {

        log.info(map.toString());
        String phone = map.get("phone").toString();

//        String code = map.get("code").toString();

        Object attribute = session.getAttribute(phone);

//        if(attribute != null && attribute.equals(code)) {

            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(userLambdaQueryWrapper);
            if(user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
//        }
//        return R.error("登录失败");
    }
}
