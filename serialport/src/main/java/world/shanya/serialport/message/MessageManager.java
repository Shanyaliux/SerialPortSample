package world.shanya.serialport.message;

import android.app.Activity;
import android.os.Message;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {

    private volatile static MessageManager mInstance;
    private static MessageLooper messageLooper = MessageLooper.getMessageLooper();
    private List<ListenerContainer> containerList = new ArrayList<>();

    public synchronized static MessageManager getInstance() {
        if (mInstance == null) {
            synchronized (MessageManager.class) {
                if (mInstance == null) {
                    mInstance = new MessageManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 注册事件
     *
     * @param activity
     * @param cmd      事件唯一标志
     * @param listener 事件回调
     */
    public void registerMessageReceiver(Activity activity, String cmd, MessageLooper.OnMessageListener listener) {
        if (messageLooper != null) {
            messageLooper.registerReceiver(cmd, listener);
        }
        ListenerContainer container = getListenerContainer(activity);
        if (container == null) {
            container = new ListenerContainer(activity);
            containerList.add(container);
        }
        container.addListener(listener);
    }


    /**
     * 注册事件
     *
     * @param fragment
     * @param cmd
     * @param listener
     */
    public void registerMessageReceiver(Fragment fragment, String cmd, MessageLooper.OnMessageListener listener) {
        if (messageLooper != null) {
            messageLooper.registerReceiver(cmd, listener);
        }
        ListenerContainer container = getListenerContainer(fragment);
        if (container == null) {
            container = new ListenerContainer(fragment);
            containerList.add(container);
        }
        container
                .addListener(listener);
    }


    /**
     * 取消事件
     *
     * @param activity
     */
    public void unRegisterMessageReceiver(Activity activity) {
        ListenerContainer container = getListenerContainer(activity);
        if (container != null) {
            container.clear();
        }
    }

    public void unRegisterMessageReceiver(Fragment fragment) {
        ListenerContainer container = getListenerContainer(fragment);
        if (container != null) {
            container.clear();
        }
    }

    /**
     * 发送事件
     *
     * @param cmd
     * @param message
     */
    public void sendMessage(String cmd, Message message) {
        messageLooper.sendMessage(cmd, message);
    }

    public ListenerContainer getListenerContainer(Activity activity) {
        if (activity != null && containerList != null) {
            for (ListenerContainer container : containerList) {
                if (container == null) {
                    continue;
                }
                if (container.activity != null && container.activity == activity) {
                    return container;
                }
            }
        }
        return null;
    }

    public ListenerContainer getListenerContainer(Fragment fragment) {
        if (fragment != null && containerList != null) {
            for (ListenerContainer container : containerList) {
                if (container == null) {
                    continue;
                }
                if (container.fragment != null && container.fragment == fragment) {
                    return container;
                }
            }
        }
        return null;
    }

    /**
     * 将同一个Activity或Fragment的OnMessageListener放到一起，便于统一取消事件
     */
    private static class ListenerContainer {
        Activity activity;
        Fragment fragment;
        ArrayList<MessageLooper.OnMessageListener> listeners;

        ListenerContainer(Activity activity) {
            this.activity = activity;
        }

        ListenerContainer(Fragment fragment) {
            this.fragment = fragment;
        }

        /**
         * 添加监听器
         *
         * @param listener
         */
        public void addListener(MessageLooper.OnMessageListener listener) {
            if (listener != null) {
                if (listeners == null) {
                    listeners = new ArrayList<>();
                }
                listeners.add(listener);
            }
        }

        /**
         * 清空监听器
         */
        public void clear() {
            activity = null;
            fragment = null;
            if (listeners != null) {
                for (MessageLooper.OnMessageListener listener : listeners) {
                    if (listener != null) {
                        messageLooper.unRegisterReciver(listener);
                    }
                }
                listeners.clear();
                listeners = null;
            }
        }
    }

}
