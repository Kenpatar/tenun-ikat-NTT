package ken.tenunikatntt.Model;

import java.util.List;

/**
 * Created by Emilken18 on 6/21/2018.
 */

public class Request {
    private String Phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private String comment;
    private List<Order> kains;

    public Request() {
    }

    public Request(String phone, String name, String address, String total, String status, String comment, List<Order> kains) {
        Phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.kains = kains;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Order> getKains() {
        return kains;
    }

    public void setKains(List<Order> kains) {
        this.kains = kains;
    }
}