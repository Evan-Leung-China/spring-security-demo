package com.evan.demo.security.system.user.dao;

import com.evan.demo.security.system.user.pojo.dto.RoleDTO;
import com.evan.demo.security.system.user.pojo.entity.DevMenu;
import com.evan.demo.security.system.user.pojo.entity.DevUserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevUserRoleRepository extends CrudRepository<DevUserRole, Integer> {

    List<DevUserRole> findByUserId(Integer userId);

    @Query("select m " +
            "from DevUserRole ur " +
            "inner join DevRoleMenu rm on rm.roleId = ur.roleId " +
            "inner join DevMenu m on m.id = rm.menuId " +
            "where ur.userId = :userId")
    List<DevMenu> findMenuByUserId(@Param("userId") Integer userId);
}
