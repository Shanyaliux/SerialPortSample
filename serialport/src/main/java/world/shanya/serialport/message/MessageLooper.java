package world.shanya.serialport.message;

import android.os.Message;
import android.text.TextUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class MessageLooper {

    private volatile static MessageLooper mInstance;
    /**
     * 由于会有频繁的增、删操作，因此采用线程安全的ConCurrentHasMap()，Vector也是线程安全的
     */
    private Map<String, Vector<OnMessageListener>> mMessageReceiver = new ConcurrentHashMap<>();

    public interface OnMessageListener {
        void onMessage(Message msg);
    }


    public synchronized static MessageLooper getMessageLooper() {
        if (mInstance == null) {
            synchronized (MessageLooper.class) {
                if (mInstance == null) {
                    mInstance = new MessageLooper();
                }
            }
        }
        return mInstance;
    }

    private MessageLooper() {
        super();
    }

    /**
     * 注册事件
     *
     * @param cmd      事件唯一标志
     * @param listener 回调
     */
    public void registerReceiver(String cmd, OnMessageListener listener) {
        if (!TextUtils.isEmpty(cmd) && listener != null) {
            Vector<OnMessageListener> listeners = mMessageReceiver.get(cmd);
            if (listeners == null) {
                listeners = new Vector<>();
            }
            listeners.add(listener);
            this.mMessageReceiver.put(cmd, listeners);
        }
    }

    /**
     * 注销注册事件
     *
     * @param listener
     */
    public void unRegisterReciver(OnMessageListener listener) {
        if (listener != null) {
            Iterator iterator = this.mMessageReceiver.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Vector<OnMessageListener>> entry = (Map.Entry) iterator.next();
                if (entry != null) {
                    String key = entry.getKey();
                    Vector<OnMessageListener> listeners = this.mMessageReceiver.get(key);
                    if (listeners != null && listeners.remove(listener)) {
                        return;
                    }
                }
            }
        }
    }

    /**
     * 发送事件
     *
     * @param cmd     事件唯一标志
     * @param message 信息
     */
    public void sendMessage(String cmd, Message message) {
        if (!TextUtils.isEmpty(cmd)) {
            Vector<OnMessageListener> listeners = this.mMessageReceiver.get(cmd);
            if (listeners != null) {
                for (int i = 0; i < listeners.size(); i++) {
                    OnMessageListener listener = listeners.get(i);
                    listener.onMessage(message);
                }
            }
        }
    }

}
