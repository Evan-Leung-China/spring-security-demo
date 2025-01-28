package com.evan.demo.security.system.user.dao;

import com.evan.demo.security.system.user.pojo.entity.DevMenu;
import com.evan.demo.security.system.user.pojo.entity.DevRoleMenu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevRoleMenuRepository extends CrudRepository<DevRoleMenu, Integer> {

    /**
     * 查询所有url所需权限 -- 授权到按钮级别的SQL使用该SQL
     */
    @Query("select c " +
            "from DevRoleMenu a " +
            "inner join DevRole b on b.id = a.roleId " +
            "inner join DevMenu c on c.id = a.menuId " +
            "where c.menuUri is null")
    List<DevMenu> queryAllUriRoleV4();

    @Query("select m from DevRoleMenu rm " +
            "inner join DevMenu m on m.id = rm.menuId " +
            "where rm.roleId in :roleIdList")
    List<DevMenu> findAllMenuByRoleIds(@Param("roleIdList") List<Integer> roleIdList);
}
