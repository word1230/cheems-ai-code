package com.cheems.cheemsaicode.genresult.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.cheems.cheemsaicode.genresult.entity.User;
import com.cheems.cheemsaicode.genresult.mapper.UserMapper;
import com.cheems.cheemsaicode.genresult.service.UserService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author cheems
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

}
