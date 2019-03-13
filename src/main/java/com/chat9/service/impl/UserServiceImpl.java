package com.chat9.service.impl;

import com.chat9.mapper.UsersMapper;
import com.chat9.pojo.Users;
import com.chat9.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username){

        Users user = new Users();
        user.setUsername(username);
        Users result = usersMapper.selectOne(user);
        return result != null ? true : false;
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String pwd){

        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",pwd);

        Users result = usersMapper.selectOneByExample(userExample);

        return result;
    }

    @Override
    public Users saveUser(Users user){
        String userID = sid.nextShort();
        //generate unique QR code for each user
        user.setQrcode("");
        user.setId((userID));
        usersMapper.insert(user);

        return user;
    }

}
