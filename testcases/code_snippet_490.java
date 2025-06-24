public R execute(HttpResponseProcessor<R> responseProcessor) {
        byte[] bytes = null;

        try {
            for (Map.Entry<String, String> header : this.headers.entrySet()) {
                this.builder.setHeader(header.getKey(), header.getValue());
            }

            preExecute(this.builder);

            HttpResponse response = this.httpClient.execute(this.builder.build());
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                bytes = EntityUtils.toByteArray(entity);
            }

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode < 200 || statusCode >= 300) {
                throw new HttpResponseException("Unexpected response from server: " + statusCode + " / " + statusLine.getReasonPhrase(), statusCode, statusLine.getReasonPhrase(), bytes);
            }

            if (bytes == null) {
                return null;
            }

            return responseProcessor.process(bytes);
        } catch (HttpResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error executing http method [" + builder + "]. Response : " + String.valueOf(bytes), e);
        }
    }