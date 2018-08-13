package com.spawpaw.android.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by BenBenShang on 2016.12.30.using computer CDFAE1CC
 */
public abstract class ActivityOnCreate extends ActivityRoot implements IActivityOnCreate {

    /**
     * 当前Activity渲染的视图View
     **/
    private View mContextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "BaseActivity-->onCreate()");
        Bundle bundle = getIntent().getExtras();
        initParms(bundle);
        if (null == mContextView)
            mContextView = LayoutInflater.from(this).inflate(bindLayout(), null);

        initView(mContextView);
        doBusiness(this);
    }


    public void initParms(Bundle parms) {

    }




    public View getView(View v, int id) {
        v.findViewById(id);
        return v;
    }

}
