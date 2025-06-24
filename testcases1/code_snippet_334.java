public ResponseEntity<Void> postForRedirect(String path, HttpHeaders headers, MultiValueMap<String, String> params) {
        ResponseEntity<Void> exchange = postForResponse(path, headers, params);

        if (exchange.getStatusCode() != HttpStatus.FOUND) {
            throw new IllegalStateException("Expected 302 but server returned status code " + exchange.getStatusCode());
        }

        if (exchange.getHeaders().containsKey("Set-Cookie")) {
            String cookie = exchange.getHeaders().getFirst("Set-Cookie");
            headers.set("Cookie", cookie);
        }

        String location = exchange.getHeaders().getLocation().toString();

        return client.exchange(location, HttpMethod.GET, new HttpEntity<Void>(null, headers), Void.class);
    }