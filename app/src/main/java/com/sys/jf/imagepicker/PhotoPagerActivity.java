package com.sys.jf.imagepicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.sys.jf.imagepicker.fragment.ImagePagerFragment;

import com.sys.jf.imagepicker.R;

import static com.sys.jf.imagepicker.PhotoPicker.KEY_SELECTED_PHOTOS;
import static com.sys.jf.imagepicker.PhotoPreview.EXTRA_CURRENT_ITEM;
import static com.sys.jf.imagepicker.PhotoPreview.EXTRA_PHOTOS;
import static com.sys.jf.imagepicker.PhotoPreview.EXTRA_SHOW_DELETE;

/**
 * Created by donglua on 15/6/24.
 */
public class PhotoPagerActivity extends AppCompatActivity {

    private ImagePagerFragment pagerFragment;
    private TextView photoNumberTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.__picker_activity_photo_pager);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
        List<String> paths = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
        boolean showDelete = getIntent().getBooleanExtra(EXTRA_SHOW_DELETE, true);

        if (pagerFragment == null) {
            pagerFragment =
                    (ImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
        }
        pagerFragment.setPhotos(paths, currentItem);

        photoNumberTv = findViewById(R.id.photo_number);
        ImageView imgDestroyPage = findViewById(R.id.img_destory_page);
        ImageView imgDeletePhoto = findViewById(R.id.img_delete_photo);
        updateActionBarTitle();
        imgDestroyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imgDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int index = pagerFragment.getCurrentItem();

                final String deletedPath = pagerFragment.getPaths().get(index);

                Snackbar snackbar = Snackbar.make(pagerFragment.getView(), R.string.__picker_deleted_a_photo,
                        Snackbar.LENGTH_LONG);

                if (pagerFragment.getPaths().size() <= 1) {

                    // show confirm dialog
                    new AlertDialog.Builder(PhotoPagerActivity.this)
                            .setTitle(R.string.__picker_confirm_to_delete)
                            .setPositiveButton(R.string.__picker_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    pagerFragment.getPaths().remove(index);
                                    pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                                    onBackPressed();
                                }
                            })
                            .setNegativeButton(R.string.__picker_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();

                } else {

                    snackbar.show();

                    pagerFragment.getPaths().remove(index);
                    pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                }

                snackbar.setAction(R.string.__picker_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (pagerFragment.getPaths().size() > 0) {
                            pagerFragment.getPaths().add(index, deletedPath);
                        } else {
                            pagerFragment.getPaths().add(deletedPath);
                        }
                        pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                        pagerFragment.getViewPager().setCurrentItem(index, true);
                    }
                });
            }
        });

        pagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateActionBarTitle();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(KEY_SELECTED_PHOTOS, pagerFragment.getPaths());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    public void updateActionBarTitle() {
        photoNumberTv.setText(getString(R.string.__picker_image_index, pagerFragment.getViewPager().getCurrentItem() + 1,
                pagerFragment.getPaths().size()));
    }
}
