package id.unware.poken.ui.shoppingorder.presenter;

import java.util.ArrayList;

import id.unware.poken.domain.AddressBook;
import id.unware.poken.domain.OrderDetail;
import id.unware.poken.domain.ShoppingCart;
import id.unware.poken.domain.ShoppingOrder;
import id.unware.poken.domain.ShoppingOrderInserted;
import id.unware.poken.ui.presenter.BasePresenter;

/**
 * @author Anwar Pasaribu
 * @since Jun 07 2017
 */

public interface IShoppingOrderModelPresenter extends BasePresenter {
    void onShoppingOrderDataResponse(ArrayList<ShoppingOrder> shoppingOrders);

    void onAddressBookCreated(AddressBook newlyCreatedAddressBook);
    void onAddressBookContentResponse(ArrayList<AddressBook> addressBookArrayList);


    void onNetworkMessage(String msg, int messageStatus);

    void onOrderDetailCreatedOrUpdated(OrderDetail orderDetail);

    void onOrderedProductInserted(ShoppingOrderInserted shoppingOrderInserted);

    void onOrderDetailResponse(ShoppingOrder shoppingOrder);

    void onShoppingCartsParseResponse(ArrayList<ShoppingCart> shoppingCartArrayList);
}
