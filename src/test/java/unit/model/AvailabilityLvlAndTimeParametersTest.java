package unit.model;

import org.example.model.AvailabilityLvlAndTimeParameters;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AvailabilityLvlAndTimeParametersTest {

    @Test
    public void initialize() {
        AvailabilityLvlAndTimeParameters parameters = new AvailabilityLvlAndTimeParameters();
        String availabilityLvlAsString = "99.9";
        String executionTimeAsString = "45";
        parameters.setAvailabilityLvlAsString(availabilityLvlAsString);
        parameters.setResponseExecutionTimeAsString(executionTimeAsString);

        parameters.initialize();

        assertEquals(Double.parseDouble(availabilityLvlAsString), parameters.getAvailabilityLvl(), 0.05);
        assertEquals(Double.parseDouble(executionTimeAsString), parameters.getResponseExecutionTime(), 0.05);

    }

}
