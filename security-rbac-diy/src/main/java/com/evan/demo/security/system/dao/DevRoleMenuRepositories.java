package com.evan.demo.security.system.dao;

import com.evan.demo.security.system.pojo.vo.UrlRoleVO;
import com.evan.demo.security.system.pojo.entity.DevMenu;
import com.evan.demo.security.system.pojo.entity.DevRoleMenu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevRoleMenuRepositories extends CrudRepository<DevRoleMenu, Integer> {

    /**
     * 查询所有url所需权限 -- 授权到按钮级别的SQL使用该SQL
     */
    @Query("select new com.evan.demo.security.system.pojo.vo.UrlRoleVO(b.id, b.roleCode, c.menuUri) " +
            "from DevRoleMenu a " +
            "inner join DevRole b on b.id = a.roleId " +
            "inner join DevMenu c on c.id = a.menuId " +
            "where c.menuUri is not null")
    List<UrlRoleVO> queryAllUriRole();

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
    List<DevMenu> findAllMenuByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);
}
