package com.spawpaw.maze;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.spawpaw.android.base.BaseDefaultAppCompatActivity;
import com.spawpaw.util.Enums;
import com.spawpaw.toolset.R;

import java.util.ArrayList;
import java.util.List;

public class MazeActivity extends BaseDefaultAppCompatActivity {
    private MazeUtil mazeUtil = new MazeUtil();
    private FrameLayout fl_content;
    private EditText et_maze_width;
    private EditText et_maze_height;
    Button btn_set_free;
    Button btn_set_obstacle;
    Button btn_generate_maze;
    Button btn_find_way_out;
    CheckBox cb_isRandom;
    TextView tv_fill;
    TextView tv_statues;
    private Cell mazeMap[][];
    private int mazeWidth = 100;//迷宫的宽高(横向纵向元素的数量)
    private int mazeHeight = 100;
    private int flWidth = 0;//迷宫容器的宽高
    private int flHeight = 0;
    private int cellSize = 0;//迷宫单元格大小
    private int marginTop = 0;//上边距
    private int marginLeft = 0;//下边距
    private boolean isRandom = false;
    State currState = State.WATTING_FOR_COMMAD;//当前操作

    private Context mContext = this;

    @Override
    public int bindLayout() {
        return R.layout.activity_maze;
    }

    @Override
    public void initView(View view) {
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        tv_fill = (TextView) findViewById(R.id.tv_fill);
        et_maze_width = (EditText) findViewById(R.id.et_maze_width);
        et_maze_height = (EditText) findViewById(R.id.et_maze_height);
        cb_isRandom = (CheckBox) findViewById(R.id.cb_is_random);
        btn_generate_maze = (Button) findViewById(R.id.btn_generate_maze);
        btn_set_free = (Button) findViewById(R.id.btn_set_free);
        btn_set_obstacle = (Button) findViewById(R.id.btn_set_obstacle);
        btn_find_way_out = (Button) findViewById(R.id.btn_find_way_out);
        tv_statues = (TextView) findViewById(R.id.tv_statues);
    }

    @Override
    public void doBusiness(Context mContext) {
        hideUI();

        cb_isRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isRandom = b;
            }
        });
        btn_generate_maze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mazeUtil.init();
            }
        });
        btn_set_free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currState = State.SETTING_FREE;
            }
        });
        btn_set_obstacle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currState = State.SETTING_OBSTACLE;
            }
        });
        btn_find_way_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySnake("请选取起点");
                currState = State.FINDING_WAY_OUT;
            }
        });
        mazeUtil.init();
    }

    private void hideUI() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        findViewById(R.id.fl_main).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private int fromx = -1, fromy = -1, tox = -1, toy = -1;

    private class MazeUtil {
        void init() {
            // TODO: 2016.12.15 清空flconten内所有控件
            fl_content.removeViewsInLayout(1, fl_content.getChildCount() - 1);
            // TODO: 2016.12.15 初始化迷宫参数
            mazeWidth = Integer.valueOf(et_maze_width.getText().toString());
            mazeHeight = Integer.valueOf(et_maze_height.getText().toString());
            mazeMap = new Cell[mazeHeight][mazeWidth];
            // TODO: 2016.12.15 量取fl_content 的宽高
            WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
            int width, height;
            height = wm.getDefaultDisplay().getHeight();
            width = wm.getDefaultDisplay().getWidth();
            flWidth = height > width ? height * 3 / 4 : width * 3 / 4;
            flHeight = height > width ? width : height;
            //初始化单元格大小
            cellSize = (mazeWidth > mazeHeight) ? flWidth / mazeWidth : flHeight / mazeHeight;
            marginTop = (flHeight - cellSize * mazeHeight) / 2;
            marginLeft = (flWidth - cellSize * mazeWidth) / 2;
//            displayLongToast(String.format("fl:%d,%d;\n margin:%d,%d\n cell:%d", flWidth, flHeight, marginTop, marginLeft, cellSize));
            // TODO: 2016.12.15 按照数据初始化

            for (int i = 0; i < mazeHeight; i++)
                for (int j = 0; j < mazeWidth; j++) {
                    Cell cell = new Cell(mContext);
                    fl_content.addView(cell);
                    cell.init(
                            marginLeft + cellSize * j,
                            marginTop + cellSize * i,
                            cellSize,
                            CellState.FREE
                    );
                    cell.setOnClickListener(new TvClickListener(i, j));
                    mazeMap[i][j] = cell;
                }
            // TODO: 2016.12.16 随机设置迷宫
            if (isRandom)
                for (int i = 0; i < mazeWidth; i++)
                    for (int j = 0; j < mazeHeight; j++) {
                        mazeMap[j][i].setColor(Enums.random(CellState.class));
                    }
        }

        void clean() {
            access = new ArrayList<>();
            for (int i = 0; i < mazeWidth; i++)
                for (int j = 0; j < mazeHeight; j++) {
                    mazeMap[j][i].setColor(mazeMap[j][i].state);
                }
        }

        void drawAccess() {

            for (Cell cell : access) {
                cell.setBackgroundColor(Color.GREEN);
            }
        }

        List<Cell> path = new ArrayList<>();
        List<Cell> access = new ArrayList<>();

        void searchForShortestPath() {
            count = 0;
            minLenth = Integer.MAX_VALUE;
            int len = dfs(fromx, fromy, 0);
            if (len == Integer.MAX_VALUE)
                displaySnake("入口与出口之间没有通路");
            else
                displaySnake("最短路径长度为" + len + "\n" + count + "次DFS");
            drawAccess();
            fromx = fromy = tox = toy = -1;
        }

        Integer minLenth;
        Integer count;

        Integer dfs(int x, int y, int lenth) {
            count++;
            //退出条件
            if (x < 0 || x >= mazeWidth || y < 0 || y >= mazeHeight || lenth > minLenth || mazeMap[y][x].state == CellState.OBSTACLE)
                return minLenth;

            if (x == tox && y == toy) {
                minLenth = lenth;
                access = new ArrayList<>();
                access.addAll(path);
                return minLenth;
            }

            //标记该点已经走过
            mazeMap[y][x].state = CellState.OBSTACLE;
            path.add(mazeMap[y][x]);
            //若满足条件，则搜索八个方向
            //上
            dfs(x, y + 1, lenth + 1);
            //右上
//            dfs(x + 1, y + 1, lenth + 1);
            //右
            dfs(x + 1, y, lenth + 1);
            //右下
//            dfs(x + 1, y - 1, lenth + 1);
            //下
            dfs(x, y - 1, lenth + 1);
            //左下
//            dfs(x - 1, y - 1, lenth + 1);
            //左
            dfs(x - 1, y, lenth + 1);
            //左上
//            dfs(x - 1, y + 1, lenth + 1);


            //还原点的状态
            mazeMap[y][x].state = CellState.FREE;
            path.remove(path.size() - 1);
            return minLenth;
        }

    }


    private class TvClickListener implements View.OnClickListener {
        int j, i;

        TvClickListener(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public void onClick(View view) {
            switch (currState) {
                case SETTING_FREE:
                    mazeMap[i][j].setColor(CellState.FREE);
                    break;
                case SETTING_OBSTACLE:
                    mazeMap[i][j].setColor(CellState.OBSTACLE);
                    break;
                case FINDING_WAY_OUT:
                    if (fromx < 0) {
                        mazeUtil.clean();
                        displaySnake("请选取终点");
                        fromx = j;
                        fromy = i;
                        mazeMap[i][j].setBackgroundColor(Color.CYAN);
                    } else {

                        mazeMap[i][j].setBackgroundColor(Color.CYAN);
                        tox = j;
                        toy = i;
                        mazeUtil.searchForShortestPath();
                    }
                    break;
            }

        }
    }

    private void displaySnake(String str) {
        Snackbar.make(fl_content, str, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}
