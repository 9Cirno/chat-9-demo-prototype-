package com.chat9.service;

import com.chat9.pojo.Users;

public interface UserService {
    /*
    * @Description: to check if user exist
    */
    public boolean queryUsernameIsExist(String username);

    /*
      @Description: to check if password correct
     */
    public Users queryUserForLogin(String username, String pwd);

    /*
        @Description: register
    */
    public Users saveUser(Users user);
}
