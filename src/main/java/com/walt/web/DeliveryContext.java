package com.walt.web;

import com.walt.model.Customer;
import com.walt.model.Restaurant;

import java.util.Date;

public class DeliveryContext {
    Customer customer;
    Restaurant restaurant;
    Date date;

    public Date getDate() {
        return date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }
}
