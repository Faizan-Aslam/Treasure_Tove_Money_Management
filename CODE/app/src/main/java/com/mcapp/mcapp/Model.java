package com.mcapp.mcapp;

public class Model {
    String id,transactionName,amount,category,comment,date,paymentMethod;

    public Model(){}

    public Model(String id,String transactionName,String amount,String category,String comment,String date,String paymentMethod){
        this.id=id;
        this.transactionName = transactionName;
        this.amount=amount;
        this.category = category;
        this.comment=comment;
        this.date=date;
        this.paymentMethod=paymentMethod;
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
}
