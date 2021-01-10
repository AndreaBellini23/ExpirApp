package andreabellini.java.expirapp;

public class Product {
    public String expireDate;
    public String productName;
    public String category;

    public Product(){

    }

    public Product(String productName, String expireDate, String category){
        this.productName = productName;
        this.expireDate = expireDate;
        this.category = category;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
