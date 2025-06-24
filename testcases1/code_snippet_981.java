public static HttpURLConnection getURLConnection(String url)
      throws IOException {
    return (HttpURLConnection) new URL(url).openConnection();
  }