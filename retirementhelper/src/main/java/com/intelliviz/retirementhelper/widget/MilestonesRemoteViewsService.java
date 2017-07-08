package com.intelliviz.retirementhelper.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Service for providing the Views factory.
 * Created by Ed Muhlestein on 6/12/2017.
 */
public class MilestonesRemoteViewsService extends RemoteViewsService {
    public MilestonesRemoteViewsService() {
    }
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MilestonesRemoteViewsFactory(this);
    }
}
