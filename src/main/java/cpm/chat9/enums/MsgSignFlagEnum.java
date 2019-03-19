package cpm.chat9.enums;

/**
 * 
 * @Description: enums for unsign/sign
 */
public enum MsgSignFlagEnum {
	
	unsign(0, "unsign"),
	signed(1, "sign");
	
	public final Integer type;
	public final String content;
	
	MsgSignFlagEnum(Integer type, String content){
		this.type = type;
		this.content = content;
	}
	
	public Integer getType() {
		return type;
	}  
}
