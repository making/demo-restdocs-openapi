package com.example.demo.order;

import com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper;
import com.example.demo.DemoRestdocsOpenapiApplication;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.resourceDetails;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@ContextConfiguration(classes = DemoRestdocsOpenapiApplication.class)
public class OrderControllerTest {
	@LocalServerPort
	private int port;
	private RequestSpecification documentationSpec;
	@Value("${restdoc.scheme:http}")
	private String restdocScheme;
	@Value("${restdoc.host:localhost}")
	private String restdocHost;
	@Value("${restdoc.port:8080}")
	private int restdocPort;
	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@Before
	public void setUp() {
		this.documentationSpec = new RequestSpecBuilder()
				.addFilter(documentationConfiguration(this.restDocumentation)).build();
	}

	@Test
	public void getOrder() {
		JsonNode body = given(this.documentationSpec)
				.filter(RestAssuredRestDocumentationWrapper.document("get-order",
						resourceDetails().description("Get an order"), //
						preprocessRequest(modifyUris() //
								.scheme(this.restdocScheme) //
								.host(this.restdocHost) //
								.port(this.restdocPort)), //
						responseFields( //
								fieldWithPath("id").description("Id of the order"), //
								fieldWithPath("status")
										.description("Status of the order")))) //
				.when() //
				.port(this.port) //
				.get("/orders/{orderId}", 100) //
				.then() //
				.assertThat().statusCode(is(200)) //
				.extract().as(JsonNode.class);

		assertThat(body.has("id")).isTrue();
		assertThat(body.get("id").asLong()).isEqualTo(100);
		assertThat(body.has("status")).isTrue();
		assertThat(body.get("status").asText()).isEqualTo("COMPLETED");
	}

}
