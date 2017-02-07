package com.example.chenrong.paintdemo;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MyPaintView img;

    private RecyclerView rv_btn;

    private MyButtonAdapter myButtonAdapter; // 底部按钮适配器

    private PopupWindow popupWindow;

    /**
     * popupWindow的自定义视图
     */
    private View colorsContentView;

    private View sizeContentView;

    /**
     * popupWindow的宽
     */
    private int sW;

    /**
     * popupWindow的高（根据分辨率计算60dp转px）
     */
    private int sH;

    /**
     * 分辨率
     */
    private float scale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //获取屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        scale = this.getResources().getDisplayMetrics().density;
        //计算popupWindow的宽高
        sW = dm.widthPixels;
        sH = (int) (60 * scale + 0.5f);

        initView();

        setListener();
    }

    private void initView() {
        img = (MyPaintView) findViewById(R.id.img);
        rv_btn = (RecyclerView) findViewById(R.id.rv_button);
        //设置横向滑动
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_btn.setLayoutManager(linearLayoutManager);
        //初始化适配器
        myButtonAdapter = new MyButtonAdapter(getApplicationContext());
        rv_btn.setAdapter(myButtonAdapter);
    }

    private void setListener() {
        myButtonAdapter.setOnItemClickListener(new MyButtonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0: // 画笔
                        img.setPaint_shape(MyPaintView.PAINT_SHAPE.LINE_SHAPE);
                        break;
                    case 1: // 橡皮擦
                        img.eraser();
                        break;
                    case 2: // 画笔粗细
                        if (sizeContentView == null) {
                            sizeContentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.sizeview,null);
                            int progress = img.getPaint_size();
                            final TextView textView = (TextView) sizeContentView.findViewById(R.id.tv_sizeview);
                            textView.setText("当前画笔大小为：" + String.valueOf(progress));
                            SeekBar seekBar = (SeekBar) sizeContentView.findViewById(R.id.sb_sizeview);
                            seekBar.setProgress(progress);
                            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                    textView.setText("当前画笔大小为：" + String.valueOf(i));
                                    img.setPaint_size(i);
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });
                        }
                        showPopupWindow(rv_btn,sizeContentView);
                        break;
                    case 3: // 颜色
                        if (colorsContentView == null) {
                            colorsContentView = LayoutInflater.from(MainActivity.this).inflate(
                                    R.layout.colorrecyclerview, null);
                            RecyclerView recyclerView = (RecyclerView) colorsContentView.findViewById(R.id.colorrecyclerview);
                            //设置横向滑动
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            //初始化适配器
                            MyColorsAdapter myColorsAdapter = new MyColorsAdapter(getApplicationContext());
                            //item的点击事件（自定义接口回调）
                            myColorsAdapter.setOnItemClickListener(new MyColorsAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    int color = MainActivity.this.getResources().getColor(MyColorsAdapter.colors[position]);
                                    img.setPaint_color(color);
                                    //收起popupWindow
                                    popupWindow.dismiss();
                                }
                            });
                            recyclerView.setAdapter(myColorsAdapter);
                        }
                        showPopupWindow(rv_btn,colorsContentView);
                        break;
                    case 4: // 三角形
                        img.setPaint_shape(MyPaintView.PAINT_SHAPE.TRIAN_SHAPE);
                        break;
                    case 5: // 矩形
                        img.setPaint_shape(MyPaintView.PAINT_SHAPE.RECT_SHAPE);
                        break;
                    case 6: // 圆形
                        img.setPaint_shape(MyPaintView.PAINT_SHAPE.CIRCCLE_SHAPE);
                        break;
                    case 7: // 撤销
                        img.undo();
                        break;
                    case 8: // 恢复
                        img.redo();
                        break;
                    case 9: // 清空
                        img.resetView();
                        break;
                    case 10: // 保存
                        img.save();
                        break;
                }
            }
        });
    }

    /**
     * 弹出框
     * @param v 控件
     * @param contentView 视图
     */
    private void showPopupWindow(View v,View contentView) {
        popupWindow = new PopupWindow(contentView, sW, sH);
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //设置动画
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        //在控件上方显示popupWindow
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWindow.getWidth() / 2, location[1] - popupWindow.getHeight());
    }
}
