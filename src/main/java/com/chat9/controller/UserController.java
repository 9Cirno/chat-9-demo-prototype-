package com.chat9.controller;

import com.chat9.pojo.Users;
import com.chat9.pojo.bo.UsersBO;
import com.chat9.pojo.vo.MyFriendsVO;
import com.chat9.pojo.vo.UsersVO;
import com.chat9.service.UserService;
import com.chat9.utils.*;
import cpm.chat9.enums.OperatorFriendRequestTypeEnum;
import cpm.chat9.enums.SearchFriendStatusEnum;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("u")
public class UserController {
   @Autowired
   private UserService userService;

   @Autowired
   private FastDFSClient fastDFSClient;

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
         //        user.setFaceImage("M00/00/00/no-avatar.png");
         //        user.setFaceImageBig("M00/00/00/no-avatar.png");
         user.setNickname(user.getUsername());
         user.setFaceImage("M00/00/00/no-avatar.png");
         user.setFaceImageBig("M00/00/00/no-avatar.png");
         user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
         userResult = userService.saveUser(user);
      }
      UsersVO userVO = new UsersVO();
      BeanUtils.copyProperties(userResult,userVO);
      return JSONResult.ok(userVO);
   }



   @PostMapping("/uploadFaceBase64")
   public JSONResult uploadFaceBase64(@RequestBody UsersBO userBO) throws Exception{

      //get base64 data from front-end
      String base64Data = userBO.getFaceData();
      String userFacePath = Paths.get("").toAbsolutePath().toString()+userBO.getUserId()+"userface64.png";
      System.out.println(userFacePath);
      FileUtils.base64ToFile(userFacePath, base64Data);
      MultipartFile facefile = FileUtils.fileToMultipart(userFacePath);
      String url = fastDFSClient.uploadBase64(facefile);
      System.out.println(url);

      String thump = "_80x80.";
      String arr[] = url.split("\\.");
      String thumpImgUrl = arr[0] + thump + arr[1];

      Users user = new Users();
      user.setId(userBO.getUserId());
      user.setFaceImage(thumpImgUrl);
      user.setFaceImageBig(url);

      Users result = userService.updateUserInfo(user);
      return JSONResult.ok(result);

   }

   @PostMapping("/setNickname")
   public JSONResult setNickname(@RequestBody UsersBO userBO) throws Exception{

      Users user = new Users();
      user.setId(userBO.getUserId());
      user.setNickname(userBO.getNickname());

      Users result = userService.updateUserInfo(user);
      return JSONResult.ok(result);

   }

   @PostMapping("/updateQRcode")
   public JSONResult updateQRcode(@RequestBody UsersBO userBO) throws Exception{

      Users user = new Users();
      user.setId(userBO.getUserId());
      user.setUsername(userBO.getUsername());
      Users result = userService.updateQRcode(user);
      return JSONResult.ok(result);

   }

   @PostMapping("/search")
   public JSONResult searchUser(String placeHolder, String myUserId, String friendUsername) throws Exception{

      // both parameter can not be empty
      if (StringUtils.isBlank(myUserId)||StringUtils.isBlank(friendUsername)){
         return JSONResult.errorMsg("ERROR");
      }

      //1:friend not exist
      //2:friend is myself
      //3:already friend

      Integer status =userService.preconditionSearchFriends(myUserId,friendUsername);
      if (status == SearchFriendStatusEnum.SUCCESS.status){
         Users user = userService.queryUserInfoByUsername(friendUsername);
         UsersVO userVO = new UsersVO();
         BeanUtils.copyProperties(user, userVO);
         return JSONResult.ok(userVO);
      }
      else{
         String errorMsg = SearchFriendStatusEnum.getMsgByKey(status);
         return JSONResult.errorMsg(errorMsg);
      }

   }
   //String placeHolder,
   @PostMapping("/addFriendRequest")
   public JSONResult addFriend(String myUserId, String friendUsername) throws Exception{

      // both parameter can not be empty
      if (StringUtils.isBlank(myUserId)||StringUtils.isBlank(friendUsername)){
         return JSONResult.errorMsg("ERROR");
      }

      //1:friend not exist
      //2:friend is myself
      //3:already friend

      Integer status =userService.preconditionSearchFriends(myUserId,friendUsername);
      if (status == SearchFriendStatusEnum.SUCCESS.status){
        userService.sendFriendRequest(myUserId, friendUsername);
      }
      else{
         String errorMsg = SearchFriendStatusEnum.getMsgByKey(status);
         return JSONResult.errorMsg(errorMsg);
      }

      return JSONResult.ok();

   }

   @PostMapping("/queryFriendRequests")
   public JSONResult queryFriendRequests(String userId){

      // if empty
      if (StringUtils.isBlank(userId)){
         return JSONResult.errorMsg("ERROR");
      }

      return JSONResult.ok(userService.queryFriendRequestList(userId));
   }

   @PostMapping("/operFriendRequest")
   public JSONResult operFriendRequest(String acceptUserId,String sendUserId, Integer operType){

      // if empty
      if (StringUtils.isBlank(acceptUserId)||StringUtils.isBlank(sendUserId)||operType==null){
         return JSONResult.errorMsg("ERROR");
      }

      if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))){
         return JSONResult.errorMsg("ERROR");
      }

      if (operType == OperatorFriendRequestTypeEnum.IGNORE.type){
         //delete request from database only
         userService.deleteFriendRequest(sendUserId,acceptUserId);
      }else if (operType == OperatorFriendRequestTypeEnum.PASS.type){
         //delete request and create friends for both use
         userService.passFriendRequest(sendUserId, acceptUserId);

      }

      List<MyFriendsVO> myFriends = userService.queryMyFriends(acceptUserId);

      return JSONResult.ok(myFriends);

   }




   @PostMapping("/myFriends")
   public JSONResult myFriends(String userId){

      // if empty
      if (StringUtils.isBlank(userId)){
         return JSONResult.errorMsg("ERROR");
      }


      List<MyFriendsVO> myFriends = userService.queryMyFriends(userId);

      return JSONResult.ok(myFriends);

   }



}
