package org.example;

import org.example.auxilary.Message;
import org.example.model.Parameters;
import org.example.service.AnalyzeByTimeAndHttpCodeService;
import org.example.service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Executor {

    private final List<Service> parameterServiceList = new ArrayList<>();

    private final AnalyzeByTimeAndHttpCodeService availabilityLvlAndTimeValidator = new AnalyzeByTimeAndHttpCodeService();


    public Executor() {
        parameterServiceList.add(availabilityLvlAndTimeValidator);
    }


    public void executeService(String[] args) {
        parameterServiceList.forEach(service -> {
            Optional<Parameters> possibleSuitableParameters = service.getSuitableParameters(args);
            if (possibleSuitableParameters.isPresent()) {
                Parameters parameters = possibleSuitableParameters.get();
                if (service.validate(parameters)) {
                    parameters.initialize();
                    service.execute(parameters);
                } else {
                    System.out.println(Message.PARAMETERS_ARE_NOT_VALIDATED_MESSAGE);
                    System.exit(0);
                }
            } else {
                System.out.println(Message.INPUT_PARAMETERS_NOT_MATCH);
                System.out.println(Message.ANALYZE_BY_AVAILABILITY_LVL_AND_TIME);
                System.exit(0);
            }
        });
    }

}
