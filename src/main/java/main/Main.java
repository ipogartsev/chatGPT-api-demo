package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Main {

	public static void main(String[] args) {

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request;
		// the body of request is stored in request.json file
		Path jsonRequestBody = Path.of("src/main/resources/request.json");
		
		try {
			request = HttpRequest.newBuilder().uri(URI.create("https://api.openai.com/v1/chat/completions"))
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer %s".formatted(System.getenv("OPENAI_API_KEY")))
					.POST(BodyPublishers.ofFile(jsonRequestBody)).build();

			HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
			
			// Create file to store response
			Files.createDirectories(Path.of(".").toAbsolutePath().resolve("temp/response.json"));
			
			// if the response was successful (status code 200)
			if (response.statusCode() == 200) {
				try (InputStream responseBody = response.body();) {
					// Copy the response data to the output file
					Files.copy(responseBody, Path.of("temp/response.json"), StandardCopyOption.REPLACE_EXISTING);
					System.out.println("API response saved to: " + Path.of("temp/response.json").getFileName());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.err.println("API request failed with status code: " + response.statusCode());
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
