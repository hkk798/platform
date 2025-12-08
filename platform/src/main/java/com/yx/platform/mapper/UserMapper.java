package com.yx.platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.yx.platform.entity.SysUser; // ⚠️确保这里导入了你刚才写的实体类
import java.util.List;

// @Mapper 告诉 Spring：这个接口是用来操作数据库的
@Mapper
public interface UserMapper {

    // @Select 里的就是我们刚才在 DBeaver 里运行过的 SQL 语句
    // 这行代码的意思是：执行这句 SQL，然后把结果封装成 SysUser 列表返回给我们
    @Select("SELECT * FROM sys_user")
    List<SysUser> findAll();

    // 新增：插入一条用户数据
    // #{username} 的意思是：把 Java 传进来的 username 填到这里
    @org.apache.ibatis.annotations.Insert("INSERT INTO sys_user(username, password, role) VALUES(#{username}, #{password}, 'user')")
    void save(SysUser user);

    // 新增：登录查询
    // 意思就是：去找找有没有同时满足 "username=?" 和 "password=?" 的人
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND password = #{password}")
    SysUser login(String username, String password);
}
