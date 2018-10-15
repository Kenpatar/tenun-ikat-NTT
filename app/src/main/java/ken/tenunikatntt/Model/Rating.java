package ken.tenunikatntt.Model;

/**
 * Created by Emilken18 on 7/20/2018.
 */

public class Rating {
    private String userPhone; //both key and value
    private String kainId;
    private String rateValue;
    private String comment;

    public Rating() {
    }

    public Rating(String userPhone, String kainId, String rateValue, String comment) {
        this.userPhone = userPhone;
        this.kainId = kainId;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getKainId() {
        return kainId;
    }

    public void setKainId(String kainId) {
        this.kainId = kainId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
