package com.vunke.electricity.modle;

import java.util.List;

/**
 * Created by zhuxi on 2019/9/21.
 */

public class CostInfoBean {

    /**
     * respCode : 2000
     * respMsg : success
     * bizBody : [{"costId":"0","costName":"物业费","unit":"季","price":3000,"modifyTime":"2019-09-19 11:01:48"},{"costId":"3","costName":"空调费","unit":"度","price":100,"modifyTime":"2019-09-19 11:01:48"},{"costId":"1","costName":"电费","unit":"度","price":100,"modifyTime":"2019-09-19 11:01:48"},{"costId":"2","costName":"水费","unit":"立方米","price":288,"modifyTime":"2019-09-19 11:01:48"}]
     */

    private int respCode;
    private String respMsg;
    /**
     * costId : 0
     * costName : 物业费
     * unit : 季
     * price : 3000
     * modifyTime : 2019-09-19 11:01:48
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
        private String costId;
        private String costName;
        private String unit;
        private Long price;
        private String modifyTime;

        public String getCostId() {
            return costId;
        }

        public void setCostId(String costId) {
            this.costId = costId;
        }

        public String getCostName() {
            return costName;
        }

        public void setCostName(String costName) {
            this.costName = costName;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(Long price) {
            this.price = price;
        }

        public String getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(String modifyTime) {
            this.modifyTime = modifyTime;
        }

        @Override
        public String toString() {
            return "BizBodyBean{" +
                    "costId='" + costId + '\'' +
                    ", costName='" + costName + '\'' +
                    ", unit='" + unit + '\'' +
                    ", price=" + price +
                    ", modifyTime='" + modifyTime + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CostInfoBean{" +
                "respCode=" + respCode +
                ", respMsg='" + respMsg + '\'' +
                ", bizBody=" + bizBody +
                '}';
    }
}
