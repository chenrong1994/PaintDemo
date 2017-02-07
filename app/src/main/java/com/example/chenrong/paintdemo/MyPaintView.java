package com.example.chenrong.paintdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by chenrong on 2016/10/7.
 */
public class MyPaintView extends View{
    private Context context;

    private Bitmap mBitmap;

    private Bitmap lastBitmap; // 用于预览图形

    private Canvas canvas;

    private Canvas lastCanvas; // 用于预览图形

    private Path mPath; // 路径的坐标

    private ArrayList<DrawPath> savePath; // 画过的路径list

    private ArrayList<DrawPath> deletePath; // 撤销的路径list

    private DrawPath dp; // 自定义路径的对象

    private Paint mBitmapPaint; // 显示旧的画布时用的画笔

    private Paint paint = new Paint();

    private int paint_size;

    private int paint_color;

    private float startX;

    private float startY;

    private float currentX;

    private float currentY;

    private float endX;

    private float endY;

    private int paint_shape; // 画笔类型(线或者其他形状)

    private boolean isPreview; // 是否预览（图形在移动过程预览）

    private int bitmapWidth = 0; // 画布的宽

    private int bitmapHeight = 0; // 画布的高

    public MyPaintView(Context context) {
        super(context);
        init();
        this.context = context;
        savePath = new ArrayList<>();
        deletePath = new ArrayList<>();
    }

    public MyPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        this.context = context;
        savePath = new ArrayList<>();
        deletePath = new ArrayList<>();

    }

    public MyPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        savePath = new ArrayList<>();
        deletePath = new ArrayList<>();
        mPath = new Path();
        init();
    }

    public int getPaint_size() {
        return paint_size;
    }

    public void setPaint_size(int paint_size) {
        this.paint_size = paint_size;
        paint.setStrokeWidth(paint_size);
    }

    public int getPaint_color() {
        return paint_color;
    }

    public void setPaint_color(int paint_color) {
        this.paint_color = paint_color;
        paint.setColor(paint_color);
        paint.setStrokeWidth(paint_size);
    }

    public int getPaint_shape() {
        return paint_shape;
    }

    public void setPaint_shape(int paint_shape) {
        this.paint_shape = paint_shape;
        paint.setColor(paint_color);
        paint.setStrokeWidth(paint_size);
    }


    /**
     * 绘制图片的方法
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isPreview){
            canvas.drawBitmap(lastBitmap, new Matrix(), paint);
            lastCanvas = null;
            lastBitmap.recycle();
            lastBitmap = null;
        } else {
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);     //显示旧的画布
            if (mPath != null && paint_shape == PAINT_SHAPE.LINE_SHAPE) {
                // 实时的显示

                canvas.drawPath(mPath, paint);

            }
        }
    }

    public void eraser() {
        this.paint_shape = PAINT_SHAPE.LINE_SHAPE;
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4 * paint_size);
    }

    /**
     * 撤销的核心思想就是将画布清空，
     * 将保存下来的Path路径最后一个移除掉，
     * 重新将路径画在画布上面。
     */
    public void undo(){

        if(savePath != null && savePath.size() > 0){
            //调用初始化画布函数以清空画布
            init();
            //将路径保存列表中的最后一个元素删除 ,并将其保存在路径删除列表中
            DrawPath drawPath = savePath.get(savePath.size() - 1);
            deletePath.add(drawPath);
            savePath.remove(savePath.size() - 1);

            //将路径保存列表中的路径重绘在画布上
            Iterator<DrawPath> item = savePath.iterator();      //重复保存
            while (item.hasNext()) {
                DrawPath dp = item.next();
                canvas.drawPath(dp.path, dp.paint);
            }
            invalidate();// 刷新
        }
    }
    /**
     * 恢复的核心思想就是将撤销的路径保存到另外一个列表里面(栈)，
     * 然后从redo的列表里面取出最顶端对象，
     * 画在画布上面即可
     */
    public void redo(){
        if(deletePath.size() > 0){
            //将删除的路径列表中的最后一个，也就是最顶端路径取出（栈）,并加入路径保存列表中
            DrawPath dp = deletePath.get(deletePath.size() - 1);
            savePath.add(dp);
            //将取出的路径重绘在画布上
            canvas.drawPath(dp.path, dp.paint);
            //将该路径从删除的路径列表中去除
            deletePath.remove(deletePath.size() - 1);
            invalidate();
        }
    }
    /**
     * 清空的主要思想就是初始化画布
     * 将保存路径的两个List清空
     * */
    public void resetView(){
        //调用初始化画布函数以清空画布
        init();
        invalidate();//刷新
        savePath.clear();
        deletePath.clear();
    }

    //保存绘制好的图片
    public void save() {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "我的涂鸦");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "dog" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        Toast.makeText(context, "已保存到本地图库", Toast.LENGTH_SHORT).show();
    }

    private void touch_start() {
        isPreview = false;
        mPath = new Path();
        dp = new DrawPath();
        dp.path = this.mPath;
        dp.paint = this.paint;
        mPath.reset();//清空path
        if (paint_shape == PAINT_SHAPE.LINE_SHAPE) {
            mPath.moveTo(startX, startY);
        }

    }
    private void touch_move() {
        if (paint_shape == PAINT_SHAPE.LINE_SHAPE) { // 线
            //画贝塞尔曲线
            mPath.quadTo(startX, startY, (currentX + startX) / 2, (currentY + startY) / 2); // 防抖
            startX = currentX;
            startY = currentY;
        } else if (paint_shape == PAINT_SHAPE.TRIAN_SHAPE) { //三角形
            isPreview = true;
            lastBitmap = Bitmap.createBitmap(mBitmap);
            lastCanvas = new Canvas(lastBitmap);
            Path lastPath = new Path();
            lastPath.moveTo(startX,startY);
            lastPath.lineTo(currentX,currentY);
            lastPath.lineTo(startX - (currentX - startX),currentY);
            lastPath.close();
            lastCanvas.drawPath(lastPath,paint);
            lastPath = null;
        } else if (paint_shape == PAINT_SHAPE.RECT_SHAPE) { // 矩形
            isPreview = true;
            lastBitmap = Bitmap.createBitmap(mBitmap);
            lastCanvas = new Canvas(lastBitmap);
            lastCanvas.drawRect(startX, startY, currentX, currentY, paint);
        } else if (paint_shape == PAINT_SHAPE.CIRCCLE_SHAPE) { // 圆形
            isPreview = true;
            lastBitmap = Bitmap.createBitmap(mBitmap);
            lastCanvas = new Canvas(lastBitmap);
            lastCanvas.drawCircle((startX + currentX) / 2, (startY + currentY) / 2,
                    (float) Math.sqrt((currentX - startX) * (currentX - startX) + (currentY - startY) * (currentY - startY)) / 2, paint);
        }
    }
    private void touch_up() {
        Log.w("+++++++++++","");
        isPreview = false;
        if (paint_shape == PAINT_SHAPE.LINE_SHAPE) { // 线
            mPath.lineTo(startX, startY);
            canvas.drawPath(mPath, paint);
            savePath.add(dp);
            mPath = null;
        } else if (paint_shape == PAINT_SHAPE.TRIAN_SHAPE) { // 三角形
            mPath.moveTo(startX,startY);
            mPath.lineTo(endX,endY);
            mPath.lineTo(startX - (endX - startX),endY);
            mPath.close();
            canvas.drawPath(mPath,paint);
            savePath.add(dp);
            mPath = null;
        } else if (paint_shape == PAINT_SHAPE.RECT_SHAPE) { // 矩形
            mPath.moveTo(startX, startY);
            mPath.lineTo(endX,startY);
            mPath.lineTo(endX,endY);
            mPath.lineTo(startX,endY);
            mPath.close();
            canvas.drawPath(mPath, paint);
            savePath.add(dp);
            mPath = null;
        } else if (paint_shape == PAINT_SHAPE.CIRCCLE_SHAPE) { // 圆形
            mPath.addCircle((startX + currentX) / 2, (startY + currentY) / 2,
                    (float) Math.sqrt((currentX - startX) * (currentX - startX) + (currentY - startY) * (currentY - startY)) / 2,
                    Path.Direction.CW);
            canvas.drawPath(mPath, paint);
            savePath.add(dp);
            mPath = null;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取手按下时的坐标
                startX = event.getX();
                startY = event.getY();
                touch_start();
                invalidate(); //清屏
                break;
            case MotionEvent.ACTION_MOVE:
                // 获取手移动后的坐标
                currentX = event.getX();
                currentY = event.getY();
                touch_move();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //获取手抬起后的坐标
                endX = event.getX();
                endY = event.getY();
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 初始化
     */
    private void init() {
        //默认画线，不需要预览图形
        isPreview = false;
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        //判断是否是第一次初始化控件
        if (bitmapWidth == 0 || bitmapHeight == 0) {
            // 设置圆形画笔，防止粗线条分叉
            paint.setStrokeCap(Paint.Cap.ROUND);
            // 抗锯齿
            paint.setAntiAlias(true);
            // 设置空心
            paint.setStyle(Paint.Style.STROKE);
            // 宽度5个像素
            setPaint_size(5);
            // 画笔颜色为黑色
            setPaint_color(Color.BLACK);
            //画笔画线
            paint_shape = PAINT_SHAPE.LINE_SHAPE;
            //初始化控件后创建充满的图片
            this.post(new Runnable() {
                @Override
                public void run() {
                    // 创建一张空白图片
                    bitmapWidth = getWidth();
                    bitmapHeight = getHeight();
                    mBitmap = Bitmap.createBitmap(bitmapWidth,bitmapHeight, Bitmap.Config.ARGB_8888);
                    // 创建一张画布
                    canvas = new Canvas(mBitmap);
                    // 画布背景为白色
                    canvas.drawColor(Color.WHITE);
                }
            });
        } else {
            // 创建一张空白图片
            mBitmap = Bitmap.createBitmap(bitmapWidth,bitmapHeight, Bitmap.Config.ARGB_8888);
            // 创建一张画布
            canvas = new Canvas(mBitmap);
            // 画布背景为白色
            canvas.drawColor(Color.WHITE);
        }

    }

    /**
     * 静态的形状类，定义各种形状的int值
     */
    public static class PAINT_SHAPE {

        public static int LINE_SHAPE = 0; // 线

        public static int TRIAN_SHAPE = 1; // 矩形

        public static int RECT_SHAPE = 2; // 矩形

        public static int CIRCCLE_SHAPE = 3; // 矩形

    }

    //路径对象
    private class DrawPath{
        Path path;
        Paint paint;
    }
}
