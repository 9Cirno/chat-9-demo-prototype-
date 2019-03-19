package com.chat9.mapper;

import com.chat9.pojo.Users;
import com.chat9.pojo.vo.FriendRequestVO;
import com.chat9.pojo.vo.MyFriendsVO;
import com.chat9.utils.MyMapper;

import java.util.List;

public interface UsersMapperCustom extends MyMapper<Users> {

    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    public List<MyFriendsVO> queryMyFriends(String userId);

    public void batchUpdateMsgSigned(List<String> msgIdList);

}
