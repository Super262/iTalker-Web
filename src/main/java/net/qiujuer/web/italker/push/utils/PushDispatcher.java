package net.qiujuer.web.italker.push.utils;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.base.Strings;
import net.qiujuer.web.italker.push.bean.api.base.PushModel;
import net.qiujuer.web.italker.push.bean.db.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PushDispatcher {
    private static final String appId = "Rr51sROK4B8FXbq0TUjAF5";
    private static final String appKey = "eurxTdqHECAKgc7s4xtUe9";
    private static final String masterSecret = "2zqRh5hMIY93LBqVlBtsi";
    private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";

    private final IGtPush pusher;
    private final List<BatchBean> beans = new ArrayList<>();

    public PushDispatcher() {
        pusher = new IGtPush(host, appKey, masterSecret);
    }

    public boolean add(User receiver, PushModel model) {
        if (receiver == null || model == null ||
                Strings.isNullOrEmpty(receiver.getPushId()))
            return false;

        String pushString = model.getPushString();
        if (Strings.isNullOrEmpty(pushString))
            return false;


        BatchBean bean = buildMessage(receiver.getPushId(), pushString);
        beans.add(bean);
        return true;
    }

    private BatchBean buildMessage(String clientId, String text) {

        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(text);
        template.setTransmissionType(0);

        SingleMessage message = new SingleMessage();
        message.setData(template);
        message.setOffline(true);
        message.setOfflineExpireTime(24 * 3600 * 1000);

        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(clientId);

        return new BatchBean(message, target);
    }


    public boolean submit() {
        IBatch batch = pusher.getBatch();

        boolean haveData = false;

        for (BatchBean bean : beans) {
            try {
                batch.add(bean.message, bean.target);
                haveData = true;
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        if (!haveData)
            return false;

        IPushResult result = null;
        try {
            result = batch.submit();
        } catch (IOException e) {
            e.printStackTrace();

            try {
                batch.retry();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

        if (result != null) {
            try {
                Logger.getLogger("PushDispatcher")
                        .log(Level.INFO, (String) result.getResponse().get("result"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Logger.getLogger("PushDispatcher")
                .log(Level.WARNING, "Exception in pushing! ");
        return false;

    }
    private static class BatchBean {
        SingleMessage message;
        Target target;

        BatchBean(SingleMessage message, Target target) {
            this.message = message;
            this.target = target;
        }
    }
}