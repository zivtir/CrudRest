package com.walt.dao;

import com.walt.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DeliveryRepository extends CrudRepository<Delivery, Long> {
    List<Delivery> findAllDeliveryByDriver(Driver driver);
    List<Delivery> findAllDeliveryByDriverAndDeliveryTime(Driver driver,Date deliveryTime);

    @Query("select d.driver as driver, sum(d.distance) as totalDistance from Delivery as d group by d.driver.id order by totalDistance desc")
    List<DriverDistance> driverDistanceReport();

    @Query("select d.driver as driver, sum(d.distance) as totalDistance from Delivery as d  where d.driver.city.id = :cityId group by d.driver.id  order by totalDistance desc")
    List<DriverDistance> driverDistanceReportInCity(@Param("cityId")Long cityId);

}


