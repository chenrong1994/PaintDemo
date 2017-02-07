package com.example.chenrong.paintdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class MyColorsAdapter extends RecyclerView.Adapter<MyColorsAdapter.MyViewHolder> {

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    /**
     * 颜色数组
     */
    public static Integer[] colors = {
            R.color.a,
            R.color.g,
            R.color.h,
            R.color.j,
            R.color.m,
            R.color.l,
            R.color.r,
            R.color.f,
            R.color.d,
            R.color.n,
            R.color.s,
            R.color.t,
            R.color.b,
            R.color.i,
            R.color.u,
            R.color.o,
            R.color.p,
            R.color.e,
            R.color.k,
            R.color.c,
    };
    private LayoutInflater inflater;
    private Context context;

    public MyColorsAdapter(Context context) {
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public MyColorsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=inflater.inflate(R.layout.mycolorsadapter_item,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        viewHolder.imageView = (ImageView) itemView.findViewById(R.id.adapter_iv);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyColorsAdapter.MyViewHolder holder, final int position) {
        int color = context.getResources().getColor(colors[position]);
        holder.imageView.setBackgroundColor(color);

        //如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return colors == null ? 0 : colors.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public MyViewHolder(View arg0)
        {
            super(arg0);
        }
        ImageView imageView;
    }
}
