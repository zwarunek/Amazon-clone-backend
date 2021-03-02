package com.zacharywarunek.kettering.cs461project.entitys;

import javax.persistence.*;

@Entity
@Table(name = "Account")
public class Account {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "AccountId")
    private int accountId;
    @Column(name = "FirstName")
    private String firstName;
    @Column(name = "LastName")
    private String lastName;
    @Column(name = "Password")
    private String password;
    @Column(name = "PrimeMember")
    private boolean primeMember;
    @Column(name = "Email")
    private String email;
    @Transient
    private String token;

    public Account constructEntity(String firstName, String lastName, String password, boolean primeMember, String email) {
        Account account = new Account();
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setPassword(password);
        account.setPrimeMember(primeMember);
        account.setEmail(email);
        return account;
    }


    @Override
    public String toString(){
        return String.format("Account [accountId=%d, firstName=%s, lastName=%s, password=%s, primeMember=%b, email=%s]", accountId, firstName, lastName, password, primeMember, email);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPrimeMember() {
        return primeMember;
    }

    public void setPrimeMember(boolean primeMember) {
        this.primeMember = primeMember;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
