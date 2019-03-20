package com.chat9.netty;
import com.chat9.SpringUtil;
import com.chat9.service.UserService;
import com.chat9.utils.JsonUtils;
import cpm.chat9.enums.MsgActionEnum;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: this handler deals with message
 * TextWWebSocketFrame: in netty, used for websocket process, frame is the message loader
 */
// track channel heartbeat
// extends from ChannelInboundHandlerAdapter to skip implement channelread0
public class HeartBeatHandler extends ChannelInboundHandlerAdapter{



   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{

      // if not receive any request (e.g. read/write) in a certain interval,
      // ths IdleStateEvent triggered
      // triggered condition: read Idle/ write Idle / All Idle (both I/O)
      if (evt instanceof IdleStateEvent){
         IdleStateEvent event = (IdleStateEvent) evt;
         if (event.state() == IdleState.READER_IDLE){
            //read Idle
            //System.out.println("read Idle");
         }else if (event.state() == IdleState.WRITER_IDLE){
            //write idle
            //System.out.println("write Idle");
         }else if (event.state() == IdleState.ALL_IDLE){
            //System.out.println("before "+ChatHandler.users.size());
            Channel channel = ctx.channel();
            channel.close();
            //System.out.println("after "+ChatHandler.users.size());
         }
      }
   }

}


