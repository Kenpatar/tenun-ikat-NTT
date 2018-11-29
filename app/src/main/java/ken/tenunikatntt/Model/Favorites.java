package ken.tenunikatntt.Model;

/**
 * Created by Emilken18 on 11/28/2018.
 */

public class Favorites {
    private String KainId, KainName, KainPrice, KainMenuId, KainImage, KainDiscount, KainDescription, UserPhone;

    public Favorites() {
    }

    public Favorites(String kainId, String kainName, String kainPrice, String kainMenuId, String kainImage, String kainDiscount, String kainDescription, String userPhone) {
        KainId = kainId;
        KainName = kainName;
        KainPrice = kainPrice;
        KainMenuId = kainMenuId;
        KainImage = kainImage;
        KainDiscount = kainDiscount;
        KainDescription = kainDescription;
        UserPhone = userPhone;
    }

    public String getKainId() {
        return KainId;
    }

    public void setKainId(String kainId) {
        KainId = kainId;
    }

    public String getKainName() {
        return KainName;
    }

    public void setKainName(String kainName) {
        KainName = kainName;
    }

    public String getKainPrice() {
        return KainPrice;
    }

    public void setKainPrice(String kainPrice) {
        KainPrice = kainPrice;
    }

    public String getKainMenuId() {
        return KainMenuId;
    }

    public void setKainMenuId(String kainMenuId) {
        KainMenuId = kainMenuId;
    }

    public String getKainImage() {
        return KainImage;
    }

    public void setKainImage(String kainImage) {
        KainImage = kainImage;
    }

    public String getKainDiscount() {
        return KainDiscount;
    }

    public void setKainDiscount(String kainDiscount) {
        KainDiscount = kainDiscount;
    }

    public String getKainDescription() {
        return KainDescription;
    }

    public void setKainDescription(String kainDescription) {
        KainDescription = kainDescription;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }
}
