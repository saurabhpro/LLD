package worktech.parkinglot.guice;

import com.google.inject.AbstractModule;
import worktech.parkinglot.service.ParkingService;
import worktech.parkinglot.service.impl.ParkingServiceImpl;

public class BasicModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ParkingService.class).to(ParkingServiceImpl.class);
    }
}