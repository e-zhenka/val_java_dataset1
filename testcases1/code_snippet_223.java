@Override
    @Nonnull
    public String getValidHref(final String url) {
        if (StringUtils.isNotEmpty(url)) {
            // Percent-encode characters that are not allowed in unquoted
            // HTML attributes: ", ', >, <, ` and space. We don't encode =
            // since this would break links with query parameters.
            String encodedUrl = url.replaceAll("\"", "%22")
                    .replaceAll("'", "%27")
                    .replaceAll(">", "%3E")
                    .replaceAll("<", "%3C")
                    .replaceAll("`", "%60")
                    .replaceAll(" ", "%20");
            int qMarkIx = encodedUrl.indexOf('?');
            if (qMarkIx > 0) {
                encodedUrl = encodedUrl.substring(0, qMarkIx) + encodedUrl.substring(qMarkIx).replaceAll(":", "%3A");
            }
            encodedUrl = mangleNamespaces(encodedUrl);
            if (xssFilter.isValidHref(encodedUrl)) {
                return encodedUrl;
            }
        }
        // fall through to empty string
        return "";
    }