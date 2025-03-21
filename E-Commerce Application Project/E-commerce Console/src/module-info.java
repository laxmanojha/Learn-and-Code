/**
 * 
 */
/**
 * 
 */
module App {
	requires java.net.http;
	requires com.google.gson;
	
	opens com.itt.ecommerce.dto to com.google.gson;
}