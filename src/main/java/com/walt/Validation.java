package com.walt;

import com.walt.model.Customer;
import com.walt.model.Restaurant;
import org.springframework.stereotype.Service;

@Service
public class Validation {
    public void validateInput(Customer customer, Restaurant restaurant) throws Exception {
        if (customer.getCity().getName() != restaurant.getCity().getName()){
            throw new Exception("Customer and Restaurant should be in the same city");
        }
    }
}
