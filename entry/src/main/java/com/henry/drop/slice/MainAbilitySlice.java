package com.henry.drop.slice;

import com.henry.drop.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.TextField;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.Revocable;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.audio.AudioManager;
import ohos.media.audio.SoundPlayer;
import ohos.media.audio.ToneDescriptor;

public class MainAbilitySlice extends AbilitySlice {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x00201, "MainAbilitySlice");
    TextField t_TotalLiquid ,t_SpendTime;
    double t_SpendTimeDouble ,t_TotalLiquidDouble;
    Context tContext ;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        t_SpendTime = (TextField) findComponentById(ResourceTable.Id_drip_spend_Time);
        t_TotalLiquid = (TextField) findComponentById(ResourceTable.Id_liquid_total);

        Button button = (Button) findComponentById(ResourceTable.Id_clickButton);
        if (button != null) {
            // 为按钮设置点击回调
            button.setClickedListener(new Component.ClickedListener() {
                @Override
                public void onClick(Component component) {
                    tContext = getContext();

                    try {
                        t_SpendTimeDouble = Double.parseDouble(t_SpendTime.getText());
                        t_TotalLiquidDouble = Double.parseDouble(t_TotalLiquid.getText());
                    } catch (NumberFormatException e) {
                        new ToastDialog(tContext).setText(t_SpendTime.getText() + t_TotalLiquid.getText() + "录入存在菲数字，请修改").show();
                        return;
                    }

                    double time = t_TotalLiquidDouble * t_SpendTimeDouble;

                    int time_minutes_int = (int) Math.floor(time / 60);
                    int time_seconed_int = (int) Math.floor(time % 60);

                    new ToastDialog(tContext).setText("预计全部滴完时间为：" + time_minutes_int + "分钟" + time_seconed_int+"秒").show();

                    //二十滴是一毫升

                    TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.HIGH);
                    Revocable revocable = globalTaskDispatcher.asyncDispatch(new Runnable() {
                        @Override
                        public void run() {
                            HiLog.info(label, "async task1 run");
                            try {
                                Thread.sleep((long) (time*980));
                                RingSound();
                                ToastDialog tToastDialog = new ToastDialog(MainAbilitySlice.this);
                                tToastDialog.setText("时间到了！请注意点滴！！");
                                tToastDialog.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    HiLog.info(label, "after async task1");

                }
            });
        }
    }
    public void RingSound() {
        // 步骤1：实例化对象
        SoundPlayer soundPlayer = new SoundPlayer();
        // 步骤2：创建DTMF_0（高频1336Hz，低频941Hz）持续时间1000ms的tone音
        soundPlayer.createSound(ToneDescriptor.ToneType.DTMF_0, 2000);
        // 步骤3：tone应播放，暂停和资源释放
        soundPlayer.play();
        HiLog.info(label,"ring over");
        soundPlayer.createSound(ToneDescriptor.ToneType.DTMF_1, 2000);
        // 步骤3：tone应播放，暂停和资源释放
        soundPlayer.play();
        soundPlayer.createSound(ToneDescriptor.ToneType.DTMF_2, 2000);
        // 步骤3：tone应播放，暂停和资源释放
        soundPlayer.play();

        soundPlayer.pause();
        soundPlayer.release();
    }


    public void RingTone() {
        // 步骤1：实例化对象
        SoundPlayer soundPlayer = new SoundPlayer(AudioManager.AudioVolumeType.STREAM_MUSIC.getValue());
        // 步骤2：指定音频资源加载并创建短音
        int soundId = soundPlayer.createSound("/system/xxx");
        // 步骤3：指定音量，循环次数和播放速度
        SoundPlayer.SoundPlayerParameters parameters = new SoundPlayer.SoundPlayerParameters();
        parameters.setVolumes(new SoundPlayer.AudioVolumes());
        parameters.setLoop(10);
        parameters.setSpeed(1.0f);
        // 步骤4：短音播放
        soundPlayer.play(soundId, parameters);
        // 步骤5：停止播放
        soundPlayer.stop(soundId);
        // 步骤6：释放短音资源
        soundPlayer.deleteSound(soundId);
    }



    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
