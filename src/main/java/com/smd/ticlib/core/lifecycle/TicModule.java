package com.smd.ticlib.core.lifecycle;

import com.smd.ticlib.core.target.TicTargetKind;

public interface TicModule {

    String id();

    boolean supports(TicTargetKind kind);

    default void onBuild(TicLifecycleContext context) {
    }

    default void onCopy(TicLifecycleContext oldContext, TicLifecycleContext newContext) {
    }

    default void onReplay(TicLifecycleContext context) {
    }

    default void onValidate(TicLifecycleContext context) {
    }
}
