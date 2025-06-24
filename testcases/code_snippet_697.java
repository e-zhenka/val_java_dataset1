public static Response getErrorResponse(Throwable e, Response.Status status) {
        String message = e.getMessage() == null ? "Failed with " + e.getClass().getName() : e.getMessage();
        Response response = getErrorResponse(message, status);
        JSONObject responseJson = (JSONObject) response.getEntity();
        try {
            responseJson.put(AtlasClient.STACKTRACE, printStackTrace(e));
        } catch (JSONException e1) {
            LOG.warn("Could not construct error Json rensponse", e1);
        }
        return response;
    }