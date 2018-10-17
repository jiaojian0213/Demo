package com.example.jiao.demo.ui;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
public class ChildPhotoImageAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mLayoutInflater;
    private List<PhotoModel> imageList;
    private final static String TAG = "ImageAdapter";

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private boolean showCamera = false;
    private boolean showSelectIndicator = true;

    private List<PhotoModel> selectedImageList = new ArrayList<>();

    private int mItemSize;
    private GridView.LayoutParams mItemLayoutParams;

    public ChildPhotoImageAdapter(Context context, List<PhotoModel> imageList) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.imageList = imageList;
        mItemLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
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
        mItemLayoutParams = new GridView.LayoutParams(mItemSize, (int)(0.6 *mItemSize));
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

//    @Override
//    public int getItemViewType(int position) {
//        if (showCamera && position == 0) {
//            return TYPE_CAMERA;
//        }
//        return TYPE_NORMAL;
//    }

    @Override
    public int getCount() {
        return showCamera ? imageList.size() + 1 : imageList.size();
    }

    @Override
    public PhotoModel getItem(int position) {
        if (showCamera) {
            if (position == 0) {
                return null;
            }
            return imageList.get(position - 1);
        } else {
            return imageList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);

        if (type == TYPE_CAMERA) {
            convertView = mLayoutInflater.inflate(R.layout.imageselector_item_camera, parent, false);
            convertView.setTag(null);
        } else if (type == TYPE_NORMAL) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.imageselector_item_image, parent, false);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag(R.layout.imageselector_item_image);
                if (holder == null) {
                    convertView = mLayoutInflater.inflate(R.layout.imageselector_item_image, parent, false);
                    holder = new ViewHolder(convertView);
                }
            }

            if (showSelectIndicator) {
                holder.photo_check.setVisibility(View.VISIBLE);
                if (selectedImageList.contains(getItem(position))) {
//                    holder.photo_check.setImageResource(R.mipmap.imageselector_select_checked);
                    holder.photo_mask.setVisibility(View.VISIBLE);
                } else {
//                    holder.photo_check.setImageResource(R.mipmap.imageselector_select_uncheck);
                    holder.photo_mask.setVisibility(View.GONE);
                }
            } else {
                holder.photo_check.setVisibility(View.GONE);
            }

            if (mItemSize > 0) {
                final ViewHolder fholder = holder;
                fholder.photo_image.setBackgroundColor(Color.WHITE);
                String path = getItem(position).getPath();
                Uri imageUri = Uri.parse("http://pic38.nipic.com/20140225/3554136_195849520358_2.jpg");
//                if(!TextUtils.isEmpty(path)) {
//                    convertView.setTag(path);
//                    if (path.startsWith("http")) {
//                        imageUri = Uri.parse(Utils.genThumbnailUrl(path));
//                    }else if(path.startsWith("file")){
//                        imageUri = Uri.parse(path);
//                    }else {
//                        imageUri = Uri.parse("file://"+path);
//                    }
//                }else{
//                    String url = getItem(position).getUrl();
//                    convertView.setTag(url);
//                    imageUri = Uri.parse(Utils.genThumbnailUrl(url));
//                }


                if(imageUri != null) {
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setUri(imageUri)
                            .setCallerContext("ZoomableApp-MyPagerAdapter")
                            .build();
                    fholder.photo_image.setController(controller);
                }
            }
        }

        GridView.LayoutParams layoutParams = (GridView.LayoutParams) convertView.getLayoutParams();
        if (layoutParams.height != mItemSize) {
            convertView.setLayoutParams(mItemLayoutParams);
        }

        return convertView;
    }

    public void setData(List data) {
        this.imageList = data;
        notifyDataSetChanged();
    }

    public String deleteItem(int position) {
        if(position >0)
            position--;
        PhotoModel image = imageList.remove(position);
        notifyDataSetChanged();
        return image.getPath();
    }

    public void addData(List<PhotoModel> landPhotos) {
        if(imageList != null){
            ArrayList<PhotoModel> objects = new ArrayList<>();
            objects.addAll(imageList);
            objects.addAll(landPhotos);
            imageList = objects;
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        if(imageList != null)
        imageList.clear();
        notifyDataSetChanged();
    }

    class ViewHolder {
        SimpleDraweeView photo_image;
        View photo_mask;
        ImageView photo_check;

        ViewHolder(View itemView) {
            photo_image = (SimpleDraweeView) itemView.findViewById(R.id.photo_image);
            photo_mask = itemView.findViewById(R.id.photo_mask);
            photo_check = (ImageView) itemView.findViewById(R.id.photo_check);
            itemView.setTag(R.layout.imageselector_item_image,this);
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

}