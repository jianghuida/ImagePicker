package com.sys.jf.imagepicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.chrisbanes.photoview.PhotoView;
import com.sys.jf.imagepicker.R;
import com.sys.jf.imagepicker.utils.AndroidLifecycleUtils;

/**
 * Created by donglua on 15/6/21.
 */
public class PhotoPagerAdapter extends PagerAdapter {

  private List<String> paths = new ArrayList<>();
  private RequestManager mGlide;

  public PhotoPagerAdapter(RequestManager glide, List<String> paths) {
    this.paths = paths;
    this.mGlide = glide;
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {
    final Context context = container.getContext();
    View itemView = LayoutInflater.from(context)
        .inflate(R.layout.__picker_picker_item_pager, container, false);

    PhotoView imageView = itemView.findViewById(R.id.iv_pager);
    imageView.setBackgroundColor(context.getResources().getColor(R.color.colorBlack));
    final String path = paths.get(position);
    final Uri uri;
    if (path.startsWith("http")) {
      uri = Uri.parse(path);
    } else {
      uri = Uri.fromFile(new File(path));
    }

    boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(context);

    if (canLoadImage) {
      Glide.with(context)
              .load(uri)
              .into(imageView);
    }
    container.addView(itemView);

    return itemView;
  }


  @Override public int getCount() {
    return paths.size();
  }


  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }


  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
    mGlide.clear((View) object);
  }

  @Override
  public int getItemPosition (Object object) { return POSITION_NONE; }

}
