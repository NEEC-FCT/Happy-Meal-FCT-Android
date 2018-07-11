package miguelcalado.restauracaomenus;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import miguelcalado.restauracaomenus.R;

public class CustomPagerAdapter2 extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    String[] mResources;

    public CustomPagerAdapter2(Context context, String[] Resources) {
        mResources = Resources;
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        // imageView.setImageResource(mResources[position]);
        Glide.with(mContext).load(mResources[position]).into(imageView);
        container.addView(itemView);

        final int pos=position;

        itemView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Dialog dialog=new Dialog(mContext,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_setconnection);


                CustomPagerAdapter3 mCustomPagerAdapter = new CustomPagerAdapter3(mContext, mResources);
                ViewPager mViewPager = (ViewPager) dialog.findViewById(R.id.pager);
                mViewPager.setAdapter(mCustomPagerAdapter);

                dialog.show();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }


}