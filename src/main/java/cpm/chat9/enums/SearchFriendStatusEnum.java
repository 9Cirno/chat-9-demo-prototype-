package cpm.chat9.enums;

public enum SearchFriendStatusEnum {

    SUCCESS(0, "OK"),
    User_NOT_EXIST(1,"User not found"),
    NOT_YOURSELF(2,"Can not add yourself"),
    ALREADY_FRIENDS(3,"This user is already your friend");

    public final Integer status;
    public final String msg;

    SearchFriendStatusEnum(Integer status, String msg){
        this.status = status;
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public static String getMsgByKey(Integer status) {
        for (SearchFriendStatusEnum type : SearchFriendStatusEnum.values()) {
            if (type.getStatus() == status) {
                return type.msg;
            }
        }
        return null;
    }


}
