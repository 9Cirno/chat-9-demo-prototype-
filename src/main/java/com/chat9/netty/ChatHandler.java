package com.chat9.netty;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @Description: this handler deals with message
 * TextWWebSocketFrame: in netty, used for websocket process, frame is the message loader
 */

public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
   // used for remember and control all clients channels
   private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

   protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame)
         throws Exception {
      String content = textWebSocketFrame.text();
      System.out.println("received: "+content);

      for (Channel channel: clients){
         channel.writeAndFlush(new TextWebSocketFrame("[message received ]"+content));
      }
      /*
      clients.writeAndFlush(new TextWebSocketFrame("[message received ]"+content));
       */
   }

   /**
    * when client connection with server, get channel of client and add to Channel group
    * @param ctx
    * @throws Exception
    */
   @Override public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      clients.add(ctx.channel());

   }

   @Override public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      // when handler removed triggered, its channel will be removed automatically
      //clients.remove(ctx.channel());
      System.out.println("server disconnect, long id: "+ ctx.channel().id().asLongText());
      System.out.println("server disconnect, short id: "+ ctx.channel().id().asShortText());
   }
}
