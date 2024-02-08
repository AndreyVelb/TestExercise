package org.example.service;

import org.example.model.Parameters;

import java.util.Optional;

public interface Service {

    void execute(Parameters parameters);

    boolean validate(Parameters parameters);

    Optional<Parameters> getSuitableParameters(String[] args);

}
