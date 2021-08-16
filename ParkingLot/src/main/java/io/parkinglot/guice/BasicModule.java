package io.parkinglot.guice;

import com.google.inject.AbstractModule;
import io.parkinglot.service.ParkingService;
import io.parkinglot.service.impl.ParkingServiceImpl;

public class BasicModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ParkingService.class).to(ParkingServiceImpl.class);
    }
}