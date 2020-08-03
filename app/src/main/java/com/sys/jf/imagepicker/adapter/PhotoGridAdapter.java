package com.sys.jf.imagepicker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sys.jf.imagepicker.R;
import com.sys.jf.imagepicker.entity.Photo;
import com.sys.jf.imagepicker.entity.PhotoDirectory;
import com.sys.jf.imagepicker.event.OnItemCheckListener;
import com.sys.jf.imagepicker.event.OnPhotoClickListener;
import com.sys.jf.imagepicker.utils.AndroidLifecycleUtils;
import com.sys.jf.imagepicker.utils.FileUtils;
import com.sys.jf.imagepicker.utils.MediaStoreHelper;

/**
 * Created by donglua on 15/5/31.
 */
public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder> {

    private RequestManager glide;
    private Context context;

    private OnItemCheckListener onItemCheckListener = null;
    private OnPhotoClickListener onPhotoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;
    private OnLimitListener onLimitListener = null;
    private ArrayList<String> selectedGifList;

    public final static int ITEM_TYPE_CAMERA = 100;
    public final static int ITEM_TYPE_PHOTO = 101;
    private final static int COL_NUMBER_DEFAULT = 3;

    private boolean hasCamera = true;
    private boolean previewEnable = true;
    private boolean limitSize = false;

    private int imageSize;
    private int columnNumber = COL_NUMBER_DEFAULT;


    public PhotoGridAdapter(Context context, RequestManager requestManager, List<PhotoDirectory> photoDirectories) {
        this.context = context;
        this.photoDirectories = photoDirectories;
        this.glide = requestManager;
        setColumnNumber(context, columnNumber);
    }

    public PhotoGridAdapter(Context context, RequestManager requestManager, List<PhotoDirectory> photoDirectories, ArrayList<String> orginalPhotos, int colNum) {
        this(context, requestManager, photoDirectories);
        setColumnNumber(context, colNum);
        selectedPhotos = new ArrayList<>();
        if (orginalPhotos != null) selectedPhotos.addAll(orginalPhotos);
    }

    private void setColumnNumber(Context context, int columnNumber) {
        this.columnNumber = columnNumber;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        imageSize = widthPixels / columnNumber;
    }

    @Override
    public int getItemViewType(int position) {
        return (showCamera() && position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.__picker_item_photo, parent, false);
        final PhotoViewHolder holder = new PhotoViewHolder(itemView);
        if (viewType == ITEM_TYPE_CAMERA) {
            holder.vSelected.setVisibility(View.GONE);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);

            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onClick(view);
                    }
                }
            });
        }
        return holder;
    }


    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {

        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {

            List<Photo> photos = getCurrentPhotos();
            final Photo photo;

            if (showCamera()) {
                photo = photos.get(position - 1);
            } else {
                photo = photos.get(position);
            }

            boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.ivPhoto.getContext());

            if (canLoadImage) {
                if (photo.getPath().endsWith(".gif")) {
                    holder.flGif.setVisibility(View.VISIBLE);
                } else {
                    holder.flGif.setVisibility(View.GONE);
                }
                glide.load(new File(photo.getPath()))
                        .into(holder.ivPhoto);
            }

            final boolean isChecked = isSelected(photo);

            holder.vSelected.setSelected(isChecked);
            holder.ivPhoto.setSelected(isChecked);

            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPhotoClickListener != null) {
                        int pos = holder.getAdapterPosition();
                        if (previewEnable) {
                            onPhotoClickListener.onClick(view, pos, showCamera());
                        } else {
                            holder.vSelected.performClick();
                        }
                    }
                }
            });
            holder.vSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
                    boolean isEnable = true;
                    if (limitSize && !isSelected(photo) && !FileUtils.isSizeFit(photo.getPath())) {
                        if (onLimitListener != null) {
                            onLimitListener.onLimit();
                        }
                        return;
                    }
                    if (onItemCheckListener != null) {
                        isEnable = onItemCheckListener.onItemCheck(pos, photo,
                                getSelectedPhotos().size() + (isSelected(photo) ? -1 : 1));
                    }
                    if (isEnable) {
                        toggleSelection(photo);
                        notifyItemChanged(pos);
                    }
                }
            });

        } else {
            holder.ivPhoto.setImageResource(R.drawable.__picker_camera);
        }
    }


    @Override
    public int getItemCount() {
        int photosCount =
                photoDirectories.size() == 0 ? 0 : getCurrentPhotos().size();
        if (showCamera()) {
            return photosCount + 1;
        }
        return photosCount;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;
        private FrameLayout flGif;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            vSelected = itemView.findViewById(R.id.v_selected);
            flGif = itemView.findViewById(R.id.fl_gif);
        }
    }

    public interface OnLimitListener {
        void onLimit();
    }

    public void setOnLimitListener(OnLimitListener onLimitListener) {
        this.onLimitListener = onLimitListener;
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }


    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }


    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }


    public ArrayList<String> getSelectedPhotoPaths() {
        ArrayList<String> selectedPhotoPaths = new ArrayList<>(getSelectedItemCount());
        if (selectedGifList == null) {
            selectedGifList = new ArrayList<>(getSelectedItemCount());
        } else {
            selectedGifList.clear();
        }
        for (String photo : selectedPhotos) {
            selectedPhotoPaths.add(photo);
            selectedGifList.add(photo.endsWith(".gif") ? "gif" : "");
        }

        return selectedPhotoPaths;
    }

    public ArrayList<String> getSelectedGifList() {
        return selectedGifList;
    }

    public void setShowCamera(boolean hasCamera) {
        this.hasCamera = hasCamera;
    }

    public void setPreviewEnable(boolean previewEnable) {
        this.previewEnable = previewEnable;
    }

    public void isLimitSize(boolean limitSize) {
        this.limitSize = limitSize;
    }

    public boolean showCamera() {
        return (hasCamera && currentDirectoryIndex == MediaStoreHelper.INDEX_ALL_PHOTOS);
    }

    @Override
    public void onViewRecycled(PhotoViewHolder holder) {
        glide.clear(holder.ivPhoto);
        super.onViewRecycled(holder);
    }
}
