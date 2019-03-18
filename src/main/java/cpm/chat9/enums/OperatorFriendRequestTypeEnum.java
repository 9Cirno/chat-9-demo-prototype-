package cpm.chat9.enums;

/**
 * 
 * @Description: 忽略或者通过 好友请求的枚举
 */
public enum OperatorFriendRequestTypeEnum {
	
	IGNORE(0, "ignore"),
	PASS(1, "accept");
	
	public final Integer type;
	public final String msg;
	
	OperatorFriendRequestTypeEnum(Integer type, String msg){
		this.type = type;
		this.msg = msg;
	}
	
	public Integer getType() {
		return type;
	}  
	
	public static String getMsgByType(Integer type) {
		for (OperatorFriendRequestTypeEnum operType : OperatorFriendRequestTypeEnum.values()) {
			if (operType.getType() == type) {
				return operType.msg;
			}
		}
		return null;
	}
	
}
