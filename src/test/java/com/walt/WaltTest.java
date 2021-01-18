package com.walt;

import com.walt.dao.*;
import com.walt.model.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

@SpringBootTest()
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WaltTest {

    @TestConfiguration
    static class WaltServiceImplTestContextConfiguration {

        @Bean
        public WaltService waltService() {
            return new WaltServiceImpl();
        }
    }

    @Autowired
    WaltService waltService;

    @Resource
    CityRepository cityRepository;

    @Resource
    CustomerRepository customerRepository;

    @Resource
    DriverRepository driverRepository;

    @Resource
    DeliveryRepository deliveryRepository;

    @Resource
    RestaurantRepository restaurantRepository;

    @Autowired
    Validation validation;

    @BeforeEach()
    public void prepareData(){

        City jerusalem = new City("Jerusalem");
        City tlv = new City("Tel-Aviv");
        City bash = new City("Beer-Sheva");
        City haifa = new City("Haifa");

        cityRepository.save(jerusalem);
        cityRepository.save(tlv);
        cityRepository.save(bash);
        cityRepository.save(haifa);

        createDrivers(jerusalem, tlv, bash, haifa);

        createCustomers(jerusalem, tlv, haifa,bash);

        createRestaurant(jerusalem, tlv,haifa);

        initDeliveryRepository();
    }

    private void createRestaurant(City jerusalem, City tlv, City haifa) {
        Restaurant meat = new Restaurant("meat", jerusalem, "All meat restaurant");
        Restaurant vegan = new Restaurant("vegan", tlv, "Only vegan");
        Restaurant cafe = new Restaurant("cafe", tlv, "Coffee shop");
        Restaurant chinese = new Restaurant("chinese", tlv, "chinese restaurant");
        Restaurant mexican = new Restaurant("mexican", haifa, "mexican restaurant ");
        Restaurant italian = new Restaurant("italian", haifa, "italian restaurant ");

        restaurantRepository.saveAll(Lists.newArrayList(meat, vegan, cafe, chinese, mexican,italian));
    }

    private void createCustomers(City jerusalem, City tlv, City haifa,City bash) {
        Customer beethoven = new Customer("Beethoven", tlv, "Ludwig van Beethoven");
        Customer mozart = new Customer("Mozart", jerusalem, "Wolfgang Amadeus Mozart");
        Customer chopin = new Customer("Chopin", haifa, "Frédéric François Chopin");
        Customer rachmaninoff = new Customer("Rachmaninoff", tlv, "Sergei Rachmaninoff");
        Customer bach = new Customer("Bach", tlv, "Sebastian Bach. Johann");
        Customer bashCustomer = new Customer("BashCustomer", bash, "Rabin st");

        customerRepository.saveAll(Lists.newArrayList(beethoven, mozart, chopin, rachmaninoff, bach,bashCustomer));
    }

    private void createDrivers(City jerusalem, City tlv, City bash, City haifa) {
        Driver mary = new Driver("Mary", tlv);
        Driver patricia = new Driver("Patricia", tlv);
        Driver jennifer = new Driver("Jennifer", haifa);
        Driver james = new Driver("James", bash);
        Driver john = new Driver("John", bash);
        Driver robert = new Driver("Robert", jerusalem);
        Driver david = new Driver("David", jerusalem);
        Driver daniel = new Driver("Daniel", tlv);
        Driver noa = new Driver("Noa", haifa);
        Driver ofri = new Driver("Ofri", haifa);
        Driver nata = new Driver("Neta", jerusalem);

        driverRepository.saveAll(Lists.newArrayList(mary, patricia, jennifer, james, john, robert, david, daniel, noa, ofri, nata));
    }

    @Test
    public void testBasics(){

        assertEquals(((List<City>) cityRepository.findAll()).size(),4);
        assertEquals((driverRepository.findAllDriversByCity(cityRepository.findByName("Beer-Sheva")).size()), 2);
    }

    @Test
    public void testDriverFromTA() throws Exception {
        Customer beethoven = customerRepository.findByName("Beethoven");
        Restaurant vegan = restaurantRepository.findByName("vegan");
        Date deliveryDate = new Date(2020,01,23,14,00,00);
        Delivery delivery = waltService.createOrderAndAssignDriver(beethoven,vegan,deliveryDate);
        assertTrue(delivery.getDriver().getCity().getName().equals(beethoven.getCity().getName()));
    }

    @Test
    public void testExceptionCustomerAndRestaurantDifferentCity() throws Exception {
        Customer beethoven = customerRepository.findByName("Beethoven");
        Restaurant meat = restaurantRepository.findByName("meat");
        Date deliveryDate = new Date(2020,01,23,14,00,00);
        assertThrows(Exception.class,()->{
            validation.validateInput(beethoven,meat);
        });
    }

    @Test
    public void testExceptionNoAvailableDriver() throws Exception {
        Date deliveryDate = new Date(2020,01,23,01,00,00);
        Customer beethoven = customerRepository.findByName("Beethoven");

        assertThrows(Exception.class,()->{
            waltService.pickDriver(deliveryDate,beethoven);
        });
    }

    @Test
    public void testLeastBusyDriver() throws Exception {
        Customer bashCustomer =  customerRepository.findByName("BashCustomer");

        Date deliveryDate2 = new Date(2020,01,23,03,00,00);
        Driver leastBusy = waltService.pickDriver(deliveryDate2,bashCustomer);

        assertTrue(leastBusy.getName().equals("James"));
    }

    @Test
    public void testRankReportDescOrder() throws Exception {
        List<DriverDistance> driverDistances = waltService.getDriverRankReport();
        Boolean ordered = true;
        Long prevDistance = Long.MAX_VALUE;
        for (DriverDistance dis: driverDistances) {
            if (dis.getTotalDistance() <= prevDistance){
                prevDistance = dis.getTotalDistance();
            }
            else{
                ordered = false;
                break;
            }
        }
        assertTrue(ordered);
    }

    @Test
    public void testRankReportPerCity() throws Exception {
        City ta= cityRepository.findByName("Tel-Aviv");

        List<DriverDistance> driverDistancesTA = waltService.getDriverRankReportByCity(ta);
        Long expected = 30L;
        assertEquals(expected,driverDistancesTA.get(0).getTotalDistance());
    }

    @Test
    public void testRankReportPerCityNotExist() throws Exception {
        City haifa = cityRepository.findByName("Haifa");

        List<DriverDistance> driverDistancesTA = waltService.getDriverRankReportByCity(haifa);
        int expected = 0;
        assertEquals(expected,driverDistancesTA.size());
    }

    private void initDeliveryRepository() {
        Date deliveryDate = new Date(2020,01,23,01,00,00);
        Customer beethoven =  customerRepository.findByName("Beethoven");
        Restaurant vegan = restaurantRepository.findByName("vegan");
        Driver mary = driverRepository.findByName("Mary");
        Driver patricia = driverRepository.findByName("Patricia");
        Driver daniel = driverRepository.findByName("Daniel");

        Delivery deliveryTA1 = new Delivery(mary,vegan,beethoven,deliveryDate);
        deliveryTA1.setDistance(new Double(13));

        Delivery deliveryTA2 = new Delivery(daniel,vegan,beethoven,deliveryDate);
        deliveryTA2.setDistance(new Double(15));

        Delivery deliveryTA3 = new Delivery(patricia,vegan,beethoven,deliveryDate);
        deliveryTA3.setDistance(new Double(22));

        Date deliveryDate2 = new Date(2020,01,23,03,00,00);
        Delivery deliveryTA4 = new Delivery(patricia,vegan,beethoven,deliveryDate2);
        deliveryTA4.setDistance(new Double(8));

        Customer bashCustomer =  customerRepository.findByName("BashCustomer");
        Restaurant italian = restaurantRepository.findByName("italian");
        Driver james = driverRepository.findByName("James");
        Driver john = driverRepository.findByName("John");

        Delivery deliveryBash1 = new Delivery(james,italian,bashCustomer,deliveryDate);
        deliveryBash1.setDistance(5);

        Delivery deliveryBash2 = new Delivery(john,italian,bashCustomer,deliveryDate);
        deliveryBash2.setDistance(55);

        deliveryRepository.saveAll(Lists.newArrayList(deliveryTA1,deliveryTA2,deliveryTA3,deliveryTA4,deliveryBash1,deliveryBash2));
    }
}
