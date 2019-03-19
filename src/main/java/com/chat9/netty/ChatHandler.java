package com.chat9.netty;
import com.chat9.SpringUtil;
import com.chat9.service.UserService;
import com.chat9.utils.JsonUtils;
import cpm.chat9.enums.MsgActionEnum;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: this handler deals with message
 * TextWWebSocketFrame: in netty, used for websocket process, frame is the message loader
 */

public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
   // used for remember and control all clients channels
   private static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

   protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame)
         throws Exception {
//      String content = textWebSocketFrame.text();
//      System.out.println("received: "+content);
//
//      for (Channel channel: users){
//         channel.writeAndFlush(new TextWebSocketFrame("[message received ]"+content));
//      }
      /*
      clients.writeAndFlush(new TextWebSocketFrame("[message received ]"+content));
       */

      //1. get message from clients
      String content = textWebSocketFrame.text();
      Channel currentChannel = channelHandlerContext.channel();

      DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
      Integer action = dataContent.getAction();

      //2. distinguish message type and process according to the type



      if (action == MsgActionEnum.CONNECT.type){
         //2.1 when the socket open at the first time, initialize channel
         //mapping userid and channelid
         String sendId = dataContent.getChatMsg().getSenderId();
         UserChannelRel.put(sendId, currentChannel);

         for (Channel c:users){
            System.out.println(c.id().asLongText());
         }
         UserChannelRel.output();

      }else if(action == MsgActionEnum.CHAT.type){
         //2.2 store the message to database,mark it as unread
         ChatMsg chatMsg = dataContent.getChatMsg();
         String msgText = chatMsg.getMsg();
         String receiverId = chatMsg.getReceiverId();
         String senderId = chatMsg.getSenderId();

         //Store to database and mark as unread

         //get spring managed java been
         UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");

         String msgId = userService.saveMsg(chatMsg);
         chatMsg.setMsgId(msgId);

         //get receiver channel id
         Channel receiverChannel = UserChannelRel.get(receiverId);
         if (receiverChannel == null){
            // todo
            // null channel implies receiver not connected
            // use third party notification to send message is a solution
         }else{
            // when channel is not null, looking for if channel exists in channel group
            Channel findChannel = users.find(receiverChannel.id());
            if (findChannel != null){
               receiverChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(chatMsg)));
            }else{
               // todo
               // null channel implies receiver not connected
               // use third party notification to send message is a solution
            }
         }


      }else if(action == MsgActionEnum.SIGNED.type){
         //2.3 change the message to read for the already read messages
         UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
         //extension in this case are ids for signed message
         String msgIdStr = dataContent.getExtend();
         String msgIds[] = msgIdStr.split(",");

         List<String> msgIdList = new ArrayList<>();
         for(String mid : msgIds){
            if(StringUtils.isNotBlank(mid)){
               msgIdList.add(mid);
            }
         }

         System.out.println(msgIdList.toString());
         if(msgIdList!=null && !msgIdList.isEmpty() && msgIdList.size()>0){
            // batch sign
            userService.updateMsgSigned(msgIdList);
         }


      }else if(action == MsgActionEnum.KEEPALIVE.type){
         //2.4 heartbeat message

      }







   }

   /**
    * when client connection with server, get channel of client and add to Channel group
    * @param ctx
    * @throws Exception
    */
   @Override public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      users.add(ctx.channel());

   }

   @Override public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      // when handler removed triggered, its channel will be removed automatically
      //clients.remove(ctx.channel());
      //System.out.println("server disconnect, long id: "+ ctx.channel().id().asLongText());
      //System.out.println("server disconnect, short id: "+ ctx.channel().id().asShortText());
      users.remove(ctx.channel());

   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      cause.printStackTrace();
      //close channel and remove from channel group
      ctx.channel().close();
      users.remove(ctx.channel());
   }
}
