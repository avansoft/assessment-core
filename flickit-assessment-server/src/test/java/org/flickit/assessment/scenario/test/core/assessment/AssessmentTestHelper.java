package org.flickit.assessment.scenario.test.core.assessment;

import io.restassured.response.Response;
import org.flickit.assessment.core.adapter.in.rest.assessment.CreateAssessmentRequestDto;
import org.flickit.assessment.scenario.test.ScenarioContext;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@Component
public class AssessmentTestHelper {

    public Response create(ScenarioContext context, CreateAssessmentRequestDto request) {
        return given()
            .contentType(JSON)
            .auth().oauth2(context.getCurrentUser().getJwt())
            .body(request)
            .when()
            .post("/assessment-core/api/assessments")
            .then()
            .extract()
            .response();
    }
}
