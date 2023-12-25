package com.evan.demo.security.system.dao;

import com.evan.demo.security.system.pojo.entity.DevRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevRoleRepositories extends CrudRepository<DevRole, Integer> {
}
