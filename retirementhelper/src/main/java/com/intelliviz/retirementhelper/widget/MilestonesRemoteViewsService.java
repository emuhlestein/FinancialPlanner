package com.intelliviz.retirementhelper.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class MilestonesRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MilestonesRemoteViewsFactory(this);
    }
}
