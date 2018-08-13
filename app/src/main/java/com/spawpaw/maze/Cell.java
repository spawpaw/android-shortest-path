package com.spawpaw.maze;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by CDFAE1CC on 2016.12.15.
 * <p>
 * 一个迷宫格子
 */
public class Cell extends TextView {


    CellState state = CellState.FREE;


    void init(int x, int y, int cellSize, CellState state) {
        this.setX(x);
        this.setY(y);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();
        params.width = cellSize;
        params.height = cellSize;
        this.setLayoutParams(params);
        setColor(state);
    }

    void setColor(CellState state) {
        this.state = state;
        switch (state) {
            case FREE:
                this.setBackgroundColor(Color.BLUE);
                break;
            case OBSTACLE:
                this.setBackgroundColor(Color.RED);
                break;
        }

    }

    public Cell(Context context) {
        super(context);
    }

    public Cell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Cell(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Cell(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
