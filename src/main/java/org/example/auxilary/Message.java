package org.example.auxilary;

public class Message {

    public static final String INPUT_PARAMETERS_NOT_MATCH = "Your input parameters do not match any of the ways you can interact with the application";

    public static final String ANALYZE_BY_AVAILABILITY_LVL_AND_TIME =
            "In order to analyze the execution time and display periods in which the application " + System.lineSeparator() +
            "response level was less than specified in the parameters, pass the following parameters " + System.lineSeparator() +
            "to the application input: " + System.lineSeparator() +
            "cat access.log | java -jar analyze.jar -u 99.9 -t 35, where: " + System.lineSeparator() +
            "analyze.jar - this jar file;" + System.lineSeparator() +
            "-u 99.9 - acceptable response rate for analysis (99.9 %);" + System.lineSeparator() +
            "-t 35 - time that is considered acceptable during analysis (35 ms)";

    public static final String INPUT_DATA_EXCEPTION_MESSAGE = "Perhaps you forgot to input a data stream from access.log file";

    public static final String PARAMETERS_ARE_NOT_VALIDATED_MESSAGE = "Some of the parameters were not validated";


}
