package com.henry.drop;

import com.henry.drop.utils.LogUtil;
import ohos.aafwk.content.IntentParams;

import java.util.List;

public class CountDownBean {

    /**
     * 液体总量
     */
    private static  Double TotalLiquid = 0.00 ;

    /**
     * 20滴所需时间（s）
     */
    private static  Double TimeSpendFor20Dip = 0.00;

    /**
     * 总计需要多少ms可以滴完（calculate）
     */
    private static  Double TimeToSpendAll = 0.00;

    /**
     * 当前已经过去多久了ms
     */
    private static  Double TimeHaveUsed = 0.00;

    public CountDownBean(double tTotalLiquid ,double tTimeSpendFor20Dip,double tTimeToSpendAll,double tTimeHaveUsed) {
        super();
        this.TotalLiquid = tTotalLiquid;
        this.TimeSpendFor20Dip = tTimeSpendFor20Dip;
        this.TimeToSpendAll = tTimeToSpendAll;
        this.TimeHaveUsed = tTimeHaveUsed;
    }

    public CountDownBean() {
    }

    public CountDownBean(IntentParams params) {
        if (params == null) {
            LogUtil.info(this.getClass(), "Invalid intent params, can't create CountDownBean");
            return;
        }

        this.TotalLiquid = getDoubleParam(params, "TotalLiquid");
        this.TimeSpendFor20Dip = getDoubleParam(params, "TimeSpendFor20Dip");
        this.TimeToSpendAll = getDoubleParam(params, "TimeToSpendAll");
        this.TimeHaveUsed = getDoubleParam(params, "TimeHaveUsed");
    }

    private double getDoubleParam(IntentParams intentParams, String key) {
        Object value = intentParams.getParam(key);
        if ((value != null) && (value instanceof Double)) {
            return (double) value;
        }
        return -1;
    }
    public void saveDataToParams(IntentParams params) {
        params.setParam("TotalLiquid", this.TotalLiquid == null ? "" : this.TotalLiquid);
        params.setParam("TimeSpendFor20Dip", this.TimeSpendFor20Dip == null ? "" : this.TimeSpendFor20Dip);
        params.setParam("TimeToSpendAll", this.TimeToSpendAll == null ? "" : this.TimeToSpendAll);
        params.setParam("TimeHaveUsed", this.TimeHaveUsed == null ? "" : this.TimeHaveUsed);
    }


    public  double getTotalLiquid() {
        return TotalLiquid;
    }

    public  double getTimeSpendFor20Dip() {
        return TimeSpendFor20Dip;
    }

    public  double getTimeToSpendAll() {
        return TimeToSpendAll;
    }

    public  double getTimeHaveUsed() {
        return TimeHaveUsed;
    }

    public  void setTotalLiquid(Double totalLiquid) {
        TotalLiquid = totalLiquid;
    }

    public  void setTimeSpendFor20Dip(Double timeSpendFor20Dip) {
        TimeSpendFor20Dip = timeSpendFor20Dip;
    }

    public  void setTimeToSpendAll(Double timeToSpendAll) {
        TimeToSpendAll = timeToSpendAll;
    }

    public  void setTimeHaveUsed(Double timeHaveUsed) {
        TimeHaveUsed = timeHaveUsed;
    }
}
