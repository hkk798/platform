package com.yx.platform.controller;

import com.yx.platform.entity.SysUser;
import com.yx.platform.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @RestController 告诉 Spring：我是个服务员，专门负责给浏览器返回数据（通常是 JSON 格式）
@RestController
public class UserController {

    // @Autowired 意思是：自动注入。
    // 就像服务员不需要自己招聘厨师，餐厅（Spring）会自动给它分配一个做好了的 UserMapper
    @Autowired
    private UserMapper userMapper;

    // @GetMapping("/user") 意思是：
    // 当浏览器访问地址 http://localhost:8080/user 时，就运行下面这个方法
    @GetMapping("/user")
    public List<SysUser> list() {
        // 调用 Mapper 去数据库查所有用户，然后直接返回
        return userMapper.findAll();
    }

    // 新增：注册接口
    // @PostMapping 专门用来接收“保存/新增”数据的请求
    // @RequestBody 的意思是：请把浏览器发来的 JSON 数据，自动拼装成 SysUser 对象
    @org.springframework.web.bind.annotation.PostMapping("/register")
    public String register(@org.springframework.web.bind.annotation.RequestBody SysUser user) {
        // 1. 调用 Mapper 把数据存进数据库
        userMapper.save(user);
        // 2. 告诉浏览器：搞定了！
        return "注册成功！";
    }


    // =========== 临时测试用的作弊通道 ===========
    // 注意：平时开发不能这么写，因为把密码放在网址里不安全。
    // 但为了测试，我们先这样用一下！
    @GetMapping("/test/register")
    public String testRegister(String username, String password) {
        // 1. 手动创建一个新用户对象
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(password);

        // 2. 让 Mapper 把这个对象存进去
        userMapper.save(user);

        return "测试注册成功！用户：" + username;
    }

    // =========== 临时测试登录接口 ===========
    @GetMapping("/test/login")
    public String testLogin(String username, String password) {
        // 1. 调用 Mapper 去查查有没有这个人
        SysUser user = userMapper.login(username, password);

        // 2. 判断结果
        if (user != null) {
            // 如果 user 不是空的，说明查到了 -> 登录成功
            return "登录成功！欢迎回来，" + user.getUsername();
        } else {
            // 如果 user 是空的，说明没查到 -> 登录失败
            return "登录失败！账号或密码错啦！";
        }
    }
}