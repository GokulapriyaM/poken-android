package id.unware.poken.ui.home.presenter;

import id.unware.poken.domain.Category;
import id.unware.poken.domain.Featured;
import id.unware.poken.domain.Product;
import id.unware.poken.domain.Section;
import id.unware.poken.domain.Seller;

/**
 * @author Anwar Pasaribu
 * @since Jun 01 2017
 */

public interface IHomePresenter {
    void getHomeData();

    void onSectionActionClick(int position, Section section);

    void onCategoryClick(int position, Category category);
    void onSellerClick(int position, Seller seller);
    void onProductClick(int position, Product product);

    void onFeaturedItemClicked(int position, Featured featured);

    void startPokenInstagram();

    void startPokenFacebookPage();

    void startPokenPhoneContact();
}
