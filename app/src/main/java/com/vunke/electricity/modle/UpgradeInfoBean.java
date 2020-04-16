package com.vunke.electricity.modle;

import java.util.List;

/**
 * Created by zhuxi on 2019/9/23.
 */

public class UpgradeInfoBean {

    /**
     * respCode : 2000
     * respMsg : success
     * sign : null
     * bizBody : [{"upgradeId":1,"versionCode":1,"versionName":"第一个版本","createTime":"2019-09-16 15:00:09","url":"http://www.baidu.com","status":"0"}]
     */

    private int respCode;
    private String respMsg;
    private Object sign;
    /**
     * upgradeId : 1
     * versionCode : 1
     * versionName : 第一个版本
     * createTime : 2019-09-16 15:00:09
     * url : http://www.baidu.com
     * status : 0
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

    public Object getSign() {
        return sign;
    }

    public void setSign(Object sign) {
        this.sign = sign;
    }

    public List<BizBodyBean> getBizBody() {
        return bizBody;
    }

    public void setBizBody(List<BizBodyBean> bizBody) {
        this.bizBody = bizBody;
    }

    public static class BizBodyBean {
        private int upgradeId;
        private int versionCode;
        private String versionName;
        private String createTime;
        private String url;
        private String status;

        public int getUpgradeId() {
            return upgradeId;
        }

        public void setUpgradeId(int upgradeId) {
            this.upgradeId = upgradeId;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
