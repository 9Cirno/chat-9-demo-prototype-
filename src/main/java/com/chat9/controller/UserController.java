package com.chat9.controller;

import com.chat9.pojo.Users;
import com.chat9.pojo.vo.UsersVO;
import com.chat9.service.UserService;
import com.chat9.utils.JSONResult;
import com.chat9.utils.JsonUtils;
import com.chat9.utils.MD5Utils;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("u")
public class UserController {
   @Autowired
   private UserService userService;

   @GetMapping("/debuggerGet")
   public JSONResult debuggerGet(){
      int a = 0;
      return JSONResult.ok(null);
   }

   @PostMapping("/registerOrLogin")
   public JSONResult registOrLogin(@RequestBody Users user) throws Exception{
      // 0 both user name and password can not be empty

      if (StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())){
         return JSONResult.errorMsg("Username and Password can not be empty");
      }
      boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
      Users userResult = null;
      if(usernameIsExist){
         // login
         userResult = userService.queryUserForLogin(user.getUsername(),MD5Utils.getMD5Str(user.getPassword()));
         if (userResult == null){
            return JSONResult.errorMsg("Username or/and Password incorrect");
         }
      }else{
         // register
         user.setNickname(user.getUsername());
         user.setFaceImage("");
         user.setFaceImageBig("");
         user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
         userResult = userService.saveUser(user);
      }
      UsersVO userVO = new UsersVO();
      BeanUtils.copyProperties(userResult,userVO);
      return JSONResult.ok(userVO);
   }

}
