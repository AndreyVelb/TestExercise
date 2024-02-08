package integration;

import mockit.Mock;
import mockit.MockUp;
import org.example.Executor;
import org.example.auxilary.Message;
import org.example.model.AvailabilityLvlAndTimeParameters;
import org.example.model.Parameters;
import org.example.service.AnalyzeByTimeAndHttpCodeService;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Optional;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExecutorTest {

    @Nested
    class TestExecuteServiceMethodWithResources {
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
        public void executeService_IsNotSuitableParameters_SystemExit() {
            new MockUp<System>() {
                @Mock
                public void exit(int value) {
                    throw new RuntimeException(String.valueOf(value));
                }
            };

            Executor executor = new Executor();
            String[] args = new String[]{"-a", "3", "-b", "2", "-c", "1"};
            String expectedMessage = Message.INPUT_PARAMETERS_NOT_MATCH
                    + System.lineSeparator()
                    + Message.ANALYZE_BY_AVAILABILITY_LVL_AND_TIME;

            try {
                executor.executeService(args);
            } catch (Exception e) {
                assertEquals("0", e.getMessage());
            }

            assertEquals(expectedMessage, outputStreamCaptor.toString().trim());

        }

        @Test
        public void executeService_AnalyzeByTimeAndHttpCodeService_ParametersNotValidated_SystemExit() {
            new MockUp<System>() {
                @Mock
                public void exit(int value) {
                    throw new RuntimeException(String.valueOf(value));
                }
            };

            Executor executor = new Executor();
            String[] args = new String[]{"-u", "stringParam", "-t", "stringParam"};

            try {
                executor.executeService(args);
            } catch (Exception e) {
                assertEquals("0", e.getMessage());
            }
            assertEquals(Message.PARAMETERS_ARE_NOT_VALIDATED_MESSAGE, outputStreamCaptor.toString().trim());
        }

        @Test
        public void executeService_AnalyzeByTimeAndHttpCodeService_RightParameters() {
            Executor executor = new Executor();
            String[] args = new String[]{"-u", "60.2", "-t", "40"};
            String expectedResult = "16:47:03\t16:48:05\t37,5" + System.lineSeparator() +
                                    "16:49:06\t16:52:07\t35,3";

            try {
                System.setIn(new FileInputStream("src/test/resources/access.log"));
                executor.executeService(args);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            assertEquals(expectedResult, outputStreamCaptor.toString().trim());
        }

        @Test
        public void executeService_EmptyInput_SystemExit() {
            new MockUp<System>() {
                @Mock
                public void exit(int value) {
                    throw new RuntimeException(String.valueOf(value));
                }
            };

            Executor executor = new Executor();
            String[] args = new String[]{"-u", "60.2", "-t", "40"};

            try {
                withTextFromSystemIn().execute(() -> executor.executeService(args));
            } catch (Exception e) {
                assertEquals("0", e.getMessage());
            }
            assertEquals(Message.INPUT_DATA_EXCEPTION_MESSAGE, outputStreamCaptor.toString().trim());
        }
    }


}
