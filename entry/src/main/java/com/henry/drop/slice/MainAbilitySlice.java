package com.henry.drop.slice;

import com.henry.drop.CountDownBean;
import com.henry.drop.ResourceTable;
import com.henry.drop.ui.DeviceSelectDialog;
import com.henry.drop.ui.WidgetHelper;
import com.henry.drop.utils.LogUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityContinuation;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.TextField;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.Revocable;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.distributedschedule.interwork.DeviceManager;
import ohos.media.audio.SoundPlayer;
import ohos.media.audio.ToneDescriptor;
import ohos.distributedschedule.interwork.DeviceInfo;

import java.util.List;
import ohos.agp.components.Image;

import static ohos.data.search.schema.PhotoItem.TAG;

public class MainAbilitySlice extends AbilitySlice  implements IAbilityContinuation {
    private static final String TAG = MainAbilitySlice.class.getSimpleName();
    TextField t_TotalLiquid ,t_SpendTime;
    double t_SpendTimeDouble ,t_TotalLiquidDouble, t_TimeToSpendAll ,t_TimeHaveUsed;
    Context tContext ;
    private CountDownBean cachedPageData;

    private Image doConnectImg;


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        doConnectImg = (Image) findComponentById(ResourceTable.Id_mail_edit_continue);




        doConnectImg.setClickedListener(
                clickedView -> {
                    // 通过FLAG_GET_ONLINE_DEVICE标记获得在线设备列表
                    List<DeviceInfo> deviceInfoList = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
                    if (deviceInfoList.size() < 1) {
                        WidgetHelper.showTips(this, "无在网设备");
                    } else {
                        DeviceSelectDialog dialog = new DeviceSelectDialog(this);
                        // 点击后迁移到指定设备
                        dialog.setListener(
                                deviceInfo -> {
                                    LogUtil.debug(TAG, deviceInfo.getDeviceName());
                                    LogUtil.info(TAG, "continue button click");
                                    try {
                                        // 开始任务迁移
                                        continueAbility();
                                        LogUtil.info(TAG, "continue button click end");
                                    } catch (IllegalStateException | UnsupportedOperationException e) {
                                        WidgetHelper.showTips(this, ResourceTable.String_tips_mail_continue_failed);
                                    }
                                    dialog.hide();
                                });
                        dialog.show();
                    }
                });


        t_SpendTime = (TextField) findComponentById(ResourceTable.Id_drip_spend_Time);
        t_TotalLiquid = (TextField) findComponentById(ResourceTable.Id_liquid_total);


//      for debug
//        cachedPageData = new CountDownBean();
//        // 初始化界面
//        cachedPageData.setTimeHaveUsed(1.00);
//        cachedPageData.setTimeToSpendAll(5.00);
//        cachedPageData.setTimeSpendFor20Dip(1.00);
//        cachedPageData.setTotalLiquid(5.00);
        if(cachedPageData != null){
            tContext = getContext();

            t_TotalLiquid.setText(cachedPageData.getTotalLiquid()+"");
            t_SpendTime.setText(cachedPageData.getTimeSpendFor20Dip()+"");
            t_TimeToSpendAll=cachedPageData.getTimeToSpendAll();
            t_TimeHaveUsed=cachedPageData.getTimeHaveUsed();
            if(cachedPageData.getTimeHaveUsed()>0&&cachedPageData.getTimeSpendFor20Dip()>0&&cachedPageData.getTimeToSpendAll()>0&&
            cachedPageData.getTotalLiquid()>0){

                double time = cachedPageData.getTimeToSpendAll()-cachedPageData.getTimeHaveUsed();

                int time_minutes_int = (int) Math.floor(time / 60);
                int time_seconed_int = (int) Math.floor(time % 60);

                if (time_minutes_int!=0){
                    WidgetHelper.showTips(this, "预计全部滴完时间为：" + time_minutes_int + "分钟" + time_seconed_int+"秒");
                    //new ToastDialog(tContext).setText("预计全部滴完时间为：" + time_minutes_int + "分钟" + time_seconed_int+"秒").show();
                }else{
                    //new ToastDialog(tContext).setText("预计全部滴完时间为：" + time_seconed_int+"秒").show();
                    WidgetHelper.showTips(this, "预计全部滴完时间为：" + time_seconed_int+"秒");
                }


                //二十滴是一毫升
                TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.HIGH);
                Revocable revocable = globalTaskDispatcher.asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.info(TAG, "async task1 run");
                        try {
                            do {
                                Thread.sleep(500);
                                t_TimeHaveUsed=t_TimeHaveUsed+0.5;
                            }while (t_TimeHaveUsed<cachedPageData.getTimeToSpendAll()*0.98);

                            RingSound();

                            getUITaskDispatcher().asyncDispatch(new Runnable() {
                                @Override
                                public void run() {
                                    //ToastDialog tToastDialog = new ToastDialog(MainAbilitySlice.this);
                                    //tToastDialog.setText("时间到了！请注意点滴！！");
                                    //tToastDialog.show();
                                    WidgetHelper.showTips(MainAbilitySlice.this, "时间到了！请注意点滴！！");

                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                LogUtil.info( TAG, "after async task1");


            }
        }

        Button button = (Button) findComponentById(ResourceTable.Id_clickButton);
        // 为按钮设置点击回调
        button.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                tContext = getContext();

                try {
                    t_SpendTimeDouble = Double.parseDouble(t_SpendTime.getText());
                    t_TotalLiquidDouble = Double.parseDouble(t_TotalLiquid.getText());
                } catch (NumberFormatException e) {
                    //new ToastDialog(tContext).setText(t_SpendTime.getText() + t_TotalLiquid.getText() + "录入存在非数字，请修改").show();
                    WidgetHelper.showTips(MainAbilitySlice.this, ""+t_SpendTime.getText() + t_TotalLiquid.getText() + "录入存在非数字，请修改");

                    return;
                }

                double time = t_TotalLiquidDouble * t_SpendTimeDouble;

                int time_minutes_int = (int) Math.floor(time / 60);
                int time_seconed_int = (int) Math.floor(time % 60);

                if (time_minutes_int!=0){
                    //new ToastDialog(tContext).setText("预计全部滴完时间为：" + time_minutes_int + "分钟" + time_seconed_int+"秒").show();
                    WidgetHelper.showTips(MainAbilitySlice.this,"预计全部滴完时间为：" + time_minutes_int + "分钟" + time_seconed_int+"秒");
                }else{
                    //new ToastDialog(tContext).setText("预计全部滴完时间为：" + time_seconed_int+"秒").show();
                    WidgetHelper.showTips(MainAbilitySlice.this,"预计全部滴完时间为：" + time_seconed_int+"秒");


                }

                //二十滴是一毫升
                TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.HIGH);
                Revocable revocable = globalTaskDispatcher.asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.info(TAG, "async task1 run");
                        try {
                            do {
                                Thread.sleep(500);
                                t_TimeHaveUsed=t_TimeHaveUsed+0.5;
                            }while (t_TimeHaveUsed<time*0.98);

                            RingSound();

                            getUITaskDispatcher().asyncDispatch(new Runnable() {
                                @Override
                                public void run() {
                                    //ToastDialog tToastDialog = new ToastDialog(MainAbilitySlice.this);
                                    //tToastDialog.setText("时间到了！请注意点滴！！");
                                    //tToastDialog.show();
                                    WidgetHelper.showTips(MainAbilitySlice.this,"时间到了！请注意点滴！！");
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                LogUtil.info( TAG, "after async task1");

            }
        });
    }


    public void RingSound() {
        LogUtil.info(TAG,"Now ringing");
        // 步骤1：实例化对象
        SoundPlayer soundPlayer = new SoundPlayer();
        // 步骤2：创建DTMF_0（高频1336Hz，低频941Hz）持续时间1000ms的tone音
        soundPlayer.createSound(ToneDescriptor.ToneType.DTMF_0, 2000);
        // 步骤3：tone应播放，暂停和资源释放
        soundPlayer.play();
        soundPlayer.createSound(ToneDescriptor.ToneType.DTMF_1, 2000);
        // 步骤3：tone应播放，暂停和资源释放
        soundPlayer.play();
        soundPlayer.createSound(ToneDescriptor.ToneType.DTMF_2, 2000);
        // 步骤3：tone应播放，暂停和资源释放
        soundPlayer.play();

        soundPlayer.pause();
        soundPlayer.release();
        LogUtil.info(TAG," ringing ended");

    }


    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }


    //跨设备迁移支持：
    @Override
    public boolean onStartContinuation() {
        return false;
    }

    @Override
    public boolean onSaveData(IntentParams intentParams) {
        CountDownBean tCountDownBean = getCountDownBeanData();
        LogUtil.info(TAG, "begin onSaveData");
        tCountDownBean.saveDataToParams(intentParams);
        LogUtil.info(TAG, "end onSaveData");
        return true;
     }

    private CountDownBean getCountDownBeanData() {
        CountDownBean data = new CountDownBean();
        data.setTotalLiquid(t_SpendTimeDouble);
        data.setTimeSpendFor20Dip(t_TotalLiquidDouble);
        data.setTimeToSpendAll(t_TimeToSpendAll);
        data.setTimeHaveUsed(t_TimeHaveUsed);
        return data;
    }

    @Override
    public boolean onRestoreData(IntentParams intentParams) {
        LogUtil.info(TAG, "begin onRestoreData");
        cachedPageData = new CountDownBean(intentParams);
        LogUtil.info(TAG, "end onRestoreData, mail data");
        return true;
    }

    @Override
    public void onCompleteContinuation(int i) {
        LogUtil.info(TAG, "onCompleteContinuation");
        terminateAbility();
    }
}
