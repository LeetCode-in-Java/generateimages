package generateimages;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ImageDownloader {

    public static void downloadImage(String imageUrl, String destinationFile) throws IOException, InterruptedException {
        // Create an HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Create an HttpRequest for the image URL
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageUrl))
                .build();

        // Send the request and receive the response
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        // Get the InputStream from the response and write it to a file
        Path path = Paths.get(destinationFile);
        try (InputStream inputStream = response.body()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
