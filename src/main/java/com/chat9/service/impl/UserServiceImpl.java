package com.chat9.service.impl;

import com.chat9.mapper.FriendsRequestMapper;
import com.chat9.mapper.MyFriendsMapper;
import com.chat9.mapper.UsersMapper;
import com.chat9.mapper.UsersMapperCustom;
import com.chat9.pojo.FriendsRequest;
import com.chat9.pojo.MyFriends;
import com.chat9.pojo.Users;
import com.chat9.pojo.vo.FriendRequestVO;
import com.chat9.pojo.vo.MyFriendsVO;
import com.chat9.service.UserService;
import com.chat9.utils.FastDFSClient;
import com.chat9.utils.FileUtils;
import com.chat9.utils.QRCodeUtils;
import cpm.chat9.enums.SearchFriendStatusEnum;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.validation.constraints.AssertTrue;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersMapperCustom usersMapperCustom;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

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

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users saveUser(Users user){

        //generate unique QR code for each user

        String qrCodepath = Paths.get("").toAbsolutePath().toString()+user.getId()+"qrcode.png";
        //chat9_qrcode:[username]
        qrCodeUtils.createQRCode(qrCodepath, "chat9_qrcode:"+user.getUsername());
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodepath);
        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadBase64(qrCodeFile);
        }catch(IOException e){
            e.printStackTrace();
        }
        String id = sid.nextShort();
        user.setId(id);
        user.setQrcode(qrCodeUrl);
        usersMapper.insert(user);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(Users user){
        usersMapper.updateByPrimaryKeySelective(user);
        return queryUserById(user.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateQRcode(Users user){

        String qrCodepath = Paths.get("").toAbsolutePath().toString()+user.getId()+"qrcode.png";
        //chat9_qrcode:[username]
        System.out.println(qrCodepath);
        qrCodeUtils.createQRCode(qrCodepath, "chat9_qrcode:"+user.getUsername());
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodepath);
        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadBase64(qrCodeFile);
        }catch(IOException e){
            e.printStackTrace();
        }

        user.setQrcode(qrCodeUrl);
        usersMapper.updateByPrimaryKeySelective(user);
        return queryUserById(user.getId());
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    private Users queryUserById(String userID){
        return usersMapper.selectByPrimaryKey(userID);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer preconditionSearchFriends(String myUserId, String friendUsername){
        Users user = queryUserInfoByUsername(friendUsername);
        // if user not exist
        if (user == null){
            return SearchFriendStatusEnum.User_NOT_EXIST.status;
        }
        // if user is same
        if (user.getId().equals(myUserId)){
            return SearchFriendStatusEnum.NOT_YOURSELF.status;
        }
        // if user is already added
        Example mfe = new Example(MyFriends.class);
        Criteria mfc = mfe.createCriteria();
        mfc.andEqualTo("myUserId", myUserId);
        mfc.andEqualTo("myFriendUserId", user.getId());


        MyFriends myFriendsRel = myFriendsMapper.selectOneByExample(mfe);
        if (myFriendsRel != null){
            return SearchFriendStatusEnum.ALREADY_FRIENDS.status;
        }

        return SearchFriendStatusEnum.SUCCESS.status;

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfoByUsername(String username){
        Example ue = new Example(Users.class);
        Criteria uc = ue.createCriteria();
        uc.andEqualTo("username", username);
        return usersMapper.selectOneByExample(ue);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void sendFriendRequest(String myUserId, String friendUsername){
        Users friend = queryUserInfoByUsername(friendUsername);

        Example fre = new Example(FriendsRequest.class);
        Criteria frc = fre.createCriteria();
        frc.andEqualTo("sendUserId", myUserId);
        frc.andEqualTo("acceptUserId", friend.getId());
        FriendsRequest friendsRequest = friendsRequestMapper.selectOneByExample(fre);

        if (friendsRequest == null){
            // if not your friend and not friend request
            String requestId = sid.nextShort();

            FriendsRequest request = new FriendsRequest();
            request.setId(requestId);
            request.setSendUserId(myUserId);
            request.setAcceptUserId(friend.getId());
            request.setRequestDateTime(new Date());
            friendsRequestMapper.insert(request);

        }
    };


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId){
        return usersMapperCustom.queryFriendRequestList(acceptUserId);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteFriendRequest(String sendUserId, String acceptUserId){
        Example fre = new Example(FriendsRequest.class);
        Criteria frc = fre.createCriteria();
        frc.andEqualTo("sendUserId", sendUserId);
        frc.andEqualTo("acceptUserId", acceptUserId);
        friendsRequestMapper.deleteByExample(fre);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void passFriendRequest(String sendUserId, String acceptUserId){

        saveFriend(sendUserId,acceptUserId);
        saveFriend(acceptUserId,sendUserId);
        deleteFriendRequest(sendUserId,acceptUserId);

    };


    @Transactional(propagation = Propagation.REQUIRED)
    private void saveFriend(String sendUserId, String acceptUserId){
        MyFriends myFriends = new MyFriends();
        String recordId = sid.nextShort();
        myFriends.setId(recordId);
        myFriends.setMyFriendUserId(acceptUserId);
        myFriends.setMyUserId(sendUserId);
        myFriendsMapper.insert(myFriends);
    };


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MyFriendsVO> queryMyFriends(String userId){

        List<MyFriendsVO> myFriends = usersMapperCustom.queryMyFriends(userId);

        return myFriends;
    }

}
