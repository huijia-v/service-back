package com.huijia.servicebackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author hujia
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 514775722981004835L;

    private String userAccount, userPassword;


}
