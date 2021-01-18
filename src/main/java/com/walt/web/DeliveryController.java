package com.walt.web;

import com.walt.WaltService;
import com.walt.dao.CustomerRepository;
import com.walt.dao.RestaurantRepository;
import com.walt.model.City;
import com.walt.model.Customer;
import com.walt.model.Delivery;
import com.walt.model.Restaurant;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
public class DeliveryController {
    private final WaltService waltService;

    @Resource
    CustomerRepository customerRepository;

    @Resource
    RestaurantRepository restaurantRepository;

    public DeliveryController(WaltService waltService) {
        this.waltService = waltService;
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello service";
    }

    @PostMapping("/delivery")
    public Delivery newDelivery(@RequestBody DeliveryContext deliveryContext) throws Exception {
            Customer customer = customerRepository.findByName(deliveryContext.getCustomer().getName());
            Restaurant restaurant = restaurantRepository.findByName(deliveryContext.getRestaurant().getName());
            if (customer == null || restaurant == null)
                throw new Exception("Could not find Customer or Restaurant");

            return waltService.createOrderAndAssignDriver(customer,restaurant,deliveryContext.getDate());
    }

}
