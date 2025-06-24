private void testCreateReport(String requestPath, String printSpec) throws Exception {
        ClientHttpRequest request = getPrintRequest(requestPath, HttpMethod.POST);
        setPrintSpec(printSpec, request);
        final String ref;
        final String downloadUrl;
        try (ClientHttpResponse response = request.execute()) {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(getJsonMediaType(), response.getHeaders().getContentType());

            String responseAsText = getBodyAsText(response);
            JSONObject createResult = new JSONObject(responseAsText);

            ref = createResult.getString(MapPrinterServlet.JSON_PRINT_JOB_REF);
            String statusUrl = createResult.getString(MapPrinterServlet.JSON_STATUS_LINK);
            downloadUrl = createResult.getString(MapPrinterServlet.JSON_DOWNLOAD_LINK);
            assertEquals("/print/status/" + ref + ".json", statusUrl);
            assertEquals("/print/report/" + ref, downloadUrl);
        }

        // check status
        request = getPrintRequest(MapPrinterServlet.STATUS_URL + "/" + ref + ".json", HttpMethod.GET);
        try (ClientHttpResponse response = request.execute()) {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(getJsonMediaType(), response.getHeaders().getContentType());
            assertEquals("max-age=0, must-revalidate, no-cache, no-store",
                         String.join(", ", response.getHeaders().get("Cache-Control")));

            String responseAsText = getBodyAsText(response);
            JSONObject statusResult = new JSONObject(responseAsText);

            assertTrue(statusResult.has(MapPrinterServlet.JSON_DONE));
            assertEquals(downloadUrl, statusResult.getString(MapPrinterServlet.JSON_DOWNLOAD_LINK));
        }

        final boolean hasAppId = !requestPath.startsWith(MapPrinterServlet.REPORT_URL);
        String appId = null;
        if (hasAppId) {
            appId = requestPath.substring(0, requestPath.indexOf('/'));

            // app specific status option
            request = getPrintRequest(appId + MapPrinterServlet.STATUS_URL + "/" + ref + ".json",
                                      HttpMethod.GET);
            try (ClientHttpResponse response = request.execute()) {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(getJsonMediaType(), response.getHeaders().getContentType());

                String responseAsText = getBodyAsText(response);
                JSONObject statusResult = new JSONObject(responseAsText);

                assertTrue(statusResult.has(MapPrinterServlet.JSON_DONE));
                assertEquals(downloadUrl, statusResult.getString(MapPrinterServlet.JSON_DOWNLOAD_LINK));
            }
        }

        request = getPrintRequest(MapPrinterServlet.STATUS_URL + "/" + ref + ".json",
                                  HttpMethod.GET);
        try (ClientHttpResponse response = request.execute()) {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(getJsonMediaType(), response.getHeaders().getContentType());

            String responseAsText = getBodyAsText(response);
            JSONObject statusResult = new JSONObject(responseAsText);

            assertTrue(statusResult.has(MapPrinterServlet.JSON_DONE));
            assertEquals(downloadUrl, statusResult.getString(MapPrinterServlet.JSON_DOWNLOAD_LINK));
        }

        waitUntilDoneOrError(ref);

        // check download
        request = getPrintRequest(MapPrinterServlet.REPORT_URL + "/" + ref, HttpMethod.GET);
        try (ClientHttpResponse response = request.execute()) {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(new MediaType("application", "pdf"), response.getHeaders().getContentType());
            assertTrue(response.getBody().read() >= 0);
        }

        if (hasAppId) {
            // check download with appId url
            request = getPrintRequest("/" + appId + MapPrinterServlet.REPORT_URL + "/" + ref, HttpMethod.GET);
            try (ClientHttpResponse response = request.execute()) {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(new MediaType("application", "pdf"), response.getHeaders().getContentType());
                assertTrue(response.getBody().read() >= 0);
            }
        }
    }