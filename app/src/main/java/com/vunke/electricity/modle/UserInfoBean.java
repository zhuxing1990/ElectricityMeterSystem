package com.vunke.electricity.modle;

/**
 * Created by zhuxi on 2019/9/21.
 */

public class UserInfoBean {

    /**
     * respCode : 2000
     * respMsg : success
     * bizBody : {"userId":1,"userName":"吕凌坤","userMobile":"17673762992","userPass":"631318","roomId":1,"idCard":"430522199605048552","trueName":"吕凌坤","createTime":"2019-09-06 09:58:26","wxOpenId":"oGqrl1NkhdmCycIZR7Ha4D4snTtM"}
     */

    private int respCode;
    private String respMsg;
    /**
     * userId : 1
     * userName : 吕凌坤
     * userMobile : 17673762992
     * userPass : 631318
     * roomId : 1
     * idCard : 430522199605048552
     * trueName : 吕凌坤
     * createTime : 2019-09-06 09:58:26
     * wxOpenId : oGqrl1NkhdmCycIZR7Ha4D4snTtM
     */

    private BizBodyBean bizBody;

    public int getRespCode() {
        return respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public BizBodyBean getBizBody() {
        return bizBody;
    }

    public void setBizBody(BizBodyBean bizBody) {
        this.bizBody = bizBody;
    }

    public static class BizBodyBean {
        private int userId;
        private String userName;
        private String userMobile;
        private String userPass;
        private int roomId;
        private String idCard;
        private String trueName;
        private String createTime;
        private String wxOpenId;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserMobile() {
            return userMobile;
        }

        public void setUserMobile(String userMobile) {
            this.userMobile = userMobile;
        }

        public String getUserPass() {
            return userPass;
        }

        public void setUserPass(String userPass) {
            this.userPass = userPass;
        }

        public int getRoomId() {
            return roomId;
        }

        public void setRoomId(int roomId) {
            this.roomId = roomId;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getTrueName() {
            return trueName;
        }

        public void setTrueName(String trueName) {
            this.trueName = trueName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getWxOpenId() {
            return wxOpenId;
        }

        public void setWxOpenId(String wxOpenId) {
            this.wxOpenId = wxOpenId;
        }
    }
}
