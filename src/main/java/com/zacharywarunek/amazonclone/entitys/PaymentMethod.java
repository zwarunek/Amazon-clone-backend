package com.zacharywarunek.amazonclone.entitys;

import javax.persistence.*;

@Entity
@Table(name = "PaymentMethod")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PMID")
    private int pmId;
    @Column(name = "AccountId")
    private int accountId;
    @Column(name = "AddressId")
    private int addressId;
    @Column(name = "TypeId")
    private int typeId;
    @Column(name = "NameOnCard")
    private String nameOnCard;
    @Column(name = "CardNumber")
    private String cardNumber;
    @Column(name = "Exp")
    private String exp;
    @Column(name = "Cvv")
    private String cvv;
    @Column(name = "Favorite")
    private boolean favorite;

    public PaymentMethod constructEntity(int accountId, int addressId, int typeId, String nameOnCard, String cardNumber, String exp, String cvv) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setAccountId(accountId);
        paymentMethod.setAddressId(addressId);
        paymentMethod.setTypeId(typeId);
        paymentMethod.setNameOnCard(nameOnCard);
        paymentMethod.setCardNumber(cardNumber);
        paymentMethod.setExp(exp);
        paymentMethod.setCvv(cvv);
        return paymentMethod;
    }


    @Override
    public String toString() {
        return String.format("Account [accountId=%d, firstName=%s, lastName=%s, password=%s, primeMember=%b, email=%s]", pmId, pmId, typeId, nameOnCard, cardNumber, exp);
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public int getPmId() {
        return pmId;
    }

    public void setPmId(int pmId) {
        this.pmId = pmId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String isCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
}
