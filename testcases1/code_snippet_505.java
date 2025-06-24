public ResponseEntity<Void> postForRedirect(String path, HttpHeaders headers, MultiValueMap<String, String> params) {
        ResponseEntity<Void> exchange = postForResponse(path, headers, params);

        if (exchange.getStatusCode() != HttpStatus.FOUND) {
            throw new IllegalStateException("Expected 302 but server returned status code " + exchange.getStatusCode());
        }

        headers.remove("Cookie");
        if (exchange.getHeaders().containsKey("Set-Cookie")) {
            for (String cookie : exchange.getHeaders().get("Set-Cookie")) {
                headers.add("Cookie", cookie);
            }
        }

        String location = exchange.getHeaders().getLocation().toString();

        return client.exchange(location, HttpMethod.GET, new HttpEntity<Void>(null, headers), Void.class);
    }