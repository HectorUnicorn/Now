package com.keepyoga.vipyoga.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import com.keepyoga.vipyoga.App;
import com.keepyoga.vipyoga.R;
import com.keepyoga.vipyoga.ui.BaseCompatActivity;
import com.keepyoga.vipyoga.utils.ImageUtil;

/**
 * Created on 2017/10/2.
 *
 * @author ice
 * @GitHub https://github.com/XunMengWinter
 */

public class BigImagePagerActivity extends BaseCompatActivity {

    public static final String KEY_IMAGE_URLS = "image_urls";
    public static final String KEY_ENTER_INDEX = "enter_index";
    public static Integer sExitIndex;

    private List<String> mImageUrls;
    private int mEnterIndex;

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private SparseArray<PhotoView> mPhotoViewMap;

    public static void startThis(final AppCompatActivity activity, List<View> imageViews, List<String> imageUrls, int enterIndex) {
        Intent intent = new Intent(activity, BigImagePagerActivity.class);
        intent.putStringArrayListExtra(KEY_IMAGE_URLS, (ArrayList<String>) imageUrls);
        intent.putExtra(KEY_ENTER_INDEX, enterIndex);

        ActivityOptionsCompat optionsCompat
                = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, imageViews.get(enterIndex), imageUrls.get(enterIndex));

        try {
            ActivityCompat.startActivity(activity, intent,
                    optionsCompat.toBundle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            activity.startActivity(intent);
        }

        activity.setExitSharedElementCallback(new SharedElementCallback() {
            /* 这个方法会调用两次，一次进入前，一次回来前。 */
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);

                if (sExitIndex == null) {
                    return;
                }

                int exitIndex = sExitIndex;
                sExitIndex = null;

                if (exitIndex != enterIndex
                        && imageViews.size() > exitIndex
                        && imageUrls.size() > exitIndex) {
                    names.clear();
                    sharedElements.clear();
                    View view = imageViews.get(exitIndex);
                    String transitName = imageUrls.get(exitIndex);
                    if (view == null) {
                        activity.setExitSharedElementCallback((SharedElementCallback) null);
                        return;
                    }
                    names.add(transitName);
                    sharedElements.put(transitName, view);
                }
                activity.setExitSharedElementCallback((SharedElementCallback) null);
            }
        });

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image_pager);

        //延迟动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

        mEnterIndex = getIntent().getIntExtra(KEY_ENTER_INDEX, 0);
        mImageUrls = getIntent().getStringArrayListExtra(KEY_IMAGE_URLS);
        if (mImageUrls == null) {
            return;
        }

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(4);

        mPhotoViewMap = new SparseArray<>();
        mPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mImageUrls.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                PhotoView photoView = new PhotoView(container.getContext());
//                imageView.setLayoutParams(new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                String imageUrl = mImageUrls.get(position);
                Glide.with(BigImagePagerActivity.this).load(imageUrl).into(photoView);

                if (mPhotoViewMap.get(position) != null) {
                    mPhotoViewMap.remove(position);
                }
                mPhotoViewMap.put(position, photoView);

                photoView.setOnViewTapListener((view, x, y) -> {
                    onBackPressed();
                });

                photoView.setOnLongClickListener(v -> {
                    PhotoView pv = (PhotoView) v;
                    if (pv.getDrawable() == null) {
                        return false;
                    }

                    View view = getLayoutInflater().inflate(R.layout.dialog_save_image, null);

                    AlertDialog alertDialog = new AlertDialog.Builder(BigImagePagerActivity.this)
                            .setView(view)
                            .create();

                    TextView textView = (TextView) view.findViewById(R.id.save_image_tv);
                    textView.setOnClickListener(v1 -> {
                        saveImage(pv.getDrawable(), imageUrl);
                        alertDialog.dismiss();
                    });


                    alertDialog.show();
                    return true;
                });

                if (position == mEnterIndex) {
                    ViewCompat.setTransitionName(photoView, mImageUrls.get(position));
                    setStartPostTransition(photoView);
                }


                container.addView(photoView);
                return photoView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                if (mPhotoViewMap.get(position) != null) {
                    mPhotoViewMap.remove(position);
                }
                container.removeView((View) object);
            }
        };

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mEnterIndex);
    }

    @Override
    public void finishAfterTransition() {
        sExitIndex = mViewPager.getCurrentItem();
        if (sExitIndex != mEnterIndex) {
            setSharedElementCallback(sExitIndex, mPhotoViewMap.get(sExitIndex));
        }
        super.finishAfterTransition();
    }

    private void setSharedElementCallback(final int exitIndex, final View sharedView) {
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                String transitName = mImageUrls.get(exitIndex);
                names.clear();
                sharedElements.clear();
                names.add(transitName);
                sharedElements.put(transitName, sharedView);
            }
        });
    }

    public void setStartPostTransition(final View sharedView) {
        sharedView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedView.getViewTreeObserver().removeOnPreDrawListener(this);
                        //延迟结束，启用动画
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startPostponedEnterTransition();
                        }
                        return false;
                    }
                });
    }

    private void saveImage(Drawable drawable, String imageUrl) {
        dispose();
        RxPermissions rxPermissions = new RxPermissions(this);
        mDisposable = rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(isGranted -> {
                    if (isGranted) {
                        boolean isSaved = ImageUtil.saveImage(this, drawable, imageUrl);
                        if (isSaved) {
                            App.showToast(getString(R.string.save_image_success, Environment.DIRECTORY_PICTURES));
                        } else {
                            App.showToast(R.string.save_image_failed);
                        }
                    } else {
                        new AlertDialog.Builder(BigImagePagerActivity.this)
                                .setMessage(R.string.save_image_failed_permission)
                                .setPositiveButton(R.string.ok, null)
                                .create().show();
                    }
                });
    }

    private Disposable mDisposable;

    private void dispose(){
        if (mDisposable!=null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dispose();
    }
}
