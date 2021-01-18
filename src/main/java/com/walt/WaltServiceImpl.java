package com.walt;

import com.walt.dao.CityRepository;
import com.walt.dao.CustomerRepository;
import com.walt.dao.DeliveryRepository;
import com.walt.dao.DriverRepository;
import com.walt.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class WaltServiceImpl implements WaltService {
    @Resource
    DeliveryRepository deliveryRepository;

    @Resource
    DriverRepository driverRepository;

    @Resource
    CityRepository cityRepository;

    @Autowired
    Validation validation;

    @Override
    public Delivery createOrderAndAssignDriver(Customer customer, Restaurant restaurant, Date deliveryDate) throws Exception {
        validateInput(customer,restaurant);
        Driver driver =  pickDriver(deliveryDate, customer);
        Delivery delivery = new Delivery(driver,restaurant,customer,deliveryDate);
        delivery.setDistance(ThreadLocalRandom.current().nextDouble(0.1, 20));
        deliveryRepository.save(delivery);

        return delivery;
    }

    private void validateInput(Customer customer, Restaurant restaurant) throws Exception {
        validation.validateInput(customer,restaurant);
    }

    public Driver pickDriver(Date deliveryTime,Customer customer) throws Exception {
        List<Driver> drivers = driverRepository.findAllDriversByCity(cityRepository.findByName(customer.getCity().getName()));
        Driver pickedDriver = null;
        int leastBusy=Integer.MAX_VALUE;
        for (Driver drv : drivers){
            Boolean occupiedDriver = deliveryRepository.findAllDeliveryByDriverAndDeliveryTime(drv,deliveryTime).size()> 0 ? true:false;
            if (!occupiedDriver) {
                int sumDeliveries= deliveryRepository.findAllDeliveryByDriver(drv).size();
                if(sumDeliveries < leastBusy){
                    leastBusy= sumDeliveries;
                    pickedDriver = drv;
                }
            }
        }
        if (pickedDriver == null) {
            throw new Exception("No driver was found");
        }
        return pickedDriver;
    }

    @Override
    public List<DriverDistance> getDriverRankReport() {
        List<DriverDistance> driverDistances = deliveryRepository.driverDistanceReport();
        return driverDistances;
    }

    @Override
    public List<DriverDistance> getDriverRankReportByCity(City city) {
        List<DriverDistance> driverDistances = deliveryRepository.driverDistanceReportInCity(city.getId());
        return driverDistances;
    }
}
