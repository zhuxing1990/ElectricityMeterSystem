package com.vunke.electricity.server.config;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.vunke.electricity.dao.MeterDao;
import com.vunke.electricity.db.Meter;
import com.vunke.electricity.server.WebPageMap;
import com.vunke.electricity.server.WebRequest;
import com.vunke.electricity.server.workpage.CloseDevicePage;
import com.vunke.electricity.server.workpage.KillMySelfPage;
import com.vunke.electricity.server.workpage.OpenDevicePage;
import com.vunke.electricity.server.workpage.QueryMeterPage;
import com.vunke.electricity.server.workpage.QueryStatusPage;
import com.vunke.electricity.server.workpage.RebootDevicePage;
import com.vunke.electricity.server.workpage.RenewalBalancePage;
import com.vunke.electricity.server.workpage.UpgradeAppPage;
import com.vunke.electricity.util.LogUtil;
import com.vunke.electricity.util.MACUtil;

import java.io.PrintWriter;
import java.util.List;


/**
 * Created by Administrator on 2018-03-23.
 */
public class WebConfig {
    private static final String TAG = "WebConfig";
    public static final int SERVER_PORT = 9980;
    public static final String ACCOUNT_KEY = "meterNo";
    public static final String MAC = "mac";
    private static volatile String sessionAccount = "";
    private static volatile String sessionMac= "";
    public static final String ENCRYPT_KEY = "FLIKPQ0K7TLR05IONAUNM60KGXUHTPUH";


    public static final String RebootDevicePage ="/rebootDevicePage.do";
    public static final String RENEWAL_BALANCE_PAGE = "/renewalBalancePage.do";
    public static final String OPEN_DEVICE_PAGE = "/openDevicePage.do";
    public static final String CLOSE_DEVICE_PAGE = "/closeDevicePage.do";
    public static final String UPGRADE_APP_PAGE = "/upgradeAppPage.do";
    public static final String QUERY_METER_PAGE = "/queryMeterPage.do";
    public static final String KILL_MY_SELF_PAGE = "/killMySelfPage.do";
    public static final String QUERY_STATUS_PAGE = "/queryStatusPage.do";
    private static Context context;
    public static void init(Context mcontext) {
            context = mcontext;

        WebPageMap.getInstance().registerWebPage(RebootDevicePage, RebootDevicePage.class);
        WebPageMap.getInstance().registerWebPage(OPEN_DEVICE_PAGE, OpenDevicePage.class);
        WebPageMap.getInstance().registerWebPage(RENEWAL_BALANCE_PAGE, RenewalBalancePage.class);
        WebPageMap.getInstance().registerWebPage(CLOSE_DEVICE_PAGE, CloseDevicePage.class);
        WebPageMap.getInstance().registerWebPage(UPGRADE_APP_PAGE, UpgradeAppPage.class);
        WebPageMap.getInstance().registerWebPage(QUERY_METER_PAGE, QueryMeterPage.class);
        WebPageMap.getInstance().registerWebPage(KILL_MY_SELF_PAGE, KillMySelfPage.class);
        WebPageMap.getInstance().registerWebPage(QUERY_STATUS_PAGE, QueryStatusPage.class);

        try {
            LogUtil.d(TAG, MACUtil.getSERIAL()+" \n init: " + EncryptToolNew.DESEncrypt(MACUtil.getSERIAL(), ENCRYPT_KEY));
        } catch (Exception ignore) {
        }
    }

    public static void checkAccount(WebRequest request, PrintWriter out) throws PermissionException {
        try {
            sessionAccount = request.queryParameter(ACCOUNT_KEY);
            sessionMac = request.queryParameter(MAC);
            LogUtil.i(TAG, "checkAccount: " + sessionAccount);
            LogUtil.i(TAG, "checkMac: " + sessionMac);
            if (!TextUtils.isEmpty(sessionAccount)){
                LogUtil.i(TAG, EncryptToolNew.DESDecrypt(sessionAccount, ENCRYPT_KEY)+" -- checkAccount: " + sessionAccount);
                List<Meter> meters = MeterDao.Companion.getInstance(context).queryMeter(EncryptToolNew.DESDecrypt(sessionAccount, ENCRYPT_KEY));
                LogUtil.i(TAG, "checkAccount: "+meters.toString());
                if (meters==null||meters.size()==0){
                    ResponseData responseData = new ResponseData();
                    responseData.setCode(400);
                    responseData.setMessage("获取设备信息失败,无权限访问");
                    String outData = new Gson().toJson(responseData);
                    out.write(outData);
                    out.close();
                    throw new PermissionException("没有权限使用!");
                }
            }else if(!TextUtils.isEmpty(sessionMac)){
                LogUtil.i(TAG, EncryptToolNew.DESDecrypt(sessionMac, ENCRYPT_KEY)+" -- checkmac: " + sessionMac);
                String mac =MACUtil.getSERIAL();
                LogUtil.e(TAG,"mac:"+ mac + "                      \n" + EncryptToolNew.DESDecrypt(sessionMac,ENCRYPT_KEY));
                LogUtil.e(TAG, "hasPermission:"+ mac.equals(EncryptToolNew.DESDecrypt(sessionMac, ENCRYPT_KEY)));
                if (!mac.equals(EncryptToolNew.DESDecrypt(sessionMac, ENCRYPT_KEY))) {
                    ResponseData responseData = new ResponseData();
                    responseData.setCode(400);
                    responseData.setMessage("无权限访问");
                    String outData = new Gson().toJson(responseData);
                    out.write(outData);
                    out.close();
                    throw new PermissionException("没有权限使用!");
                }
            }else{
                ResponseData responseData = new ResponseData();
                responseData.setCode(400);
                responseData.setMessage("无权限访问");
                String outData = new Gson().toJson(responseData);
                out.write(outData);
                out.close();
                throw new PermissionException("没有权限使用!");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "check permission failed: ", e);
            ResponseData responseData = new ResponseData();
            responseData.setCode(400);
            responseData.setMessage("无权限访问");
            String outData = new Gson().toJson(responseData);
            out.write(outData);
            out.close();
            throw new PermissionException("没有权限使用!");
        }
    }

    public static String getSessionAccount() {
        return sessionAccount;
    }
}
