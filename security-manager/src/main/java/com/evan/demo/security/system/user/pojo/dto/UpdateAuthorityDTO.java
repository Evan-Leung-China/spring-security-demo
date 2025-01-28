package com.evan.demo.security.system.user.pojo.dto;

import com.evan.demo.security.core.constants.ModifyType;
import lombok.Data;

@Data
public class UpdateAuthorityDTO {
    private ModifyType modifyType;

    private String roleCode;

    private Integer roleId;

    private String menuUri;
}
