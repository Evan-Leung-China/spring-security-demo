package com.evan.demo.security.system.dao;

import com.evan.demo.security.system.pojo.dto.RoleDTO;
import com.evan.demo.security.system.pojo.entity.DevMenu;
import com.evan.demo.security.system.pojo.entity.DevUserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevUserRoleRepositories extends CrudRepository<DevUserRole, Integer> {

    List<DevUserRole> findByUserId(Integer userId);

    @Query("select new com.evan.demo.security.system.pojo.dto.RoleDTO(r.id, r.roleCode) " +
            "from DevRole r inner join DevUserRole ur on ur.roleId = r.id " +
            "where ur.userId = :userId")
    List<RoleDTO> findRoleByUserId(@Param("userId") Integer userId);


    @Query("select m " +
            "from DevUserRole ur " +
            "inner join DevRoleMenu rm on rm.roleId = ur.roleId " +
            "inner join DevMenu m on m.id = rm.menuId " +
            "where ur.userId = :userId")
    List<DevMenu> findMenuByUserId(@Param("userId") Integer userId);
}
