package com.nicolls.ghostevent.ghost.event;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.event.behavior.ScrollReadMoreEventBehavior;
import com.nicolls.ghostevent.ghost.event.model.Line;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScrollVerticalEvent extends BaseEvent {
    private static final String TAG = "ScrollVerticalEvent";
    private IWebTarget target;
    private Line line;
    private int distance;
    private static final long DEFAULT_ANIM_DURATION = 1000;
    private static final long ANIM_END_DELAY = 500;
    private static final long MAX_ANIM_DURATION = 5 * 1000 + ANIM_END_DELAY;
    private long animDuration = DEFAULT_ANIM_DURATION;
    private ScrollReadMoreEventBehavior scrollEventBehavior;

    public ScrollVerticalEvent(ScrollVerticalEvent event) {
        this.target = event.target;
    }

    public ScrollVerticalEvent(IWebTarget target, final int from, final int to) {
        this.target = target;
        this.line = new Line(from, to);
    }

    public ScrollVerticalEvent(IWebTarget target, Line line) {
        this.target = target;
        this.line = line;
    }

    public ScrollVerticalEvent(IWebTarget target, int distance) {
        this.distance = distance;
        this.target = target;
    }

    public ScrollVerticalEvent(IWebTarget target, ScrollReadMoreEventBehavior scrollEventBehavior) {
        this.scrollEventBehavior = scrollEventBehavior;
        this.target = target;
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
                if (scrollEventBehavior != null) {
                    line = scrollEventBehavior.onStart(cancel);
                    if (line == null) {
                        LogUtil.w(TAG, "line null ");
                        if (eventCallBack != null) {
                            eventCallBack.onComplete();
                        }
                        return;
                    }
                }
                if (line == null) {
                    final WebView webView = (WebView) target;
                    int from = webView.getScrollY();
                    int to = from + distance;
                    line = new Line(from, to);
                }
                int distance = Math.abs(line.to - line.from);
                int displayHeight = GhostUtils.displayHeight;
                if (distance >= displayHeight) {
                    animDuration = (distance / displayHeight) * DEFAULT_ANIM_DURATION;
                    if (animDuration > MAX_ANIM_DURATION) {
                        animDuration = MAX_ANIM_DURATION;
                    }
                } else if (distance > displayHeight / 2) {
                    animDuration = DEFAULT_ANIM_DURATION;
                } else {
                    animDuration = DEFAULT_ANIM_DURATION / 2;
                }
                if (animDuration < DEFAULT_ANIM_DURATION / 4) {
                    animDuration = DEFAULT_ANIM_DURATION / 4;
                }

                final WebView webView = (WebView) target;
                LogUtil.d(TAG, "scroll vertical event start anim duration:" + animDuration);
                LogUtil.d(TAG, "anim from:" + line.from + " to:" + line.to);
                final Semaphore semaphore = new Semaphore(0, true);

                final ObjectAnimator animator = ObjectAnimator.ofInt(webView, "scrollY", line.from, line.to);
                animator.setDuration(animDuration);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        LogUtil.d(TAG, "onAnimationStart scrollY:" + webView.getScrollY());
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        LogUtil.d(TAG, "onAnimationEnd scrollY:" + webView.getScrollY());
                        delayRelease();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        LogUtil.d(TAG, "onAnimationCancel scrollY:" + webView.getScrollY());
                        delayRelease();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }

                    private void delayRelease() {
                        target.getMainHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.d(TAG, "release semaphore");
                                semaphore.release();
                            }
                        }, ANIM_END_DELAY);
                    }
                });
                target.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        animator.start();
                    }
                });
                boolean ok = false;
                try {
                    ok = semaphore.tryAcquire(getExecuteTimeOut(), TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                animator.removeAllListeners();
                if (!ok) {
                    LogUtil.d(TAG, "scroll vertical event time out");
                } else {
                    LogUtil.d(TAG, "scroll vertical event completed scrollY:" + webView.getScrollY());
                }
                if (eventCallBack != null) {
                    eventCallBack.onComplete();
                }
            }
        });
    }

    @Override
    public long getExecuteTimeOut() {
        return MAX_ANIM_DURATION + getExtendsTime(); // 毫秒
    }

    @Override
    public String toString() {
        return "ScrollVerticalEvent{" +
                "target=" + target +
                ", line=" + line +
                ", animDuration=" + animDuration +
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
            jsonObject.put("scroll", (line == null ? "null" : line.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
