package unit.service;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import lombok.Getter;
import mockit.Mock;
import mockit.MockUp;
import org.example.auxilary.Message;
import org.example.model.AvailabilityLvlAndTimeParameters;
import org.example.model.Parameters;
import org.example.service.AnalyzeByTimeAndHttpCodeService;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.Optional;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.junit.jupiter.api.Assertions.*;


public class AnalyzeByTimeAndHttpCodeServiceTest {

    @Test
    public void getSuitableParameters_RightParameters_Optional() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        String[] args = new String[]{"-u", "67.2", "-t", "35.5"};

        Optional<Parameters> parameters = analyzeByTimeAndHttpCodeService.getSuitableParameters(args);

        assertTrue(parameters.isPresent());
        AvailabilityLvlAndTimeParameters availabilityLvlAndTimeParameters = (AvailabilityLvlAndTimeParameters) parameters.get();
        assertEquals(args[1], availabilityLvlAndTimeParameters.getAvailabilityLvlAsString());
        assertEquals(args[3], availabilityLvlAndTimeParameters.getResponseExecutionTimeAsString());
    }

    @Test
    public void getSuitableParameters_MoreParametersThanNeeded_EmptyOptional() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        String[] args = new String[]{"-u", "67.2", "-t", "35.5", "-r"};

        Optional<Parameters> parameters = analyzeByTimeAndHttpCodeService.getSuitableParameters(args);

        assertFalse(parameters.isPresent());
    }

    @Test
    public void getSuitableParameters_ParameterUIsNotPresent_EmptyOptional() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        String[] args = new String[]{"-b", "67.2", "-t", "35.5"};

        Optional<Parameters> parameters = analyzeByTimeAndHttpCodeService.getSuitableParameters(args);

        assertFalse(parameters.isPresent());
    }

    @Test
    public void getSuitableParameters_ParameterTIsNotPresent_EmptyOptional() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        String[] args = new String[]{"-u", "67.2", "-n", "35.5"};

        Optional<Parameters> parameters = analyzeByTimeAndHttpCodeService.getSuitableParameters(args);

        assertFalse(parameters.isPresent());
    }

    @Test
    public void getSuitableParameters_ArgsAreEmpty_EmptyOptional() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        String[] args = new String[0];

        Optional<Parameters> parameters = analyzeByTimeAndHttpCodeService.getSuitableParameters(args);

        assertFalse(parameters.isPresent());
    }

    @Test
    public void validate_RightParameters_True() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        AvailabilityLvlAndTimeParameters avLvlAndTimeParams = new AvailabilityLvlAndTimeParameters();
        avLvlAndTimeParams.setAvailabilityLvlAsString("99.0");
        avLvlAndTimeParams.setResponseExecutionTimeAsString("45");

        boolean result = analyzeByTimeAndHttpCodeService.validate(avLvlAndTimeParams);

        assertTrue(result);
    }

    @Test
    public void validate_AvailabilityLvlMoreThen100_False() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        AvailabilityLvlAndTimeParameters avLvlAndTimeParams = new AvailabilityLvlAndTimeParameters();
        avLvlAndTimeParams.setAvailabilityLvlAsString("101.0");
        avLvlAndTimeParams.setResponseExecutionTimeAsString("45");

        boolean result = analyzeByTimeAndHttpCodeService.validate(avLvlAndTimeParams);

        assertFalse(result);
    }

    @Test
    public void validate_AvailabilityLvlLessThen0_False() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        AvailabilityLvlAndTimeParameters avLvlAndTimeParams = new AvailabilityLvlAndTimeParameters();
        avLvlAndTimeParams.setAvailabilityLvlAsString("-1.0");
        avLvlAndTimeParams.setResponseExecutionTimeAsString("45");

        boolean result = analyzeByTimeAndHttpCodeService.validate(avLvlAndTimeParams);

        assertFalse(result);
    }

    @Test
    public void validate_AvailabilityLvlIsString_False() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        AvailabilityLvlAndTimeParameters avLvlAndTimeParams = new AvailabilityLvlAndTimeParameters();
        avLvlAndTimeParams.setAvailabilityLvlAsString("SomeString");
        avLvlAndTimeParams.setResponseExecutionTimeAsString("45");

        boolean result = analyzeByTimeAndHttpCodeService.validate(avLvlAndTimeParams);

        assertFalse(result);
    }

    @Test
    public void validate_TimeLessThen0_False() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        AvailabilityLvlAndTimeParameters avLvlAndTimeParams = new AvailabilityLvlAndTimeParameters();
        avLvlAndTimeParams.setAvailabilityLvlAsString("99.9");
        avLvlAndTimeParams.setResponseExecutionTimeAsString("-100");

        boolean result = analyzeByTimeAndHttpCodeService.validate(avLvlAndTimeParams);

        assertFalse(result);
    }

    @Test
    public void validate_TimeIsString_False() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        AvailabilityLvlAndTimeParameters avLvlAndTimeParams = new AvailabilityLvlAndTimeParameters();
        avLvlAndTimeParams.setAvailabilityLvlAsString("99.9");
        avLvlAndTimeParams.setResponseExecutionTimeAsString("SomeSting");

        boolean result = analyzeByTimeAndHttpCodeService.validate(avLvlAndTimeParams);

        assertFalse(result);
    }

    @Test
    public void calculatePercentage() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        int quotient = 40;
        int general = 325;
        double expectedResult = 12.307692307692307692307692307692;

        double result = analyzeByTimeAndHttpCodeService.calculatePercentage(quotient, general);

        assertEquals(expectedResult, result, 0.05);
    }

    @Test
    public void isBadByTime_InputMoreThenAcceptableTime_True() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        String inputData = "192.168.32.181 - - [14/06/2017:16:50:07 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 200 2 100.510983 \"-\" \"@list-item-updater\" prio:0";
        double acceptableTime = 45.3;

        boolean result = analyzeByTimeAndHttpCodeService.isBadByTime(inputData, acceptableTime);

        assertTrue(result);
    }

    @Test
    public void isBadByTime_InputLessThenAcceptableTime_False() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        String inputData = "192.168.32.181 - - [14/06/2017:16:50:07 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 200 2 10.510983 \"-\" \"@list-item-updater\" prio:0";
        double acceptableTime = 45.3;

        boolean result = analyzeByTimeAndHttpCodeService.isBadByTime(inputData, acceptableTime);

        assertFalse(result);
    }

    @Test
    public void isBadByHttpCode_201_False() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        String inputData = "192.168.32.181 - - [14/06/2017:16:50:07 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 201 2 10.510983 \"-\" \"@list-item-updater\" prio:0";

        boolean result = analyzeByTimeAndHttpCodeService.isBadByHttpCode(inputData);

        assertFalse(result);
    }

    @Test
    public void isBadByHttpCode_500_True() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        String inputData = "192.168.32.181 - - [14/06/2017:16:50:07 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 500 2 10.510983 \"-\" \"@list-item-updater\" prio:0";

        boolean result = analyzeByTimeAndHttpCodeService.isBadByHttpCode(inputData);

        assertTrue(result);
    }

    @Test
    public void isBadByHttpCode_505_True() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        String inputData = "192.168.32.181 - - [14/06/2017:16:50:07 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 505 2 10.510983 \"-\" \"@list-item-updater\" prio:0";

        boolean result = analyzeByTimeAndHttpCodeService.isBadByHttpCode(inputData);

        assertTrue(result);
    }

    @Test
    public void getResponseTime_RightParameters_Optional() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        String inputData = "192.168.32.181 - - [14/06/2017:16:50:07 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 505 2 10.510983 \"-\" \"@list-item-updater\" prio:0";
        String expectedResult = "16:50:07";

        Optional<String> result = analyzeByTimeAndHttpCodeService.getResponseTime(inputData);

        assertTrue(result.isPresent());
        assertEquals(expectedResult, result.get());
    }

    @Test
    public void getResponseTime_BrokenParameters_Optional() {
        AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
        String inputData = "192.168.32.181 - - [14/06/2017: 16:50:07 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 505 2 10.510983 \"-\" \"@list-item-updater\" prio:0";

        Optional<String> result = analyzeByTimeAndHttpCodeService.getResponseTime(inputData);

        assertFalse(result.isPresent());
    }

    @Nested
    class TestExecuteMethodWithResources {
        private final PrintStream standardOut = System.out;
        private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

        @BeforeEach
        public void setUp() {
            System.setOut(new PrintStream(outputStreamCaptor));
        }

        @AfterEach
        public void closeResources() {
            System.setOut(standardOut);
        }

        @Test
        public void execute_RightParameters() {
            AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
            AvailabilityLvlAndTimeParameters parameters = new AvailabilityLvlAndTimeParameters();
            parameters.setAvailabilityLvlAsString("60.2");
            parameters.setResponseExecutionTimeAsString("40");
            parameters.initialize();
            String expectedResult = "16:47:03\t16:48:05\t37,5" + System.lineSeparator()
                                    + "16:49:06\t16:52:07\t35,3";

            try {
                System.setIn(new FileInputStream("src/test/resources/access.log"));
                analyzeByTimeAndHttpCodeService.execute(parameters);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            assertEquals(expectedResult, outputStreamCaptor.toString().trim());
        }

        @Test
        public void execute_EmptyInput_SystemExit() {
            new MockUp<System>() {
                @Mock
                public void exit(int value) {
                    throw new RuntimeException(String.valueOf(value));
                }
            };

            AnalyzeByTimeAndHttpCodeService analyzeByTimeAndHttpCodeService = new AnalyzeByTimeAndHttpCodeService();
            String[] args = new String[]{"-u", "67.2", "-t", "35.5"};
            Optional<Parameters> parameters = analyzeByTimeAndHttpCodeService.getSuitableParameters(args);
            parameters.get().initialize();

            try {
                withTextFromSystemIn().execute(() -> analyzeByTimeAndHttpCodeService.execute(parameters.get()));
            } catch (RuntimeException e) {
                assertEquals("0", e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            assertEquals(Message.INPUT_DATA_EXCEPTION_MESSAGE, outputStreamCaptor.toString().trim());
        }
    }

}