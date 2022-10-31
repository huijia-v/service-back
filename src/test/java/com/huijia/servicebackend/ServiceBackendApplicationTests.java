package com.huijia.servicebackend;

import com.huijia.servicebackend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ServiceBackendApplicationTests {

    @Resource
    private UserService userService;
    @Test
    void contextLoads() {
    }

    @Test
    public void testAddUser() {

        String userAccount = "huijia5";
        String password = "12345678";
        String check = "12345678";
        long l = userService.userRegister(userAccount, password, check);
        Assertions.assertEquals(l > 0, l > 0);
        //assertEquals == assertTrue  实际运行结果与预期结果进行比较，一般测试用
    }

}
