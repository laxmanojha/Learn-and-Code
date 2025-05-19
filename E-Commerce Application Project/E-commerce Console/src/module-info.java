/**
 * 
 */
/**
 * 
 */
module App {
	requires java.net.http;
	requires com.google.gson;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotation;
	requires org.junit.jupiter.api;
	requires org.mockito;
	
	opens com.itt.ecommerce.dto to com.google.gson;
	exports com.itt.ecommerce.dto to com.fasterxml.jackson.databind;
}