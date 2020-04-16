package com.vunke.electricity.modle;

import java.util.List;

/**
 * Created by zhuxi on 2019/12/24.
 */

public class BalanceBean {

    /**
     * respCode : 2000
     * respMsg : success
     * bizBody : [{"amount":"-801.36","userMobile":"18570049982","name":"电费","costId":"1"}]
     */

    private int respCode;
    private String respMsg;
    /**
     * amount : -801.36
     * userMobile : 18570049982
     * name : 电费
     * costId : 1
     */

    private List<BizBodyBean> bizBody;

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

    public List<BizBodyBean> getBizBody() {
        return bizBody;
    }

    public void setBizBody(List<BizBodyBean> bizBody) {
        this.bizBody = bizBody;
    }

    public static class BizBodyBean {
        private float amount;
        private String userMobile;
        private String name;
        private String costId;
        private String userName;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }

        public String getUserMobile() {
            return userMobile;
        }

        public void setUserMobile(String userMobile) {
            this.userMobile = userMobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCostId() {
            return costId;
        }

        public void setCostId(String costId) {
            this.costId = costId;
        }

        @Override
        public String toString() {
            return "BizBodyBean{" +
                    "amount=" + amount +
                    ", userMobile='" + userMobile + '\'' +
                    ", name='" + name + '\'' +
                    ", costId='" + costId + '\'' +
                    ", userName='" + userName + '\'' +
                    '}';
        }
    }
}
