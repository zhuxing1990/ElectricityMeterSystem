package com.vunke.electricity.modle;

import java.util.List;

/**
 * Created by zhuxi on 2019/9/18.
 */

public class ConfigInfoBean {


    /**
     * respCode : 2000
     * respMsg : success
     * bizBody : [{"configKey":"loopTime","configValue":"1","configName":"超标间隔时间"},{"configKey":"smsNotityRate","configValue":"1","configName":"短信推送阀值"}]
     */

    private int respCode;
    private String respMsg;
    /**
     * configKey : loopTime
     * configValue : 1
     * configName : 超标间隔时间
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
        private String configKey;
        private String configValue;
        private String configName;

        public String getConfigKey() {
            return configKey;
        }

        public void setConfigKey(String configKey) {
            this.configKey = configKey;
        }

        public String getConfigValue() {
            return configValue;
        }

        public void setConfigValue(String configValue) {
            this.configValue = configValue;
        }

        public String getConfigName() {
            return configName;
        }

        public void setConfigName(String configName) {
            this.configName = configName;
        }
    }
}
