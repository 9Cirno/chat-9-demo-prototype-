package com.chat9.netty;

import io.netty.channel.Channel;

import java.util.HashMap;

// mapping user id and channel
public class UserChannelRel {
    private static HashMap<String, Channel> mapper = new HashMap<>();

    public static void put(String senderId, Channel channel){
        mapper.put(senderId,channel);
    }

    public static Channel get(String senderId){
        return mapper.get(senderId);
    }

    public static void output(){
        for(HashMap.Entry<String,Channel> entry : mapper.entrySet()){
            System.out.println("UserId: "+entry.getKey()
                    + ",ChannelId "+ entry.getValue().id().asLongText());
        }
    }

}
