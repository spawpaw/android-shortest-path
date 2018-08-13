package com.spawpaw.shortestpath;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CDFAE1CC on 2016.12.13.
 */
public class MyImageView extends ImageView {
    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private List<Edge> path = new ArrayList<>();
    private List<Edge> validPath = new ArrayList<>();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (path == null || validPath == null || path.size() == 0 || validPath.size() == 0) return;

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL_AND_STROKE);

        //画所有路径
        p.setColor(Color.LTGRAY);
        p.setStrokeWidth(10);
        p.setAlpha(200);
        for (Edge e : path) {
            canvas.drawLine(e.v1.x, e.v1.y, e.v2.x, e.v2.y, p);
        }
        //画通路
        p.setColor(Color.GREEN);
        p.setStrokeWidth(10);
        p.setAlpha(170);
        for (Edge e : validPath) {
            canvas.drawLine(e.v1.x, e.v1.y, e.v2.x, e.v2.y, p);
        }
        //画权值文字
        p.setColor(Color.WHITE);
        p.setStrokeWidth(1);
        p.setTextSize(30);
        p.setAlpha(230);
        for (Edge e : path) {
            canvas.drawText("" + (int) e.weight, (e.v1.x + e.v2.x) / 2, (e.v1.y + e.v2.y) / 2, p);
        }
//        for (Line aLine : edgesList) {
//            int weight = (int) Math.sqrt(Math.pow(aLine.x1 - aLine.x2, 2) + Math.pow(aLine.y1 - aLine.y2, 2));
//            canvas.drawText("" + weight, (aLine.x1 + aLine.x2) / 2, (aLine.y1 + aLine.y2) / 2, p);
//        }
    }

    void setPath(List<Edge> path) {
        this.path = path;
        this.invalidate();
    }

    void setValidPath(List<Edge> validPath) {
        this.validPath = validPath;
        this.invalidate();
    }
}
