package com.zacharywarunek.amazonclone.entitys;

//TODO Create Order History table in database
import javax.persistence.*;
@Entity
@Table(name = "OrderHistory")
public class OrderHistory {

    //create table OrderHistory
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "AccountID") private int accountID;

    @Column(name = "OrderNumber") private int orderNumber;

    @Column(name = "Status") private int status;

    @Column(name = "Total") private double total;

    @Column(name = "ProductListID") private  int productListID;

    @Column(name = "AddressID") private  int addressID;

    @Column(name = "PaymentMethodID") private  int paymentMethodID;

    @Column(name = "Timestamp") private  int timestamp;

    public OrderHistory constructEntity(int accountID, int addressID, int orderNumber, int status, double total, int productListID, int paymentMethodID, int timestamp) {
        OrderHistory orderHistory= new OrderHistory();
        orderHistory.setAccountID(accountID);
        orderHistory.setAddressID(addressID);
        orderHistory.setOrderNumber(orderNumber);
        orderHistory.setStatus(status);
        orderHistory.setTotal(total);
        orderHistory.setProductListID(productListID);
        orderHistory.setPaymentMethodID(paymentMethodID);
        orderHistory.setTimestamp(timestamp);
        return orderHistory;
    }

    @Override
    public String toString(){
        return String.format("OrderHistory [addressID=%d, addressID=%d, orderNumber=%d,status=%d, total=%d, productListID=%s,paymentMethodID=%s,timestamp=%d]",addressID,addressID,orderNumber,status,productListID,paymentMethodID,timestamp);
    }
    public int getAccountID(){
        return accountID;
    }
    public void setAccountID(int accountID){
        this.accountID =accountID;
    }
    public int getAddressID(){
        return addressID;
    }
    public void setAddressID(int addressID){
        this.addressID =addressID;
    }
    public int getOrderNumber(){
        return orderNumber;
    }
    public void setOrderNumber(int orderNumber){
        this.orderNumber = orderNumber;
    }
    public int getStatus(){
        return status;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public double getTotal(){
        return total;
    }
    public void setTotal(double total){
        this.total = total;
    }
    public int getProductListID(){
        return productListID;
    }
    public void setProductListID(int productListID){
        this.productListID = OrderHistory.this.productListID;
    }
    public int getPaymentMethodID(){
        return paymentMethodID;
    }
    public void setPaymentMethodID(int paymentMethodID){
        this.paymentMethodID = OrderHistory.this.paymentMethodID;
    }
    public int getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(int timestamp){
        this.timestamp = OrderHistory.this.timestamp;
    }




}
