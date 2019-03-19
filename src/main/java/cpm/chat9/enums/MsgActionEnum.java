package cpm.chat9.enums;

/**
 * 
 * @Description: 发送消息的动作 枚举
 */
public enum MsgActionEnum {
	
	CONNECT(1, "connection(reconnection)initialization"),
	CHAT(2, "chat message"),
	SIGNED(3, "read mark"),
	KEEPALIVE(4, "heartbeat"),
	PULL_FRIEND(5, "get friends");
	
	public final Integer type;
	public final String content;
	
	MsgActionEnum(Integer type, String content){
		this.type = type;
		this.content = content;
	}
	
	public Integer getType() {
		return type;
	}  
}
