package com.evan.demo.security.system.pojo.converter;

import com.evan.demo.security.system.pojo.entity.DevUser;
import com.evan.demo.security.system.pojo.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserConverter {
    UserConverter CONVERTER = Mappers.getMapper(UserConverter.class);
    UserVO toTto(DevUser user);
}
