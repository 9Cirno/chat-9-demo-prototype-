package com.chat9.service;

import com.chat9.netty.ChatMsg;
import com.chat9.pojo.Users;
import com.chat9.pojo.vo.FriendRequestVO;
import com.chat9.pojo.vo.MyFriendsVO;

import java.util.List;

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

    /*
        @Description: updateQRcode
    */
    public Users updateQRcode(Users user);
    /*
        @Description: update user info
     */
    public Users updateUserInfo(Users user);

    /*
        @Description: return enums according search user result
     */
    public Integer preconditionSearchFriends(String myUserId, String friendUsername);

    /*
        @Description: find user by username
     */
    public Users queryUserInfoByUsername(String username);

    /*
        @Description: add friend request
     */
    public void sendFriendRequest(String myUserId, String friendUsername);


    /*
        @Description: query friend request
    */
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    /*
        @Description: delete friend request
    */
    public void deleteFriendRequest(String sendUserId, String acceptUserId);


    /*
        @Description: pass friend request
                    1:delete friend request
                    2:save friend in both way
    */
    public void passFriendRequest(String sendUserId, String acceptUserId);

    /*
    @Description: query all friends
    */
    public List<MyFriendsVO> queryMyFriends(String userId);


    /*
    @Description: store chat message to database, used com.netty.ChatMsg
    */
    public String saveMsg(ChatMsg chatMsg);

    /*
    @Description: batch sign message
    */
    public void updateMsgSigned(List<String> msgIdList);

    /*
    @Description: request unsigned messages
    */
    public List<com.chat9.pojo.ChatMsg> getUnReadMsgList(String acceptUserId);


}
