package com.example.jiao.demo.ui;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.jiao.demo.Constants;
import com.example.jiao.demo.R;
import com.example.jiao.demo.daomodel.PhotoModel;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder>{
 
    Context context;
    List<PhotoModel> listbig;
    //定义宽度
    private int itemWidth;
 
    public RecyclerAdapter(Context context, List<PhotoModel> listbig) {
        this.listbig = listbig;
        this.context = context;
 
        //设置宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
 
        int width = wm.getDefaultDisplay().getWidth();
        itemWidth = width / 3;//定义固定的宽度
    }
 
    @Override
    //创建viewholder
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建一个view
      View view = View.inflate(context, R.layout.imageselector_item_image,null);
 
        //newviewholder将引入的布局视图传进去
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);
        return imageViewHolder;
    }
 
    @Override
    //绑定view 显示数据
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        //设置高
        ViewGroup.LayoutParams params = holder.itemimageview.getLayoutParams();

        final int desireSize = context.getResources().getDimensionPixelOffset(R.dimen.image_size);
        final int numCount = 3;//photo_gridView.getWidth() / desireSize;
        final int columnSpace = context.getResources().getDimensionPixelOffset(R.dimen.space_size);

        //图片宽度
        int columnWidth = ((Constants.width - columnSpace * (numCount - 1)) / numCount) -10;

//        //初始高度300
//        int itemHeight = 200;
//
//        itemHeight = new Random().nextInt(500);
//        if(itemHeight < 300){
//            itemHeight = 300;
//        }
        if(params == null)
            params = new ViewGroup.LayoutParams(columnWidth,columnWidth);
//        给imageview设置宽高
        params.width = columnWidth;
        params.height = columnWidth;
        holder.itemimageview.setLayoutParams(params);

        Uri imageUri = Uri.parse("http://pic38.nipic.com/20140225/3554136_195849520358_2.jpg");
        if(imageUri != null) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(imageUri)
                    .setCallerContext("ZoomableApp-MyPagerAdapter")
                    .build();
            holder.photo_image.setController(controller);
        }
        //显示图片
//        ImageLoader.getInstance().displayImage(listbig.get(position),holder.itemimageview);
//        //设置点击事件,
//        holder.itemimageview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dianjiCallBack.dianji(v,position);
//            }
//        });
    }
    @Override
    public int getItemCount() {
        return listbig.size();//集合的长度
    }
    //必须写的viewholder
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView photo_image;
        View photo_mask;
        ImageView photo_check;
        View itemimageview;

        public ImageViewHolder(View itemView) {
            super(itemView);
            itemimageview = itemView;
            photo_image = (SimpleDraweeView) itemView.findViewById(R.id.photo_image);
            photo_mask = itemView.findViewById(R.id.photo_mask);
            photo_check = (ImageView) itemView.findViewById(R.id.photo_check);
            itemView.setTag(R.layout.imageselector_item_image,this);
        }
    }
//    //接口的实现
//    DianjiCallBack dianjiCallBack;
//    public void setDianjiCallBack(DianjiCallBack dianjiCallBack){
//        this.dianjiCallBack = dianjiCallBack;
//    }
}

