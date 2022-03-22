import constants.Constants;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static constants.Constants.TOKEN;
import static constants.Constants.UserData.*;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class GorestTest
{
    private final String ENDPOINT = "https://gorest.co.in/public/v1/users";

    @Test
    public void testGetUserIds()
    {
        Response response = get(ENDPOINT);
        response.jsonPath().get("$.id");
        List<Integer> idList = response.jsonPath().getList("data.id");
        idList.forEach(i -> {
            Assertions.assertNotNull(i);
            Assertions.assertEquals(4, (int) ((Math.log10(i) + 1)));
        });
    }

    @Test
    public void testPostUserData()
    {
        JSONObject requestParams = createObject(EMAIL);

        given().
                body(requestParams.toString()).
                headers("Authorization",
                        "Bearer " + TOKEN,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON).
        when().
                post(ENDPOINT).
        then().
                statusCode(201).
        assertThat().
                body("data.email", equalTo(EMAIL)).
                body("data.name", equalTo(NAME)).
                body("data.gender", equalTo(GENDER)).
                body("data.status", equalTo(STATUS));
    }

    @Test
    public void testPostSameUserData()
    {
        String email  = (String) get(ENDPOINT).jsonPath().getList("data.email").get(0);
        JSONObject requestParams = createObject(email);

        given().
                body(requestParams.toString()).
                headers("Authorization",
                        "Bearer " + TOKEN,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON).
        when().
                post(ENDPOINT).
        then().
                statusCode(422).
        assertThat().
                body("data.message", contains(Constants.Warning.MESSAGE));
    }

    private JSONObject createObject(String email)
    {
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", email);
        requestParams.put("name", NAME);
        requestParams.put("gender", GENDER);
        requestParams.put("status", STATUS);
        return requestParams;
    }
}
