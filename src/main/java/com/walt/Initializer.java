package com.walt;

import com.walt.dao.CityRepository;
import com.walt.dao.CustomerRepository;
import com.walt.dao.DriverRepository;
import com.walt.dao.RestaurantRepository;
import com.walt.model.City;
import com.walt.model.Customer;
import com.walt.model.Driver;
import com.walt.model.Restaurant;
import org.assertj.core.util.Lists;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class Initializer implements CommandLineRunner {

    private RestaurantRepository restaurantRepository;
    private CityRepository cityRepository;
    private CustomerRepository customerRepository;
    private DriverRepository driverRepository;

    public Initializer(RestaurantRepository restaurantRepository, CityRepository cityRepository, CustomerRepository customerRepository, DriverRepository driverRepository) {
        this.restaurantRepository = restaurantRepository;
        this.cityRepository = cityRepository;
        this.customerRepository = customerRepository;
        this.driverRepository = driverRepository;
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

    @Override
    public void run(String... args) throws Exception {
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
    }
}
