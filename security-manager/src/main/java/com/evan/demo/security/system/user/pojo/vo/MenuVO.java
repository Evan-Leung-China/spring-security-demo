package com.evan.demo.security.system.user.pojo.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class MenuVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8503932726976990975L;

    private Integer id;
    private String menuCode;
    private String menuName;
    private List<MenuVO> childList;
    private String menuUri;
    private String menuIcon;
    private String menuType;
}
