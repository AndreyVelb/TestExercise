package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AvailabilityLvlAndTimeParameters implements Parameters {

    //-u parameter
    @Setter
    private String availabilityLvlAsString;
    private double availabilityLvl;

    //-t parameter
    @Setter
    private String responseExecutionTimeAsString;
    private double responseExecutionTime;


    @Override
    public void initialize() {
        availabilityLvl = Double.parseDouble(availabilityLvlAsString);
        responseExecutionTime = Double.parseDouble(responseExecutionTimeAsString);
    }

}
