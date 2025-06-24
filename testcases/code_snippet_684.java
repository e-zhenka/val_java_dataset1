public static Response getErrorResponse(Throwable e, Response.Status status) {
        String message = e.getMessage() == null ? "Failed with " + e.getClass().getName() : e.getMessage();
        Response response = getErrorResponse(message, status);

        return response;
    }