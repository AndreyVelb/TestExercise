package org.example.service;

import lombok.Getter;
import lombok.Setter;
import org.example.auxilary.Message;
import org.example.model.AvailabilityLvlAndTimeParameters;
import org.example.model.Parameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class AnalyzeByTimeAndHttpCodeService implements Service {

    private final Pattern httpCodePattern = Pattern.compile("\\s([01-9]{3})\\s");
    private final Pattern requestExecutionTimePattern = Pattern.compile("\\s([01-9]*\\.[01-9]*)\\s");
    private final Pattern responseTimePattern = Pattern.compile(":([01-9]{2}:[01-9]{2}:[01-9]{2})\\s");
    private final DecimalFormat decimalFormat = new DecimalFormat("#.#");


    @Override
    public Optional<Parameters> getSuitableParameters(String[] args) {
        String u = "";
        String t = "";
        if (args.length != 4) return Optional.empty();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-u")) {
                i++;
                u = args[i];
            }
            if (args[i].equals("-t")) {
                i++;
                t = args[i];
            }
        }
        if (!u.isEmpty() && !t.isEmpty()) {
            AvailabilityLvlAndTimeParameters params = new AvailabilityLvlAndTimeParameters();
            params.setAvailabilityLvlAsString(u);
            params.setResponseExecutionTimeAsString(t);
            return Optional.of(params);
        } else return Optional.empty();
    }


    @Override
    public boolean validate(Parameters parameters) {
        AvailabilityLvlAndTimeParameters initialParams = (AvailabilityLvlAndTimeParameters) parameters;
        double doubleU;
        double doubleT;
        try {
            doubleU = Double.parseDouble(initialParams.getAvailabilityLvlAsString());
            doubleT = Double.parseDouble(initialParams.getResponseExecutionTimeAsString());
        } catch (NumberFormatException e) {
            return false;
        }

        if (doubleU <= 0 || doubleU > 100) {
            return false;
        } else return doubleT > 0;
    }


    public void execute(Parameters parameters) {
        AvailabilityLvlAndTimeParameters initialParams = (AvailabilityLvlAndTimeParameters) parameters;

        //Time of first bad request in fail sequence
        String firstFailTime = "";
        //Time of last bad request in fail sequence
        String lastFailTime = "";

        //Total number of requests in fail sequence
        int totalCount = 0;
        //Number of positive requests in fail sequence
        int positiveResponseCount = 0;

        double currentAvailabilityLevel = 0.0;
        double lastFailureAvailabilityLevel = 0.0;

        try {
            if (System.in.available() > 0) {
                try (InputStreamReader isr = new InputStreamReader(System.in);
                     BufferedReader bufferedReader = new BufferedReader(isr)) {

                    String inputData;
                    while((inputData = bufferedReader.readLine()) != null) {

                        if (firstFailTime.isEmpty()) {
                            if (isBadByHttpCode(inputData) || isBadByTime(inputData, initialParams.getResponseExecutionTime())) {
                                Optional<String> responseDate = getResponseTime(inputData);
                                if (responseDate.isPresent()) {
                                    firstFailTime = responseDate.get();
                                    lastFailTime = responseDate.get();
                                    totalCount++;
                                    lastFailureAvailabilityLevel = calculatePercentage(positiveResponseCount, totalCount);
                                }
                            }
                        } else {
                            if (isBadByHttpCode(inputData) || isBadByTime(inputData, initialParams.getResponseExecutionTime())) {
                                Optional<String> responseDate = getResponseTime(inputData);
                                if (responseDate.isPresent()) {
                                    lastFailTime = responseDate.get();
                                    totalCount++;
                                    lastFailureAvailabilityLevel = calculatePercentage(positiveResponseCount, totalCount);
                                }
                            } else {
                                positiveResponseCount++;
                                totalCount++;
                                currentAvailabilityLevel = calculatePercentage(positiveResponseCount, totalCount);
                                if (currentAvailabilityLevel >= initialParams.getAvailabilityLvl()) {
                                    printResultToConsole(firstFailTime, lastFailTime, lastFailureAvailabilityLevel);
                                    firstFailTime = "";
                                    lastFailTime = "";
                                    positiveResponseCount = 0;
                                    totalCount = 0;
                                    lastFailureAvailabilityLevel = 0.0;
                                    currentAvailabilityLevel = 0.0;
                                }
                            }
                        }

                    }
                }
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            System.out.println(Message.INPUT_DATA_EXCEPTION_MESSAGE);
            System.exit(0);
        }

        currentAvailabilityLevel = calculatePercentage(positiveResponseCount, totalCount);

        if (currentAvailabilityLevel >= initialParams.getAvailabilityLvl()) {
            printResultToConsole(firstFailTime, lastFailTime, lastFailureAvailabilityLevel);
        } else {
            printResultToConsole(firstFailTime, lastFailTime, currentAvailabilityLevel);
        }
    }


    //Determines the time at which the response was sent
    public Optional<String> getResponseTime(String inputData) {
        Matcher dateAndTimeMatcher = responseTimePattern.matcher(inputData);

        if (dateAndTimeMatcher.find()) {

            return Optional.of(dateAndTimeMatcher.group(1));
        } else {
            return Optional.empty();
        }
    }


    //Determines whether the request is bad by HTTP code
    public boolean isBadByHttpCode(String inputData) {
        Matcher httpCodeMatcher = httpCodePattern.matcher(inputData);

        if (httpCodeMatcher.find()) {
            return Integer.parseInt(httpCodeMatcher.group(1)) >= 500;
        } else {
            return false;
        }
    }


    //Determines whether the request is timing poor
    public boolean isBadByTime(String inputData, double acceptableTime) {
        Matcher requestProcessingTimeMatcher = requestExecutionTimePattern.matcher(inputData);

        if (requestProcessingTimeMatcher.find()) {
            return Double.parseDouble(requestProcessingTimeMatcher.group(1)) >= acceptableTime;
        } else {
            return false;
        }
    }

    public double calculatePercentage(int quotient, int general) {
        return 100.0 * quotient / general;
    }

    private void printResultToConsole(String startFailureSequence, String finishFailureSequence, double availabilityLevel) {
        System.out.println(startFailureSequence + "\t" + finishFailureSequence + "\t" + String.format("%.1f", availabilityLevel));
    }

}
