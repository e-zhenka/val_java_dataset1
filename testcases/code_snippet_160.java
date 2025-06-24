private static FormValidation _error(Kind kind, Throwable e, String message) {
        if (e==null)    return _errorWithMarkup(Util.escape(message),kind);

        return _errorWithMarkup(Util.escape(message)+
            " <a href='#' class='showDetails'>"
            + Messages.FormValidation_Error_Details()
            + "</a><pre style='display:none'>"
            + Functions.printThrowable(e) +
            "</pre>",kind
        );
    }