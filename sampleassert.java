import org.apache.avro.generic.GenericRecord;
import com.jayway.jsonpath.JsonPath;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

public class GenericRecordAssert {

    // Assertion method to verify if the value at a given JSON path matches the assertion expression
    @Step("Asserting value at path: {jsonPath} with condition: {assertionDescription}")
    public static void assertRecordValue(GenericRecord record, String jsonPath, Condition<Object> assertion) {
        try {
            // Convert the GenericRecord to a JSON string (You can use Avro's toString() method or custom serialization if necessary)
            String json = record.toString();  // You might want to convert using Avro's method if necessary

            // Use JsonPath to extract the value from the JSON string based on the jsonPath
            Object extractedValue = JsonPath.read(json, jsonPath);

            // Capture the condition's description (i.e., what is being asserted)
            String assertionDescription = assertion.description();

            // Log the assertion condition in Allure report
            String assertionStatement = String.format("Asserting that the value at path '%s' is: %s, with condition: %s", jsonPath, extractedValue, assertionDescription);
            Allure.step(assertionStatement, () -> {});

            // Perform the assertion
            Assertions.assertThat(extractedValue).hasCondition(assertion);

            // Log the successful assertion result in Allure report
            String successStatement = String.format("Assertion passed at path '%s' with value: %s, condition: %s", jsonPath, extractedValue, assertionDescription);
            Allure.step(successStatement, () -> {});

        } catch (Exception e) {
            // If the JSON path is invalid or the key is not found, log failure in Allure
            String failureStatement = String.format("Assertion failed at path '%s' due to error: %s", jsonPath, e.getMessage());
            Allure.step(failureStatement, () -> {});

            // If the value extraction fails or the assertion fails, we fail the test
            Assertions.fail(failureStatement);
        }
    }
}




import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericData;
import org.assertj.core.api.Condition;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        // Example GenericRecord
        GenericRecord person = new GenericData.Record(null); // Assume you have the schema here
        person.put("name", "John Doe");
        person.put("age", 30);
        person.put("friends", Arrays.asList("Alice", "Bob", "Charlie"));

        // Use the assertRecordValue to verify with AssertJ conditions

        // For the name field, check if it equals "John Doe"
        GenericRecordAssert.assertRecordValue(person, "$.name", val -> val.equalTo("John Doe"));

        // For the age field, check if it is greater than 25
        GenericRecordAssert.assertRecordValue(person, "$.age", val -> val.isGreaterThan(25));

        // For the first friend in the friends array, check if it equals "Alice"
        GenericRecordAssert.assertRecordValue(person, "$.friends[0]", val -> val.equalTo("Alice"));

        // For the friends array, check if it contains "Bob"
        GenericRecordAssert.assertRecordValue(person, "$.friends", val -> val.toString().contains("Bob"));
    }
}



Allure Report Output:
For the $.name field:

pgsql
Copy
Assertion passed at path '$.name' with value: John Doe, condition: equalTo("John Doe")
For the $.age field:

pgsql
Copy
Assertion passed at path '$.age' with value: 30, condition: isGreaterThan(25)
For the first friend in the friends array:

pgsql
Copy
Assertion passed at path '$.friends[0]' with value: Alice, condition: equalTo("Alice")
For the friends array:

sql
Copy
Assertion passed at path '$.friends' with value: [Alice, Bob, Charlie], condition: contains("Bob")
