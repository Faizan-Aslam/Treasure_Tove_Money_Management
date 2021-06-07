package com.mcapp.mcapp;

public class Model {
    String id,transactionName,amount,currency,currencySymbol,category,comment,date,paymentMethod, favourite;

    public Model(){}

    public Model(String id,String transactionName,String amount,String category,String comment,String date,
                 String paymentMethod, String favourite, String currency, String currencySymbol){
        this.id=id;
        this.transactionName = transactionName;
        this.amount=amount;
        this.category = category;
        this.comment=comment;
        this.date=date;
        this.paymentMethod=paymentMethod;
        this.favourite = favourite;
        this.currency = currency;
        this.currencySymbol =currencySymbol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }
}
