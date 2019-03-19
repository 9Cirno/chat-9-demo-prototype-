package com.chat9.netty;

import java.io.Serializable;

public class DataContent implements Serializable {

    private static final long serialVersionUID = 6881078762252228639L;

    private Integer action;
    private ChatMsg chatMsg; //chat entity
    private String extend;   // extension

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ChatMsg getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(ChatMsg chatMsg) {
        this.chatMsg = chatMsg;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
