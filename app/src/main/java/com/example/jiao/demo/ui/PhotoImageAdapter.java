package com.example.jiao.demo.ui;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.jiao.demo.R;
import com.example.jiao.demo.daomodel.PhotoModel;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Image Adapter
 * Created by Yancy on 2015/12/2.
 */
public class PhotoImageAdapter extends RecyclerView.Adapter<PhotoImageAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater mLayoutInflater;
    private List<PhotoModel> imageList;
    private final static String TAG = "ImageAdapter";

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private boolean showCamera = true;
    private boolean showSelectIndicator = true;

    private List<PhotoModel> selectedImageList = new ArrayList<>();

    private int mItemSize;
    private GridView.LayoutParams mItemLayoutParams;
    private OnItemClickListener listener;

    public PhotoImageAdapter(Context context, List<PhotoModel> imageList) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.imageList = imageList;
        mItemLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
    }

    public void setData( List<PhotoModel> imageList){
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    public void addData(PhotoModel photoModel){
        if(this.imageList == null)
            this.imageList = new ArrayList<>();
        this.imageList.add(photoModel);
        notifyDataSetChanged();
    }

    public void setDefaultSelected(ArrayList<String> resultList) {
        for (String filePath : resultList) {
            PhotoModel image = getImageByPath(filePath);
            if (image != null) {
                selectedImageList.add(image);
            }
        }
        if (selectedImageList.size() > 0) {
            notifyDataSetChanged();
        }
    }

    private PhotoModel getImageByPath(String filePath) {
        if (imageList != null && imageList.size() > 0) {
            for (PhotoModel image : imageList) {
                if (image.getPath().equalsIgnoreCase(filePath)) {
                    return image;
                }
            }
        }
        return null;
    }

    public void setItemSize(int columnWidth,int type) {
        if (mItemSize == columnWidth) {
            return;
        }
        mItemSize = columnWidth;
//        mItemLayoutParams = new GridView.LayoutParams(mItemSize, (int)(0.6 *mItemSize));
        mItemSize =mItemSize;
        mItemLayoutParams = new GridView.LayoutParams(mItemSize, (int)(mItemSize));
        notifyDataSetChanged();
    }
    public void setItemSize(int columnWidth) {
        if (mItemSize == columnWidth) {
            return;
        }
        mItemSize = columnWidth;
        mItemLayoutParams = new GridView.LayoutParams(mItemSize,mItemSize);
        notifyDataSetChanged();
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return showCamera ? imageList.size() + 1 : imageList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerAdapter.ImageViewHolder{
        SimpleDraweeView photo_image;
        View photo_mask;
        ImageView photo_check;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            photo_image = (SimpleDraweeView) itemView.findViewById(R.id.photo_image);
            photo_mask = itemView.findViewById(R.id.photo_mask);
            photo_check = (ImageView) itemView.findViewById(R.id.photo_check);
            itemView.setTag(R.layout.imageselector_item_image,this);
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CAMERA) {
            View view = mLayoutInflater.inflate(R.layout.imageselector_item_camera, parent, false);
            return new ViewHolder(view);
        }
        View view  = mLayoutInflater.inflate(R.layout.imageselector_item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) holder.itemView.getLayoutParams();
        if(layoutParams == null)
            layoutParams = new ViewGroup.LayoutParams(mItemSize,mItemSize);

        if (layoutParams.height != mItemSize) {
            holder.itemView.setLayoutParams(mItemLayoutParams);
        }
        if (viewType == TYPE_CAMERA) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        listener.onCameraClick(view);
                    }
                }
            });
        }else if(viewType == TYPE_NORMAL) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        PhotoModel photoModel = imageList.get(showCamera ? position - 1 : position);
                        listener.onImageClick(view,position,photoModel);
                    }
                }
            });
        }

        if(holder.photo_image == null)
            return;
        if (mItemSize > 0) {
            final ViewHolder fholder = holder;
            fholder.photo_image.setBackgroundColor(Color.WHITE);
//            String path = getItem(position).getPath();
            Uri imageUri = Uri.parse("http://pic38.nipic.com/20140225/3554136_195849520358_2.jpg");

            if(imageUri != null) {
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(imageUri)
                        .setCallerContext("ZoomableApp-MyPagerAdapter")
                        .build();
                fholder.photo_image.setController(controller);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return TYPE_CAMERA;
        }
        return TYPE_NORMAL;
    }

    public void setShowSelectIndicator(boolean showSelectIndicator) {
        this.showSelectIndicator = showSelectIndicator;
    }

    public void setShowCamera(boolean showCamera) {
        if (this.showCamera == showCamera)
            return;
        this.showCamera = showCamera;
        notifyDataSetChanged();
    }

    public void select(PhotoModel image) {
        if (selectedImageList.contains(image)) {
            selectedImageList.remove(image);
        } else {
            selectedImageList.add(image);
        }
        notifyDataSetChanged();
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public interface OnItemClickListener{

        void onCameraClick(View view);
        void onImageClick(View view,int position,PhotoModel photoModel);
        void onDeleteClick(View view,int position,PhotoModel photoModel);

    }
}