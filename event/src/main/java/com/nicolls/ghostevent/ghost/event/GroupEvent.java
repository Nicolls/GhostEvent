package com.nicolls.ghostevent.ghost.event;

import com.nicolls.ghostevent.ghost.core.EventExecutor;
import com.nicolls.ghostevent.ghost.core.ITarget;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GroupEvent extends BaseEvent {
    private static final String TAG = "GroupEvent";
    // 使用这个延长来确保，执行child event时的超时判断一定大于，child event本身的超时判断
    private final Semaphore semaphore = new Semaphore(0, true);
    private List<BaseEvent> childList = new ArrayList<>();
    private long timeOut = 0;
    private EventExecutor.ExecuteCallBack executeCallBack;
    private ITarget target;

    public GroupEvent(ITarget target, EventExecutor.ExecuteCallBack executeCallBack) {
        this(target, executeCallBack, new BaseEvent[]{});
    }

    public GroupEvent(ITarget target, EventExecutor.ExecuteCallBack executeCallBack, BaseEvent... events) {
        this(target, executeCallBack, Arrays.asList(events));
    }

    public GroupEvent(ITarget target, EventExecutor.ExecuteCallBack executeCallBack, List<BaseEvent> list) {
        this.target = target;
        this.executeCallBack = executeCallBack;
        if (list != null) {
            for (BaseEvent event : list) {
                event.setParent(this);
                childList.add(event);
                timeOut += event.getExecuteTimeOut();
            }
        }
        initTimeOut();
    }

    public void addEvent(BaseEvent event) {
        event.setParent(this);
        childList.add(event);
        initTimeOut();
    }

    public void addEvent(List<BaseEvent> events) {
        for (BaseEvent event : events) {
            event.setParent(this);
        }
        childList.addAll(events);
        initTimeOut();
    }

    private void initTimeOut() {
        timeOut = 0;
        if (childList != null) {
            for (BaseEvent event : childList) {
                timeOut += event.getExecuteTimeOut();
            }
        }
    }

    public void removeEvent(BaseEvent event) {
        event.setParent(null);
        childList.remove(event);
    }

    public void removeAllEvents() {
        for (BaseEvent event : childList) {
            event.setParent(null);
        }
        childList.clear();
    }

    @Override
    public void exe(final AtomicBoolean cancel, final EventCallBack eventCallBack) {
        ExecutorService executorService=target.getEventTaskPool();
        if(executorService==null||executorService.isShutdown()||executorService.isTerminated()){
            LogUtil.w(TAG,"executorService shutdown ");
            if(eventCallBack!=null){
                cancel.set(true);
                eventCallBack.onFail(null);
            }
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (cancel.get()) {
                    LogUtil.d(TAG, "cancel!");
                    if (eventCallBack != null) {
                        eventCallBack.onComplete();
                    }
                    return;
                }
                for (final BaseEvent event : childList) {
                    LogUtil.d(TAG, "execute child event:" + event.getName());
                    if (cancel.get()) {
                        LogUtil.d(TAG, "execute child executor have been cancel!");
                        break;
                    }
                    boolean isOk = onChildStart(event);
                    if (!isOk) {
                        break;
                    }

                    event.exe(cancel, new EventCallBack() {
                        @Override
                        public void onComplete() {
                            LogUtil.d(TAG, "child event completed " + event.getName() + ",release semaphore");
                            onChildCompleted(event);
                            if (executeCallBack != null) {
                                executeCallBack.onSuccess(event);
                            }
                            semaphore.release();
                        }

                        @Override
                        public void onFail(Exception e) {
                            LogUtil.e(TAG, "group execute child event error " + event.getName(), e);
                            onChildFail(event);
                            if (executeCallBack != null) {
                                executeCallBack.onFail(event);
                            }
                            LogUtil.d(TAG, "semaphore release");
                            semaphore.release();
                        }
                    });
                    long timeOut = event.getExecuteTimeOut();
                    LogUtil.d(TAG, "next child try acquire wait current child. timeOut:" + timeOut);
                    boolean ok = false;
                    try {
                        ok = semaphore.tryAcquire(timeOut, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!ok) {
                        LogUtil.d(TAG, "child acquired time out!");
                        executeCallBack.onTimeOut(event);
                    } else if (executeCallBack != null) {
                        LogUtil.d(TAG, "child acquired semaphore");
                    }
                }
                if (eventCallBack != null) {
                    eventCallBack.onComplete();
                }
            }
        });
    }

    @Override
    public long getExecuteTimeOut() {
        return timeOut + getExtendsTime();
    }

    public boolean onChildStart(BaseEvent event) {
        LogUtil.d(TAG, "onChildStart");
        return true;
    }

    public void onChildCompleted(BaseEvent event) {
        LogUtil.d(TAG, "onChildCompleted");

    }

    public void onChildFail(BaseEvent event) {
        LogUtil.d(TAG, "onChildFail");

    }

    @Override
    public String toString() {
        return "GroupEvent{" +
                "childList=" + Arrays.toString(childList.toArray()) +
                '}';
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public String getDescribe() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("group", true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
