package com.example.demo.order;

import java.util.LinkedHashMap;

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
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.operation.preprocess.UriModifyingOperationPreprocessor;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.resourceDetails;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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

	UriModifyingOperationPreprocessor apiUrl() {
		return modifyUris() //
				.scheme(this.restdocScheme) //
				.host(this.restdocHost) //
				.port(this.restdocPort);
	}

	ResponseFieldsSnippet orderResponseFields() {
		return responseFields( //
				fieldWithPath("id").description("Id of the order"), //
				fieldWithPath("email").description("Order's email address"), //
				fieldWithPath("totalPrice").description("Total price of the order "), //
				fieldWithPath("status").description("Status of the order"));
	}

	RequestFieldsSnippet orderRequestFields() {
		return requestFields( //
				fieldWithPath("email").description("Order's email address"), //
				fieldWithPath("totalPrice").description("Total price of the order "));
	}

	@Test
	public void getOrder() {
		JsonNode body = given(this.documentationSpec)
				.filter(RestAssuredRestDocumentationWrapper.document("get-order",
						resourceDetails().description("Get an order"), //
						preprocessRequest(this.apiUrl()), //
						this.orderResponseFields())) //
				.log().all() //
				.when() //
				.port(this.port) //
				.get("/orders/{orderId}", 100) //
				.then() //
				.log().all() //
				.assertThat().statusCode(is(200)) //
				.extract().as(JsonNode.class);

		assertThat(body.has("id")).isTrue();
		assertThat(body.get("id").asLong()).isEqualTo(100);
		assertThat(body.has("email")).isTrue();
		assertThat(body.get("email").asText()).isEqualTo("demo@example.com");
		assertThat(body.has("totalPrice")).isTrue();
		assertThat(body.get("totalPrice").asLong()).isEqualTo(10000);
		assertThat(body.has("status")).isTrue();
		assertThat(body.get("status").asText()).isEqualTo("COMPLETED");
	}

	@Test
	public void postOrder() {
		JsonNode body = given(this.documentationSpec)
				.filter(RestAssuredRestDocumentationWrapper.document("post-order",
						resourceDetails().description("Post an order"), //
						preprocessRequest(this.apiUrl()), //
						this.orderRequestFields(), //
						this.orderResponseFields())) //
				.log().all() //
				.when() //
				.port(this.port) //
				.contentType(MediaType.APPLICATION_JSON_VALUE) //
				.body(new LinkedHashMap<String, Object>() {
					{
						put("email", "foo@example.com");
						put("totalPrice", 500);
					}
				}) //
				.post("/orders") //
				.then() //
				.log().all() //
				.assertThat().statusCode(is(201)) //
				.header(LOCATION,
						response -> equalTo(
								String.format("http://localhost:%d/orders/%d", this.port,
										response.as(JsonNode.class).get("id").asLong()))) //
				.extract().as(JsonNode.class);

		assertThat(body.has("id")).isTrue();
		assertThat(body.get("id").asLong()).isGreaterThanOrEqualTo(1);
		assertThat(body.has("email")).isTrue();
		assertThat(body.get("email").asText()).isEqualTo("foo@example.com");
		assertThat(body.has("totalPrice")).isTrue();
		assertThat(body.get("totalPrice").asLong()).isEqualTo(500);
		assertThat(body.has("status")).isTrue();
		assertThat(body.get("status").asText()).isEqualTo("SUBMITTED");
	}
}
