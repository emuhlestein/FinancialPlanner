package com.intelliviz.retirementhelper.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class MilestonesRemoteViewsService extends RemoteViewsService {
    public MilestonesRemoteViewsService() {
        Log.d("TAG", "HERE");
    }
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MilestonesRemoteViewsFactory(this);
    }
}
