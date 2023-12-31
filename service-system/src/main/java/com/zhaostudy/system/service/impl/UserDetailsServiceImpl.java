package com.zhaostudy.system.service.impl;

import com.zhaostudy.model.system.SysUser;
import com.zhaostudy.system.custom.CustomUser;
import com.zhaostudy.system.service.SysMenuService;
import com.zhaostudy.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @User：mac
 * @Author: yykk
 * @Date: 2022/11/04/5:56 PM
 * @Description: https://www.cnblogs.com/zhaostudy/
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getUserInfoByUserName(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在！");
        }
        if (sysUser.getStatus() == 0) {
            throw new RuntimeException("用户已经被禁用!");
        }
        // 根据userid查询操作权限数据
        List<String> userPermsList = sysMenuService.getUserButtonList(sysUser.getId());
        // 转换成security要求数据格式
        List<SimpleGrantedAuthority> authority = new ArrayList<>();
        for (String perm : userPermsList) {
            authority.add(new SimpleGrantedAuthority(perm.trim()));
        }
        return new CustomUser(sysUser, authority);
    }
}
