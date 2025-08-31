package com.davidp.chessjourney.application.service;

import com.davidp.chessjourney.application.config.AppProperties;
import com.davidp.chessjourney.domain.lichess.LichessUser;
import com.davidp.chessjourney.domain.lichess.LichessPreferences;
import com.davidp.chessjourney.domain.lichess.LichessStats;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Implementation of LichessService using Java HTTP client.
 * Makes REST API calls to Lichess for user account data.
 */
public class LichessServiceImpl implements LichessService {

  private static final String LICHESS_API_BASE = "https://lichess.org/api";
  private static final String ACCOUNT_ENDPOINT = "/account";
  
  private final HttpClient httpClient;
  private final AppProperties appProperties;

  public LichessServiceImpl() {
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    this.appProperties = AppProperties.getInstance();
  }

  @Override
  public Optional<LichessUser> getCurrentUser(String accessToken) {
    if (accessToken == null || accessToken.trim().isEmpty()) {
      System.out.println("⚠️ No Lichess access token provided");
      return Optional.empty();
    }

    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(LICHESS_API_BASE + ACCOUNT_ENDPOINT))
          .header("Authorization", "Bearer " + accessToken)
          .header("Accept", "application/json")
          .timeout(Duration.ofSeconds(30))
          .GET()
          .build();

      HttpResponse<String> response = httpClient.send(request, 
          HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        return Optional.of(parseUserResponse(response.body()));
      } else {
        System.err.println("⚠️ Lichess API error: " + response.statusCode() + " " + response.body());
        return Optional.empty();
      }
      
    } catch (IOException | InterruptedException e) {
      System.err.println("⚠️ Error calling Lichess API: " + e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public boolean isLichessAvailable() {
    String token = appProperties.getLichessAccessToken();
    return token != null && !token.trim().isEmpty();
  }

  /**
   * Simple JSON parsing for Lichess user response.
   * Note: This is a basic implementation. In a production app, 
   * you would use a proper JSON library like Jackson or Gson.
   */
  private LichessUser parseUserResponse(String jsonResponse) {
    LichessUser user = new LichessUser();
    
    // Extract basic fields using simple string parsing
    // This is simplified - in production use a proper JSON parser
    try {
      user.setId(extractJsonStringValue(jsonResponse, "id"));
      user.setUsername(extractJsonStringValue(jsonResponse, "username"));
      user.setEmail(extractJsonStringValue(jsonResponse, "email"));
      
      // For simplicity, create basic stats and prefs objects
      LichessStats stats = new LichessStats();
      if (jsonResponse.contains("\"count\"")) {
        String countSection = extractJsonSection(jsonResponse, "count");
        if (countSection != null) {
          stats.setAll(extractJsonIntValue(countSection, "all"));
          stats.setRated(extractJsonIntValue(countSection, "rated"));
          stats.setWin(extractJsonIntValue(countSection, "win"));
          stats.setLoss(extractJsonIntValue(countSection, "loss"));
          stats.setDraw(extractJsonIntValue(countSection, "draw"));
        }
      }
      user.setCount(stats);
      
      LichessPreferences prefs = new LichessPreferences();
      if (jsonResponse.contains("\"prefs\"")) {
        String prefsSection = extractJsonSection(jsonResponse, "prefs");
        if (prefsSection != null) {
          prefs.setDark(extractJsonBooleanValue(prefsSection, "dark"));
          prefs.setTheme(extractJsonStringValue(prefsSection, "theme"));
          prefs.setPieceSet(extractJsonStringValue(prefsSection, "pieceSet"));
        }
      }
      user.setPrefs(prefs);
      
    } catch (Exception e) {
      System.err.println("⚠️ Error parsing Lichess response: " + e.getMessage());
      // Return basic user with just username if possible
      user.setUsername(extractJsonStringValue(jsonResponse, "username"));
    }
    
    return user;
  }

  private String extractJsonStringValue(String json, String key) {
    String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*?)\"";
    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
    java.util.regex.Matcher m = p.matcher(json);
    return m.find() ? m.group(1) : null;
  }

  private int extractJsonIntValue(String json, String key) {
    String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
    java.util.regex.Matcher m = p.matcher(json);
    return m.find() ? Integer.parseInt(m.group(1)) : 0;
  }

  private boolean extractJsonBooleanValue(String json, String key) {
    String pattern = "\"" + key + "\"\\s*:\\s*(true|false)";
    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
    java.util.regex.Matcher m = p.matcher(json);
    return m.find() && "true".equals(m.group(1));
  }

  private String extractJsonSection(String json, String key) {
    String pattern = "\"" + key + "\"\\s*:\\s*\\{([^}]+)\\}";
    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
    java.util.regex.Matcher m = p.matcher(json);
    return m.find() ? m.group(1) : null;
  }
}