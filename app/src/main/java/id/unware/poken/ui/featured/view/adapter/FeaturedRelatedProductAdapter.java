package id.unware.poken.ui.featured.view.adapter;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import id.unware.poken.R;
import id.unware.poken.domain.Product;
import id.unware.poken.tools.StringUtils;
import id.unware.poken.tools.glide.GlideRequest;
import id.unware.poken.tools.glide.GlideRequests;
import id.unware.poken.ui.featured.presenter.IFeaturedPresenter;


/**
 * @author Anwar Pasaribu
 * @since Jul 7 2017
 */
public class FeaturedRelatedProductAdapter extends RecyclerView.Adapter<FeaturedRelatedProductAdapter.ViewHolder> {

    private final ArrayList<Product> mValues;
    private final IFeaturedPresenter mListener;

    private final GlideRequest<Drawable> requestBuilder;

    public FeaturedRelatedProductAdapter(ArrayList<Product> items, IFeaturedPresenter listener, GlideRequests glideRequest) {
        mValues = items;
        mListener = listener;

        requestBuilder = glideRequest.asDrawable().fitCenter();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_browse_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Product item = mValues.get(position);

        String  strProductImageUrl = String.valueOf(item.images.get(0).thumbnail),
                strProductName = item.name;
        double  productPrice = item.price,
                discountAmount = item.discount_amount,
                discountedPrice = productPrice - ((productPrice * discountAmount) / 100);

        requestBuilder
                .clone()
                .load(strProductImageUrl)
                .into(holder.itemImage);

        holder.tvTitle.setText(strProductName);
        holder.tvPrice.setText(StringUtils.formatCurrency(String.valueOf(productPrice)));

        // tvPrice2 to show SALE item
        holder.tvPrice2.setText(StringUtils.formatCurrency(String.valueOf(productPrice)));
        holder.tvPrice2.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);  // Strike
        holder.tvDiscountedPrice.setText(StringUtils.formatCurrency(String.valueOf(discountedPrice)));
        holder.tvDiscountAmount.setText((int) discountAmount + "%");

        // Discount view
        if (discountAmount > 0D) {
            holder.viewFlipperProductPrice.setDisplayedChild(0);
        } else {
            holder.viewFlipperProductPrice.setDisplayedChild(1);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.startProductDetail(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemImage) ImageView itemImage;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvPrice) TextView tvPrice;

        // Sale item
        @BindView(R.id.tvPrice2) TextView tvPrice2;
        @BindView(R.id.tvDiscountedPrice) TextView tvDiscountedPrice;
        @BindView(R.id.tvDiscountAmount) TextView tvDiscountAmount;
        @BindView(R.id.viewFlipperProductPrice) ViewFlipper viewFlipperProductPrice;

        @BindDimen(R.dimen.img_grid_m) int productImageSizeM;

        public final View mView;
        public Product mItem;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }
}
