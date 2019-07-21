package com.nicolls.ghostevent.ghost;

import android.content.Context;

import androidx.annotation.NonNull;

public class BackgroundGhost extends Ghost {
    private final Context context;

    public BackgroundGhost(@NonNull final Context context){
        this.context=context;
    }
    @Override
    public void init() {

    }
}
