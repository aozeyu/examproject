package com.wzz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzz.entity.User;
import com.wzz.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
class ExamAdminApplicationTests {

    @Autowired
    private UserServiceImpl userService;

    @Test
    void contextLoads() {
//        String s = "[{'topMenuName':'控制台','icon':'el-icon-odometer'},{'topMenuName':'在线考试','topIcon':'el-icon-menu','submenu':[{'name':'在线考试','icon':'el-icon-s-promotion','url':''},{'name':'我的成绩','icon':'el-icon-trophy','url':''},{'name':'我的题库','icon':'el-icon-notebook-2','url':''}]},{'topMenuName':'考试管理','topIcon':'el-icon-bangzhu','submenu':[{'name':'题库管理','icon':'el-icon-aim','url':''},{'name':'试题管理','icon':'el-icon-news','url':''},{'name':'考试管理','icon':'el-icon-tickets','url':''},{'name':'阅卷管理','icon':'el-icon-view','url':''}]},{'topMenuName':'考试统计','topIcon':'el-icon-pie-chart','submenu':[{'name':'统计总览','icon':'el-icon-data-line','url':''},{'name':'部门统计','icon':'el-icon-data-analysis','url':''}]},{'topMenuName':'系统设置','topIcon':'el-icon-setting','submenu':[{'name':'角色管理','icon':'el-icon-s-custom','url':''},{'name':'用户管理','icon':'el-icon-user-solid','url':''}]}]";
//        s = s.replaceAll("'","\"");
//        System.out.println(s);
        IPage<User> userPage = new Page<>(2, 2);//参数一是当前页，参数二是每页个数
        userPage = userService.page(userPage, null);
        List<User> list = userPage.getRecords();
        for(User user : list){
            System.out.println(user);
        }
    }

}
