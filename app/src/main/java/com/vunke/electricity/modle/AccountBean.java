package com.vunke.electricity.modle;

import java.util.List;

/**
 * Created by zhuxi on 2019/9/21.
 */

public class AccountBean {

    /**
     * respCode : 2000
     * respMsg : success
     * bizBody : [{"id":8,"accountId":"1688074350024399","accountName":"阿坤","accountTypeId":"1","costId":"0","balance":5,"usableBalance":5,"freezeBalance":0,"userId":"1","currency":"CNY","status":"0","version":"0","updateTime":"2019-09-19 12:29:50","createTime":"2019-09-17 11:32:25"}]
     */

    private int respCode;
    private String respMsg;
    /**
     * id : 8
     * accountId : 1688074350024399
     * accountName : 阿坤
     * accountTypeId : 1
     * costId : 0
     * balance : 5
     * usableBalance : 5
     * freezeBalance : 0
     * userId : 1
     * currency : CNY
     * status : 0
     * version : 0
     * updateTime : 2019-09-19 12:29:50
     * createTime : 2019-09-17 11:32:25
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
        private int id;
        private String accountId;
        private String accountName;
        private String accountTypeId;
        private String costId;
        private int balance;
        private float usableBalance;
        private float freezeBalance;
        private String userId;
        private String currency;
        private String status;
        private String version;
        private String updateTime;
        private String createTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getAccountTypeId() {
            return accountTypeId;
        }

        public void setAccountTypeId(String accountTypeId) {
            this.accountTypeId = accountTypeId;
        }

        public String getCostId() {
            return costId;
        }

        public void setCostId(String costId) {
            this.costId = costId;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public float getUsableBalance() {
            return usableBalance;
        }

        public void setUsableBalance(float usableBalance) {
            this.usableBalance = usableBalance;
        }

        public float getFreezeBalance() {
            return freezeBalance;
        }

        public void setFreezeBalance(float freezeBalance) {
            this.freezeBalance = freezeBalance;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
