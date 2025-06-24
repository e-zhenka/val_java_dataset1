public static HttpHost extractHost(final URI uri) {
        if (uri == null) {
            return null;
        }
        if (uri.isAbsolute()) {
            if (uri.getHost() == null) { // normal parse failed; let's do it ourselves
                // authority does not seem to care about the valid character-set for host names
                if (uri.getAuthority() != null) {
                    String content = uri.getAuthority();
                    // Strip off any leading user credentials
                    int at = content.indexOf('@');
                    if (at != -1) {
                        content = content.substring(at + 1);
                    }
                    final String scheme = uri.getScheme();
                    final String hostname;
                    final int port;
                    at = content.indexOf(":");
                    if (at != -1) {
                        hostname = content.substring(0, at);
                        try {
                            final String portText = content.substring(at + 1);
                            port = !TextUtils.isEmpty(portText) ? Integer.parseInt(portText) : -1;
                        } catch (final NumberFormatException ex) {
                            return null;
                        }
                    } else {
                        hostname = content;
                        port = -1;
                    }
                    try {
                        return new HttpHost(hostname, port, scheme);
                    } catch (final IllegalArgumentException ex) {
                        return null;
                    }
                }
            } else {
                return new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            }
        }
        return null;
    }