package com.example.chenrong.paintdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by chenrong on 2016/10/5.
 */
public class MyButtonAdapter extends RecyclerView.Adapter<MyButtonAdapter.MyViewHolder> {

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

    private Integer[] res = {
        R.drawable.pen,
        R.drawable.eraser,
        R.drawable.size,
        R.drawable.color,
        R.drawable.triangle,
        R.drawable.rectangle,
        R.drawable.circle,
        R.drawable.undo,
        R.drawable.redo,
        R.drawable.cleanl,
        R.drawable.save,
    };
    private LayoutInflater inflater;
    private Context context;

    public MyButtonAdapter(Context context) {
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public MyButtonAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=inflater.inflate(R.layout.mybuttonadapter_item,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        viewHolder.imageView = (ImageView) itemView.findViewById(R.id.iv_mybuttonadapter);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyButtonAdapter.MyViewHolder holder, final int position) {
        int resource =res[position];
        holder.imageView.setImageResource(resource);

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
        return res == null ? 0 : res.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public MyViewHolder(View arg0)
        {
            super(arg0);
        }
        ImageView imageView;
    }
}
