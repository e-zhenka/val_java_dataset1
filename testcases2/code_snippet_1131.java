public static HttpHost extractHost(final URI uri) {
        if (uri == null) {
            return null;
        }
        HttpHost target = null;
        if (uri.isAbsolute()) {
            int port = uri.getPort(); // may be overridden later
            String host = uri.getHost();
            if (host == null) { // normal parse failed; let's do it ourselves
                // authority does not seem to care about the valid character-set for host names
                host = uri.getAuthority();
                if (host != null) {
                    // Strip off any leading user credentials
                    final int at = host.indexOf('@');
                    if (at >= 0) {
                        if (host.length() > at+1 ) {
                            host = host.substring(at+1);
                        } else {
                            host = null; // @ on its own
                        }
                    }
                    // Extract the port suffix, if present
                    if (host != null) {
                        final int colon = host.indexOf(':');
                        if (colon >= 0) {
                            final int pos = colon + 1;
                            int len = 0;
                            for (int i = pos; i < host.length(); i++) {
                                if (Character.isDigit(host.charAt(i))) {
                                    len++;
                                } else {
                                    break;
                                }
                            }
                            if (len > 0) {
                                try {
                                    port = Integer.parseInt(host.substring(pos, pos + len));
                                } catch (final NumberFormatException ex) {
                                }
                            }
                            host = host.substring(0, colon);
                        }
                    }
                }
            }
            final String scheme = uri.getScheme();
            if (!TextUtils.isBlank(host)) {
                try {
                    target = new HttpHost(host, port, scheme);
                } catch (final IllegalArgumentException ignore) {
                }
            }
        }
        return target;
    }