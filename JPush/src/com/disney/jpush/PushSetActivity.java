package com.disney.jpush;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.disney.push.R;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.MultiActionsNotificationBuilder;
import cn.jpush.android.api.TagAliasCallback;


public class PushSetActivity extends InstrumentedActivity implements OnClickListener {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.push_set_dialog);
        initListener();
    }

    private void initListener() {
        findViewById(R.id.btn_add_tag).setOnClickListener(this);
        findViewById(R.id.btn_set_tag).setOnClickListener(this);
        findViewById(R.id.btn_delete_tag).setOnClickListener(this);
        findViewById(R.id.btn_get_alltag).setOnClickListener(this);
        findViewById(R.id.btn_clean_tag).setOnClickListener(this);
        findViewById(R.id.btn_check_tag).setOnClickListener(this);
        findViewById(R.id.btn_set_alias).setOnClickListener(this);
        findViewById(R.id.btn_get_alias).setOnClickListener(this);
        findViewById(R.id.btn_delete_alias).setOnClickListener(this);
        findViewById(R.id.set_style_first).setOnClickListener(this);
        findViewById(R.id.set_style_second).setOnClickListener(this);
        findViewById(R.id.btn_set_time).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_style_first:
                setAddActionsStyle();
                break;
            case R.id.set_style_second:
                setStyleBasic();
                break;
            case R.id.setStyle2:
                setStyleCustom();
                break;
            case R.id.btn_set_time:
                Intent intent = new Intent(PushSetActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                onTagAliasAction(view);
                break;
        }
    }

    TagAliasCallback tagAlias = new TagAliasCallback() {
        @Override
        public void gotResult(int responseCode, String alias, Set<String> tags) {
            Log.e(TAG,"responseCode:"+responseCode+",alias:"+alias+",tags:"+tags);
        }
    };


    /**
     * Set notification popup style - basic property
     */
    private void setStyleBasic() {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(PushSetActivity.this);
        builder.statusBarDrawable = R.drawable.ic_launcher;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
        JPushInterface.setPushNotificationBuilder(1, builder);
        Toast.makeText(PushSetActivity.this, "Basic Builder - 1", Toast.LENGTH_SHORT).show();
    }


    /**
     * Set notification popup style - notification layout
     */
    private void setStyleCustom() {
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(PushSetActivity.this, R.layout.customer_notitfication_layout, R.id.icon, R.id.title, R.id.text);
        builder.layoutIconDrawable = R.drawable.ic_launcher;
        builder.developerArg0 = "developerArg2";
        JPushInterface.setPushNotificationBuilder(2, builder);
        Toast.makeText(PushSetActivity.this, "Custom Builder - 2", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setAddActionsStyle() {
        MultiActionsNotificationBuilder builder = new MultiActionsNotificationBuilder(PushSetActivity.this);
        builder.addJPushAction(R.drawable.jpush_ic_richpush_actionbar_back, "first", "my_extra1");
        builder.addJPushAction(R.drawable.jpush_ic_richpush_actionbar_back, "second", "my_extra2");
        builder.addJPushAction(R.drawable.jpush_ic_richpush_actionbar_back, "third", "my_extra3");
        JPushInterface.setPushNotificationBuilder(10, builder);

        Toast.makeText(PushSetActivity.this, "AddActions Builder - 10", Toast.LENGTH_SHORT).show();
    }


    /**===========================================================================**/
    /**=========================TAG/ALIAS 相关=====================================**/
    /**===========================================================================**/

    /**
     * Deal with tag/alias click
     * */
    public void onTagAliasAction(View view) {
        Set<String> tags = null;
        String alias = null;
        int action = -1;
        boolean isAliasAction = false;
        switch (view.getId()){
            case R.id.btn_add_tag:
                tags = getInPutTags();
                if(tags == null){
                    return;
                }
                action = TagAliasOperatorHelper.ACTION_ADD;
                break;
            case R.id.btn_set_tag:
                tags = getInPutTags();
                if(tags == null){
                    return;
                }
                action = TagAliasOperatorHelper.ACTION_SET;
                break;
            case R.id.btn_delete_tag:
                tags = getInPutTags();
                if(tags == null){
                    return;
                }
                action = TagAliasOperatorHelper.ACTION_DELETE;
                break;
            case R.id.btn_get_alltag:
                action = TagAliasOperatorHelper.ACTION_GET;
                break;
            case R.id.btn_clean_tag:
                action = TagAliasOperatorHelper.ACTION_CLEAN;
                break;
            case R.id.btn_check_tag:
                tags = getInPutTags();
                if(tags == null){
                    return;
                }
                action = TagAliasOperatorHelper.ACTION_CHECK;
                break;
            case R.id.btn_set_alias:
                alias = getInPutAlias();
                if(TextUtils.isEmpty(alias)){
                    return;
                }
                isAliasAction = true;
                action = TagAliasOperatorHelper.ACTION_SET;
                break;
            case R.id.btn_get_alias:
                isAliasAction = true;
                action = TagAliasOperatorHelper.ACTION_GET;
                break;
            case R.id.btn_delete_alias:
                isAliasAction = true;
                action = TagAliasOperatorHelper.ACTION_DELETE;
                break;
            default:
                return;
        }
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.action = action;
        TagAliasOperatorHelper.sequence++;
        if(isAliasAction){
            tagAliasBean.alias = alias;
        }else{
            tagAliasBean.tags = tags;
        }
        tagAliasBean.isAliasAction = isAliasAction;
        TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(), TagAliasOperatorHelper.sequence,tagAliasBean);
    }
    /**
     * get input alias
     * */
    private String getInPutAlias(){
        EditText aliasEdit = (EditText) findViewById(R.id.et_alias);
        String alias = aliasEdit.getText().toString().trim();
        if (TextUtils.isEmpty(alias)) {
            Toast.makeText(getApplicationContext(), R.string.error_alias_empty, Toast.LENGTH_SHORT).show();
            return null;
        }
        if (!PushUtil.isValidTagAndAlias(alias)) {
            Toast.makeText(getApplicationContext(), R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
            return null;
        }
        return alias;
    }
    /**
     * get input tags
     * */
    private Set<String> getInPutTags(){
        EditText tagEdit = (EditText) findViewById(R.id.et_tag);
        String tag = tagEdit.getText().toString().trim();
        if (TextUtils.isEmpty(tag)) {
            Toast.makeText(getApplicationContext(), R.string.error_tag_empty, Toast.LENGTH_SHORT).show();
            return null;
        }

        String[] sArray = tag.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String tagItem : sArray) {
            if (!PushUtil.isValidTagAndAlias(tagItem)) {
                Toast.makeText(getApplicationContext(), R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
                return null;
            }
            tagSet.add(tagItem);
        }
        if(tagSet.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.error_tag_empty, Toast.LENGTH_SHORT).show();
            return null;
        }
        return tagSet;
    }
}