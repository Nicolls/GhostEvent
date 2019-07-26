package com.nicolls.ghostevent.ghost.event;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;

import com.nicolls.ghostevent.ghost.core.IWebTarget;
import com.nicolls.ghostevent.ghost.utils.GhostUtils;
import com.nicolls.ghostevent.ghost.utils.LogUtil;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class ScrollVerticalEvent extends BaseEvent {
    private static final String TAG = "ScrollVerticalEvent";
    private static final long DEFAULT_ANIM_DURATION = 800;
    private IWebTarget target;
    private int from;
    private int to;
    private int distance;
    private boolean isScrollByDistance = false;

    public ScrollVerticalEvent(IWebTarget target, int from, int to) {
        super(target);
        this.target = target;
        this.from = from;
        this.to = to;
        this.setName(TAG);
        isScrollByDistance = false;
    }

    public ScrollVerticalEvent(IWebTarget target, int distance) {
        super(target);
        this.target = target;
        this.distance = distance;
        this.setName(TAG);
        isScrollByDistance = true;
    }

    @Override
    public Completable exe(AtomicBoolean cancel) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                final WebView webView = (WebView) target;
                if (isScrollByDistance) {
                    from = webView.getScrollY();
                    to = from + distance;
                }
                long animDuration = DEFAULT_ANIM_DURATION;
                int distance = Math.abs(to - from);
                int displayHeight = GhostUtils.displayHeight;
                if (distance >= displayHeight) {
                    animDuration = (distance / displayHeight) * DEFAULT_ANIM_DURATION;
                } else if (distance > displayHeight / 2) {
                    animDuration = DEFAULT_ANIM_DURATION;
                } else {
                    animDuration = DEFAULT_ANIM_DURATION / 2;
                }
                if (animDuration < DEFAULT_ANIM_DURATION / 4) {
                    animDuration = DEFAULT_ANIM_DURATION / 4;
                }
                LogUtil.d(TAG, "scroll vertical event start anim duration:" + animDuration);
                LogUtil.d(TAG, "anim from:" + from + " to:" + to);
                final Semaphore semaphore = new Semaphore(0,true);

                final ObjectAnimator animator = ObjectAnimator.ofInt(webView, "scrollY", from, to);
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
                        semaphore.release();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        LogUtil.d(TAG, "onAnimationCancel scrollY:" + webView.getScrollY());
                        semaphore.release();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                target.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        animator.start();
                    }
                });
                semaphore.acquire();
                animator.removeAllListeners();
                LogUtil.d(TAG, "scroll vertical event completed scrollY:" + webView.getScrollY());
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public String toString() {
        return "ScrollVerticalEvent{" +
                "target=" + target +
                ", from=" + from +
                ", to=" + to +
                ", distance=" + distance +
                '}';
    }
}
