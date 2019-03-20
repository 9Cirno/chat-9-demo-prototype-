package com.chat9.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class WSServerInitializer extends ChannelInitializer<SocketChannel>{

   protected void initChannel(SocketChannel socketChannel) throws Exception {
      ChannelPipeline pipeline = socketChannel.pipeline();
      // websocket is based on http, so here needs decoder
      pipeline.addLast(new HttpServerCodec());
      // support large data stream
      pipeline.addLast(new ChunkedWriteHandler());
      // used for HttpMessage aggregates, to FullHttpRequest or FullHttpResponse
      pipeline.addLast(new HttpObjectAggregator(1024*128));

      // ============================ HTTP Support Above ==============================


      // if client does not send any request in 1 minutes, disconnect with client
      pipeline.addLast(new IdleStateHandler(58,59,60));
      // custom heartbeat handler
      pipeline.addLast(new HeartBeatHandler());




      // ========================= HeartBeat Support start ============================
      // websocket server protocol, and give the router '/ws'
      /**
       * WebSocketServerProtocolHandler will take all the task for lift websocket server
       * handshaking (close, ping, pong) ping + pong = heartbeat
       * to websocket, everything transfer with frame, different frame is used for different type of data
       */
      pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
      pipeline.addLast(new ChatHandler());
   }
}
