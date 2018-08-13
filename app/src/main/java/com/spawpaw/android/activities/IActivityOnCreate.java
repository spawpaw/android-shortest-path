package com.spawpaw.android.activities;

import android.content.Context;
import android.view.View;

/**
 * Created by BenBenShang on 2016.12.31.using computer CDFAE1CC
 */
public interface IActivityOnCreate {

    int bindLayout();


    void initView(final View view);


    void doBusiness(Context mContext);
}
