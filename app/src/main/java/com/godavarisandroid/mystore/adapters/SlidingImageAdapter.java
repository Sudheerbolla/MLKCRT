package com.godavarisandroid.mystore.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.godavarisandroid.mystore.R;
import com.godavarisandroid.mystore.activities.TermsActivity;
import com.godavarisandroid.mystore.models.Ads;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by UMA on 5/5/2018.
 */
public class SlidingImageAdapter extends PagerAdapter {
    private ArrayList<Ads> mAds;
    private LayoutInflater inflater;
    private Context context;


    public SlidingImageAdapter(Context context, ArrayList<Ads> mAds) {
        this.context = context;
        this.mAds = mAds;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mAds.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.item_sliding_images, view, false);

        assert imageLayout != null;
        final ImageView mImgAd = (ImageView) imageLayout.findViewById(R.id.imgAd);
        Picasso.with(context).load(mAds.get(position).image).into(mImgAd);
        view.addView(imageLayout, 0);

        mImgAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TermsActivity.class);
                intent.putExtra("heading", "Offer Details");
                intent.putExtra("urlToLoad", TextUtils.isEmpty(mAds.get(position).url) ? mAds.get(position).url : "https://www.milkcart.co.in");
                context.startActivity(intent);
//                Bundle bundle = new Bundle();
//                bundle.putString("AD_ID", mAds.get(position).id);
//                navigateFragment(new OfferDetailsFragment(), "OFFERDETAILSFRAGMENT", bundle, (FragmentActivity) context);
            }
        });

        return imageLayout;
    }

    public static void navigateFragment(Fragment fragment, String tag, Bundle bundle, FragmentActivity fragmentActivity) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, tag);
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        if (tag != null) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}