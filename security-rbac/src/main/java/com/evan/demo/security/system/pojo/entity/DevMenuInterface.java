package com.evan.demo.security.system.pojo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dev_menu_interface")
public class DevMenuInterface {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer menuId;

    private String uri;

    private String description;
}
