public static HttpURLConnection getURLConnection(String url)
      throws IOException {
    URLConnection conn = new URL(url).openConnection();
    final HttpURLConnection httpConn = (HttpURLConnection) conn;

    // take care of https stuff - most of the time it's only needed to
    // secure client/server comm
    // not to establish the identity of the server
    if (httpConn instanceof HttpsURLConnection) {
      HttpsURLConnection httpsConn = (HttpsURLConnection) httpConn;
      httpsConn.setSSLSocketFactory(
          TrustAllSslSocketFactory.createSSLSocketFactory());
      httpsConn.setHostnameVerifier((arg0, arg1) -> true);
    }

    return httpConn;
  }