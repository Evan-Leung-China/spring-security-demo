package com.evan.demo.security.system.pojo.dto;

import com.evan.demo.security.constants.ModifyType;
import lombok.Data;

@Data
public class UpdateAuthorityDTO {
    private ModifyType modifyType;

    private String roleCode;

    private Integer roleId;

    private String menuUri;
}
