package com.chat9.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

@Component
public class WSServer {

   private static class SingletionWSServer {
      static final WSServer instance = new WSServer();
   }

   public static WSServer getInstance(){
      return SingletionWSServer.instance;
   }

   private EventLoopGroup parentGroup;
   private EventLoopGroup childGroup ;
   private ServerBootstrap server;
   private ChannelFuture future;

   public WSServer(){
      parentGroup = new NioEventLoopGroup();
      childGroup = new NioEventLoopGroup();
      server = new ServerBootstrap();
      server.group(parentGroup, childGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new WSServerInitializer());
   }

   public void start(){
      this.future = server.bind(8085);
      System.err.println("netty websocket server started");
   }
}

