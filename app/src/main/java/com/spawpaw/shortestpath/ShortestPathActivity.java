package com.spawpaw.shortestpath;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.anupcowkur.reservoir.Reservoir;
import com.google.gson.reflect.TypeToken;
import com.spawpaw.android.base.BaseDefaultAppCompatActivity;
import com.spawpaw.toolset.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ShortestPathActivity extends BaseDefaultAppCompatActivity {


    Context mContext = this;
    private MyImageView iv_map;
    private SearchShortestPathUtil searchShortestPathUtil = new SearchShortestPathUtil();
    private TextView tv_statues;
    private TextView tv_log;
    private Button btn_mark_vertex;
    private Button btn_connect_vertex;
    private Button btn_search_shortest_path;
    private Button btn_load_data;
    private FrameLayout ffTvLayer;//顶点 TextView所在的图层
//    private View mControlsView;

    CheckBox cb_save;
    Type typeToken;

    @Override
    public int bindLayout() {
        return R.layout.activity_shortest_path;
    }

    @Override
    public void initView(View view) {
//        mControlsView = findViewById(R.id.fullscreen_content_controls);
        iv_map = (MyImageView) findViewById(R.id.iv_map);
        tv_statues = (TextView) findViewById(R.id.tv_statues);
        tv_log = (TextView) findViewById(R.id.tv_log);
        btn_mark_vertex = (Button) findViewById(R.id.btn_mark_vertex);
        btn_connect_vertex = (Button) findViewById(R.id.btn_connect_vertex);
        btn_search_shortest_path = (Button) findViewById(R.id.btn_search_shortest_path);
        btn_load_data = (Button) findViewById(R.id.btn_load_data);
        ffTvLayer = (FrameLayout) findViewById(R.id.fl_content);
        cb_save = (CheckBox) findViewById(R.id.cb_save_g);
    }

    @Override
    public void doBusiness(Context mContext) {
        // Hide UI first
        hideUI();

        try {
            Reservoir.init(this, 20480);
            typeToken = new TypeToken<String>() {
            }.getType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        btn_load_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchShortestPathUtil.loadDataFromLocalStorage();
            }
        });
        btn_mark_vertex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currState = State.MARKING_VERTEX;
            }
        });
        btn_connect_vertex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchShortestPathUtil.prepareForConnectVertex();
            }
        });
        btn_search_shortest_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchShortestPathUtil.prepareForSearch();
            }
        });
        cb_save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) searchShortestPathUtil.saveDataToLocalStorage();
            }
        });
        Log.i("init conpleted", "--------------------------------------------------------------------------------");
        iv_map.setOnTouchListener(new OnTouchPainter());
        searchShortestPathUtil.init();
    }

    private State currState = State.WAITTING_FOR_COMMAND;

    private class SearchShortestPathUtil {

        //当前Vertex集合
        List<Vertex> vertexList = new ArrayListSerializable();
        //用于绘图的Edge集合
        List<Edge> edgesToDraw = new ArrayListSerializable<>();
        //临时路径
        List<Edge> validPath = new ArrayListSerializable<>();

        class Path {
            Path(Double weight, List<Edge> path) {
                this.weightSum = weight;
                this.path = path;
            }

            Double weightSum = Double.MAX_VALUE;
            List<Edge> path;
        }

        //最短通路的集合
        List<Path> validPathList = new ArrayListSerializable<>();
        //搜索的起始点和终点
        int from = -1;
        int to = -1;

        //用于本地存储的数组

        //初始化所有数据
        SearchShortestPathUtil() {
        }

        void init() {
            // TODO: 2016.12.15 移除所有textview
            //第0位的是初始的imageView，不删
            ffTvLayer.removeViewsInLayout(1, ffTvLayer.getChildCount() - 1);

            currState = State.WAITTING_FOR_COMMAND;
            vertexList = new ArrayListSerializable<>();
            edgesToDraw = new ArrayListSerializable<>();
            validPath = new ArrayListSerializable<>();
            validPathList = new ArrayListSerializable<>();
            iv_map.setPath(edgesToDraw);
            iv_map.setValidPath(validPath);
        }

        //从本地加载数据
        void loadDataFromLocalStorage() {
            init();
            try {
//                displayLongToast(vertexsToSave.toString());
//                displayLongToast(Reservoir.get("vertexes", typeToken).toString());
                JSONArray ja = new JSONArray(Reservoir.get("vertexes", typeToken).toString());
                float ys[] = new float[ja.length()];
                float xs[] = new float[ja.length()];
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    xs[jo.getInt("index")] = (float) jo.getDouble("x");
                    ys[jo.getInt("index")] = (float) jo.getDouble("y");
                }
                for (int i = 0; i < ja.length(); i++) {
                    addVertex(xs[i], ys[i]);
                }
                ja = new JSONArray(Reservoir.get("edges", typeToken).toString());
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    connectVertex(jo.getInt("u"), jo.getInt("v"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            iv_map.setPath(edgesToDraw);
        }

        JSONArray vertexsToSave = new JSONArray();
        JSONArray edgesToSave = new JSONArray();

        //保存数据到本地
        void saveDataToLocalStorage() {
            try {
                Reservoir.put("vertexes", vertexsToSave.toString());
                Reservoir.put("edges", edgesToSave.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void saveVertex(int index, float x, float y) {
            JSONObject jo;
            try {
                jo = new JSONObject();
                jo.put("index", index);
                jo.put("x", x);
                jo.put("y", y);
                vertexsToSave.put(jo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void saveEdge(int u, int v) {
            JSONObject jo;
            try {
                jo = new JSONObject();
                jo.put("u", u);
                jo.put("v", v);
                edgesToSave.put(jo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //添加顶点
        void addVertex(float x, float y) {
            saveVertex(searchShortestPathUtil.vertexList.size(), x, y);
            MyTextView textView = new MyTextView(mContext);
            textView.setText(String.valueOf(searchShortestPathUtil.vertexList.size()));
            ffTvLayer.addView(textView);

            textView.setPadding(32, 8, 32, 8);
            //计算宽高
            int width, height;
            width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            textView.measure(width, height);
            width = textView.getMeasuredWidth();
            height = textView.getMeasuredHeight();

            //设置位置
            textView.setX(x - width / 2);
            textView.setY(y - height / 2);
            //设置背景及监听
            textView.setBackgroundResource(R.drawable.tv_bg);
            textView.setOnClickListener(new OnClicVertex(searchShortestPathUtil.vertexList.size()));
            //设置宽高
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) textView.getLayoutParams();
            params.width = width;
            params.height = height;
            textView.setLayoutParams(params);
            vertexList.add(new Vertex(x, y/*, textView*/));
        }

        //连接顶点
        void connectVertex(int u, int v) {
            saveEdge(u, v);
            Edge e = new Edge(vertexList.get(u), vertexList.get(v));
            vertexList.get(u).addNext(v, e.weight);
            vertexList.get(v).addNext(u, e.weight);
            edgesToDraw.add(e);
            iv_map.setPath(edgesToDraw);
        }

        void prepareForConnectVertex() {
            currState = State.CONNECTING_VERTEX;

            tmp = -1;
        }

        void prepareForSearch() {
            validPath = new ArrayListSerializable<>();
            validPathList = new ArrayListSerializable<>();
            iv_map.setValidPath(validPath);
            currState = State.CHOOSING_VERTEX_TO_SEARCH;
            minWeightSum = Double.MAX_VALUE;
            count = 0;
            from = -1;
            to = -1;
        }

        void searchForShortestPath() {
            double s = System.currentTimeMillis();
            Double sum = dfs(from, to, 0d);
            double e = System.currentTimeMillis();
            addLog(String.format("最短路径:%s,\n耗时: %s ms;%d 次DFS", sum, e - s, count));
            //找出最短路径并绘制
            Double min = Double.MAX_VALUE;

            for (Path path : validPathList) {
                if (path.weightSum < min) min = path.weightSum;
            }
            for (Path path : validPathList) {
                if (path.weightSum == min) iv_map.setValidPath(path.path);
            }
        }


        Double minWeightSum = Double.MAX_VALUE;
        Integer count = 0;

        Double dfs(int from, int to, Double weightSum) {
            count++;
            //结束条件
            if (from == to || weightSum > minWeightSum || vertexList.get(from).isOccupied) {
                if (from == to && minWeightSum > weightSum) {
                    //将当前路径添加到最短通路
                    minWeightSum = weightSum;
                    List<Edge> tmp = new ArrayListSerializable<>();
                    tmp.addAll(validPath);
                    validPathList.add(new Path(weightSum, tmp));
                }
                return minWeightSum;
            }
            //开始计算当前顶点
            vertexList.get(from).isOccupied = true;//
            Vertex.NeighbourVertex e = vertexList.get(from).next;
            while (e != null) {
                //添加edge记录
                validPath.add(new Edge(vertexList.get(from), vertexList.get(e.nextIndex)));
                dfs(e.nextIndex, to, weightSum + e.weight);
                //回退edge记录
                validPath.remove(validPath.size() - 1);
                e = e.next;
            }
            //结束，还原当前顶点状态
            vertexList.get(from).isOccupied = false;
            return minWeightSum;

        }
    }

    private class OnTouchPainter implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.i("touch iv", "---------------------------------------------------------------------------------");
            if (currState == State.MARKING_VERTEX)
                searchShortestPathUtil.addVertex(motionEvent.getX(), motionEvent.getY());
            return false;
        }
    }

    private int tmp = -1;

    private class OnClicVertex implements View.OnClickListener, Serializable {
        int index;

        OnClicVertex(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View view) {
            if (currState == State.CONNECTING_VERTEX) {
                if (tmp < 0) tmp = this.index;
                else {
                    searchShortestPathUtil.connectVertex(tmp, index);
                    tmp = -1;
                }

            }
            if (currState == State.CHOOSING_VERTEX_TO_SEARCH) {
                if (searchShortestPathUtil.from < 0)
                    searchShortestPathUtil.from = this.index;
                else {
                    searchShortestPathUtil.to = this.index;
                    searchShortestPathUtil.searchForShortestPath();
                    currState = State.WAITTING_FOR_COMMAND;
                }
            }

        }
    }

    private void addLog(String str) {
        tv_statues.setText(str);
        tv_log.setText(String.format("%s\n%s", tv_log.getText(), str));
    }

    private void saveObject(String fName, Object obj) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = mContext.openFileOutput(fName, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
            //这里是保存文件产生异常
        } finally {
            try {
                if (fos != null)
                    fos.close();
                if (oos != null)
                    oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Object readObject(String fName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = this.openFileInput(fName);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            //这里是读取文件产生异常
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (ois != null)
                    ois.close();
            } catch (IOException e) {
                //ois流关闭异常
                e.printStackTrace();
            }
        }
        //读取产生异常，返回null
        return null;
    }

    private void hideUI() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        findViewById(R.id.fullscreen_content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        mControlsView.setVisibility(View.GONE);
    }


}
