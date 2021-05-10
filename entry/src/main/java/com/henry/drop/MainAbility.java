package com.henry.drop;

import com.henry.drop.utils.LogUtil;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.IAbilityContinuation;
import ohos.aafwk.content.Intent;
import com.henry.drop.slice.*;
import ohos.aafwk.content.IntentParams;

import java.util.ArrayList;
import java.util.List;

public class MainAbility extends Ability implements IAbilityContinuation {
    private static final String TAG = MainAbility.class.getSimpleName();

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        requestPermission();
    }


    private void requestPermission() {
        String[] permissions = {
                "ohos.permission.DISTRIBUTED_DATASYNC"
                //,
                //"ohos.permission.READ_USER_STORAGE",
                //"ohos.permission.WRITE_USER_STORAGE"
        };
        List<String> applyPermissions = new ArrayList<>();
        for (String element : permissions) {
            LogUtil.info(TAG, "check permission: " + element);
            if (verifySelfPermission(element) != 0) {
                if (canRequestPermission(element)) {
                    applyPermissions.add(element);
                } else {
                    LogUtil.info(TAG, "user deny permission");
                }
            } else {
                LogUtil.info(TAG, "user granted permission: " + element);
            }
        }
        requestPermissionsFromUser(applyPermissions.toArray(new String[0]), 0);
    }

    @Override
    public boolean onStartContinuation() {
        return false;
    }

    @Override
    public boolean onSaveData(IntentParams intentParams) {
        return false;
    }

    @Override
    public boolean onRestoreData(IntentParams intentParams) {
        return false;
    }

    @Override
    public void onCompleteContinuation(int i) {

    }
}
