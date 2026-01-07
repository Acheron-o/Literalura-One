package com.alura.literalura.service;

import com.alura.literalura.exception.BookVerseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Service for making HTTP API calls to external services.
 * Provides a clean interface for REST API communication with proper error handling.
 */
@Service
public class ApiClientService {
    
    private final HttpClient httpClient;
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final String USER_AGENT = "BookVerse/1.0 (Java Library Management System)";

    public ApiClientService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
            .build();
    }

    /**
     * Fetches data from the specified URL using GET request.
     * @param url The URL to fetch data from
     * @return The response body as a string
     * @throws BookVerseException if the request fails or returns non-200 status
     */
    public String fetchData(String url) throws BookVerseException {
        return fetchData(url, null);
    }

    /**
     * Fetches data from the specified URL using GET request with custom headers.
     * @param url The URL to fetch data from
     * @param headers Additional headers to include in the request
     * @return The response body as a string
     * @throws BookVerseException if the request fails or returns non-200 status
     */
    public String fetchData(String url, Map<String, String> headers) throws BookVerseException {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");

            // Add custom headers if provided
            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }

            HttpRequest request = requestBuilder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new BookVerseException("HTTP request failed with status code: " + response.statusCode() + 
                                           " and message: " + response.body());
            }
        } catch (IOException e) {
            throw new BookVerseException("Network error while fetching data from " + url + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BookVerseException("Request interrupted while fetching data from " + url, e);
        } catch (IllegalArgumentException e) {
            throw new BookVerseException("Invalid URL: " + url, e);
        }
    }

    /**
     * Posts data to the specified URL.
     * @param url The URL to post data to
     * @param requestBody The request body as a string
     * @return The response body as a string
     * @throws BookVerseException if the request fails or returns non-200 status
     */
    public String postData(String url, String requestBody) throws BookVerseException {
        return postData(url, requestBody, null);
    }

    /**
     * Posts data to the specified URL with custom headers.
     * @param url The URL to post data to
     * @param requestBody The request body as a string
     * @param headers Additional headers to include in the request
     * @return The response body as a string
     * @throws BookVerseException if the request fails or returns non-200 status
     */
    public String postData(String url, String requestBody, Map<String, String> headers) throws BookVerseException {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                .header("User-Agent", USER_AGENT)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");

            // Add custom headers if provided
            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }

            HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return response.body();
            } else {
                throw new BookVerseException("HTTP POST request failed with status code: " + response.statusCode() + 
                                           " and message: " + response.body());
            }
        } catch (IOException e) {
            throw new BookVerseException("Network error while posting data to " + url + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BookVerseException("Request interrupted while posting data to " + url, e);
        } catch (IllegalArgumentException e) {
            throw new BookVerseException("Invalid URL: " + url, e);
        }
    }

    /**
     * Performs a health check on the specified URL.
     * @param url The URL to check
     * @return true if the URL is accessible and returns a 2xx status code
     */
    public boolean isUrlHealthy(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("User-Agent", USER_AGENT)
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discard());
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the response headers from a URL.
     * @param url The URL to check
     * @return Map of response headers
     * @throws BookVerseException if the request fails
     */
    public Map<String, String> getResponseHeaders(String url) throws BookVerseException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                .header("User-Agent", USER_AGENT)
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discard());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.headers().map();
            } else {
                throw new BookVerseException("Failed to get headers. Status code: " + response.statusCode());
            }
        } catch (IOException e) {
            throw new BookVerseException("Network error while getting headers from " + url + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BookVerseException("Request interrupted while getting headers from " + url, e);
        }
    }
}
