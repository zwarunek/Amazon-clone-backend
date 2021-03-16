package com.zacharywarunek.kettering.cs461project.entitys;

import javax.persistence.*;
@Entity
@Table(name = "BrowsingHistory")
public class BrowsingHistory {


    //create table BrowsingHistory
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "AccountID") private int accountID;

    @Column(name = "ProductID") private int productID;

    @Column(name = "TimesViewed") private int timesViewed;

    @Column(name = "MostRecentView") private int recentView;


    public Address constructEntity(int accountID, int productID, int timesViewed, int recentView) {
        BrowsingHistory browsingHistory = new BrowsingHistory();
        browsingHistory.setAccountID(accountID);
        browsingHistory.setProductID(productID);
        browsingHistory.setTimesViewed(timesViewed);
        browsingHistory.setRecentView(recentView);
        return ;
    }

    @Override
    public String toString(){
        return String.format("Address [accountID=%d, productID=%d, timesViewed=%d, recentView=%d]", accountID, productID, timesViewed, recentView);
    }
    public int getAccountID(){
        return accountID;
    }
    public void setAccountID(int accountID){
        this.accountID =accountID;
    }
    public int getProductID(){
        return productID;
    }
    public void setProductID(int productID){
        this.productID =productID;
    }
    public int getTimesViewed(){
        return timesViewed;
    }
    public void setTimesViewed(int timesViewed){
        this.timesViewed = timesViewed;
    }
    public int getRecentView(){
        return recentView;
    }
    public void setRecentView(int recentView){
        this.recentView = recentView;
    }
}

