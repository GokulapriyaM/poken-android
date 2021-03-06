package id.unware.poken.ui.shoppingcart.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import id.unware.poken.R;
import id.unware.poken.controller.ControllerDialog;
import id.unware.poken.domain.Product;
import id.unware.poken.domain.ProductImage;
import id.unware.poken.domain.Seller;
import id.unware.poken.domain.Shipping;
import id.unware.poken.domain.ShoppingCart;
import id.unware.poken.interfaces.InputDialogListener;
import id.unware.poken.tools.StringUtils;
import id.unware.poken.tools.Utils;
import id.unware.poken.ui.shoppingcart.presenter.IShoppingCartPresenter;


public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private static final String TAG = "ShoppingCartAdapter";

    private Context context;
    private List<ShoppingCart> listData;
    private IShoppingCartPresenter presenter;

    public ShoppingCartAdapter(Context context, List<ShoppingCart> items, IShoppingCartPresenter listener) {
        this.context = context;
        this.listData = items;
        this.presenter = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_shopping_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ShoppingCart item = listData.get(position);
        if (item == null) return;

        // Set item val for view holder
        holder.itemValue = item;

        Product product = item.product;
        if (product == null) return;

        Seller seller = product.seller;
        if (seller == null) return;

        ArrayList<ProductImage> images = product.images;
        if (images.isEmpty()) return;

        final long shoppingCartId = item.id;
        // TODO Need more efficient way
        boolean isSelected = presenter.isItemSelected(item);
        String storeName = seller.store_name;
        String productImage = images.get(0).thumbnail;
        String productName = product.name;
        String strShipping = item.shipping == null
                ? "-"
                : item.shipping.name;
        // Shipping name - fee
        strShipping = strShipping.concat(" - ").concat(StringUtils.formatCurrency(String.valueOf(item.shipping_fee)));

        final double originalProductPrice = product.price;
        String strFormattedOriginalPrice = StringUtils.formatCurrency(String.valueOf(originalProductPrice));
        final double discountAmount = product.discount_amount;
        final double afterDiscountProductPrice = originalProductPrice - ((originalProductPrice * discountAmount) / 100);
        final int productStock = product.stock;

        // Product image thumbnail size
        holder.cbSelectAllStoreItem.setChecked(isSelected);
        holder.tvStoreName.setText(storeName);
        Picasso.with(context)
                .load(productImage)
                .resize(holder.size64, holder.size64)
                .centerCrop()
                .into(holder.ivProductImage);
        holder.tvProductName.setText(productName);
        holder.tvSelectedProductStock.setText(context.getString(R.string.lbl_product_stock, product.stock));

        holder.tvProductTotalPrice.setText(strFormattedOriginalPrice);

        // tvPrice2 to show SALE item
        holder.tvPrice2.setText(strFormattedOriginalPrice);
        holder.tvPrice2.setPaintFlags(holder.tvProductTotalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);  // Strike
        holder.tvDiscountedPrice.setText(StringUtils.formatCurrency(String.valueOf(afterDiscountProductPrice)));
        holder.tvDiscountAmount.setText((int) discountAmount + "%");

        // Discount view
        if (discountAmount > 0D) {
            holder.viewFlipperProductPrice.setDisplayedChild(0);
        } else {
            holder.viewFlipperProductPrice.setDisplayedChild(1);
        }

        holder.textItemQuantity.setText(
                String.valueOf(item.quantity)
        );
        holder.tvSelectedShippingMethod.setText(strShipping);

        holder.cbSelectAllStoreItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (presenter != null) {
                    presenter.onItemChecked(
                            holder.getAdapterPosition(),
                            isChecked,
                            shoppingCartId,
                            item.quantity,
                            afterDiscountProductPrice,
                            item
                    );
                }
            }
        });

        holder.btnAddQuantity.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Check item on adding quantity
                    holder.cbSelectAllStoreItem.setChecked(true);

                    item.quantity = holder.controlItemQuantity(item.quantity, productStock, true);
                    Utils.Log("ShoppingCartAdapter", "[add] Q: " + item.quantity + ", stok: " + productStock);
                    holder.textItemQuantity.setText(
                            String.valueOf(item.quantity)
                    );

                    // Change shopping cart counter on list page
                    if (presenter != null) {
                        presenter.onItemQuantityChanges(
                                holder.getAdapterPosition(),
                                shoppingCartId,
                                item.quantity,
                                afterDiscountProductPrice,
                                item
                        );
                    }
                }
            }
        );

        holder.btnSubstractQuantity.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Check item on quantity changes
                        holder.cbSelectAllStoreItem.setChecked(true);

                        item.quantity = holder.controlItemQuantity(item.quantity, productStock, false);
                        Utils.Log("ShoppingCartAdapter", "[substract] Q: " + item.quantity + ", stok: " + productStock);
                        holder.textItemQuantity.setText(
                                String.valueOf(item.quantity)
                        );

                        // Change shopping cart counter on list page
                        if (presenter != null) {
                            presenter.onItemQuantityChanges(
                                    holder.getAdapterPosition(),
                                    shoppingCartId,
                                    item.quantity,
                                    afterDiscountProductPrice, item);
                        }
                    }
                }
        );

        Utils.Log(TAG, "Extra note: \"" + item.extra_note + "\"");
        String extraNote;
        if (StringUtils.isEmpty(item.extra_note)) {
            extraNote = holder.hint_cart_add_note;
        } else {
            extraNote = item.extra_note;
        }
        holder.rowCartAddNoteTextView.setText(extraNote);

        holder.rowCartAddNoteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Input text dialog.
                ControllerDialog.getInstance().showInputDialog(
                        "Tambah catatan",
                        item.extra_note,
                        "Cth. warna baju, tipe hape, dsb.",
                        InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE,
                        context,
                        new InputDialogListener() {
                            @Override
                            public void onInputTextDone(CharSequence text) {

                                Utils.Log(TAG, "Input extra note: \"" + text + "\"");

                                String prevExtraNote = item.extra_note + "";

                                if (StringUtils.isEmpty(prevExtraNote) && StringUtils.isEmpty(text + "")) return;

                                // Check item on adding quantity
                                holder.cbSelectAllStoreItem.setChecked(true);

                                if (StringUtils.isEmpty(String.valueOf(text))) {
                                    holder.rowCartAddNoteTextView.setText(holder.hint_cart_add_note);
                                } else {
                                    holder.rowCartAddNoteTextView.setText(text);
                                }

                                item.extra_note = text + "";

                                if (presenter != null) {
                                    presenter.addExtraNote(
                                            holder.getAdapterPosition(),
                                            item
                                    );
                                }
                            }
                        }

                );
            }
        });

        // DETECT WHEN NEXT ITEM HAVE THE SAME SELLER ID
        // THEN REMOVE BOTTOM MARGIN
        /*
        int nextItemPos = position + 1;
        if ( (nextItemPos > 0 && nextItemPos < listData.size())
                && listData.get(nextItemPos) != null
                && listData.get(nextItemPos).product.seller.id == item.product.seller.id) {
            Utils.Logs('i', TAG, "Next item has the same Seller ID. Seller: " + listData.get(nextItemPos).product.seller.store_name);
            RecyclerView.LayoutParams recyclerViewLayoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            recyclerViewLayoutParams.bottomMargin = 0;
        } else {
            RecyclerView.LayoutParams recyclerViewLayoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            recyclerViewLayoutParams.bottomMargin = holder.itemGapL;
        }
        */

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ShoppingCart itemValue;

        @BindView(R.id.cbSelectAllStoreItem) CheckBox cbSelectAllStoreItem;
        @BindView(R.id.ivStoreAvatar) ImageView ivStoreAvatar;
        @BindView(R.id.tvStoreName) TextView tvStoreName;
        @BindView(R.id.ivProductImage) ImageView ivProductImage;
        @BindView(R.id.tvProductName) TextView tvProductName;
        @BindView(R.id.tvSelectedProductStock) TextView tvSelectedProductStock;
        @BindView(R.id.btnDeleteCartItem) ImageButton btnDeleteCartItem;

        // Normal price
        @BindView(R.id.tvProductTotalPrice) TextView tvProductTotalPrice;

        // Sale product
        @BindView(R.id.tvPrice2) TextView tvPrice2;
        @BindView(R.id.tvDiscountedPrice) TextView tvDiscountedPrice;
        @BindView(R.id.tvDiscountAmount) TextView tvDiscountAmount;
        @BindView(R.id.viewFlipperProductPrice) ViewFlipper viewFlipperProductPrice;

        // ITEM QUANTITY
        @BindView(R.id.parentQuantityControl) CardView parentQuantityControl;
        @BindView(R.id.btnAddQuantity) ImageButton btnAddQuantity;
        @BindView(R.id.btnSubstractQuantity) ImageButton btnSubstractQuantity;
        @BindView(R.id.textItemQuantity) TextView textItemQuantity;
        @BindView(R.id.tvSelectedShippingMethod) TextView tvSelectedShippingMethod;

        // ADD EXTRA NOTE
        @BindView(R.id.rowCartAddNoteTextView) TextView rowCartAddNoteTextView;

        // RESOURCE
        @BindString(R.string.hint_cart_add_note) String hint_cart_add_note;
        @BindDimen(R.dimen.clickable_size_64) int size64;
        @BindDimen(R.dimen.item_gap_l) int itemGapL;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            btnDeleteCartItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnDeleteCartItem) {
                ControllerDialog.getInstance().showYesNoDialog(
                    context.getString(R.string.msg_shopping_cart_confirm_deletion),
                    context,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DialogInterface.BUTTON_POSITIVE == which) {
                                if (presenter != null) {
                                    // Delete cart item
                                    presenter.deleteShoppingCartItem(
                                            getAdapterPosition(),
                                            itemValue.id);
                                }
                            }
                        }
                    },
                    context.getString(R.string.btn_shopping_cart_confirm_deletion),  // YES
                    context.getString(R.string.btn_negative_cancel)  // NO
                );

            }
        }

        private int controlItemQuantity(int currentQuantity, int maxQuantity, boolean isAdd) {
            if (isAdd && currentQuantity < maxQuantity) {
                currentQuantity = currentQuantity + 1;
            } else if (!isAdd && currentQuantity > 1) {
                currentQuantity = currentQuantity - 1;
            }

            return currentQuantity;
        }
    }
}
