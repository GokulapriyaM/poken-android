package id.unware.poken.ui.shoppingorder.model;

import id.unware.poken.domain.AddressBook;
import id.unware.poken.domain.OrderDetail;
import id.unware.poken.ui.shoppingorder.presenter.IShoppingOrderModelPresenter;
import id.unware.poken.ui.shoppingorder.presenter.ShoppingOrderPresenter;

/**
 * @author Anwar Pasaribu
 * @since Jun 07 2017
 */

public interface IShoppingOrderModel {
    void requestShoppingOrderData(IShoppingOrderModelPresenter presenter);

    void postNewAddressBook(IShoppingOrderModelPresenter presenter, AddressBook addressBook);

    void getAddressBookData(IShoppingOrderModelPresenter presenter);

    void postOrUpdateOrderDetails(IShoppingOrderModelPresenter presenter, AddressBook addressBook, long previousOrderDetailId);

    void postOrUpdateOrderedProduct(IShoppingOrderModelPresenter presenter, OrderDetail orderDetail, long[] shoppingCartIds);

    void requestShoppingOrderDataById(IShoppingOrderModelPresenter presenter, long orderedProductId);

    void parseSelectedShoppingCarts(IShoppingOrderModelPresenter presenter, String shoppingCartArrayListJsonString);

    void patchOrderDetailsStatus(IShoppingOrderModelPresenter presenter, long orderDetailsId, int orderStatus);

    void patchOrderDetailsTrackingId(IShoppingOrderModelPresenter presenter, long orderDetailsId, String trackingId);
}
