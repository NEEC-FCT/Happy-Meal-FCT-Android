package miguelcalado.restauracaomenus;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class CustomPagerAdapter3 extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    String[] mResources;

    public CustomPagerAdapter3(Context context, String[] Resources) {
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
        View itemView = mLayoutInflater.inflate(R.layout.pager_item_dialog, container, false);

        PhotoView imageView = (PhotoView) itemView.findViewById(R.id.imageView);

        // imageView.setImageResource(mResources[position]);
        Glide.with(mContext).load(mResources[position]).into(imageView);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}