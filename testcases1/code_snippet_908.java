@Override
    public void showWebPage(String url, boolean openExternal, boolean clearHistory, Map<String, Object> params) {
        LOG.d(TAG, "showWebPage(%s, %b, %b, HashMap)", url, openExternal, clearHistory);

        // If clearing history
        if (clearHistory) {
            engine.clearHistory();
        }

        // If loading into our webview
        if (!openExternal) {
            // Make sure url is in whitelist
            if (pluginManager.shouldAllowNavigation(url)) {
                // TODO: What about params?
                // Load new URL
                loadUrlIntoView(url, true);
            } else {
                LOG.w(TAG, "showWebPage: Refusing to load URL into webview since it is not in the <allow-navigation> whitelist. URL=" + url);
            }
        }
        if (!pluginManager.shouldOpenExternalUrl(url)) {
            LOG.w(TAG, "showWebPage: Refusing to send intent for URL since it is not in the <allow-intent> whitelist. URL=" + url);
            return;
        }
        try {
            // Omitting the MIME type for file: URLs causes "No Activity found to handle Intent".
            // Adding the MIME type to http: URLs causes them to not be handled by the downloader.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(url);
            if ("file".equals(uri.getScheme())) {
                intent.setDataAndType(uri, resourceApi.getMimeType(uri));
            } else {
                intent.setData(uri);
            }
            cordova.getActivity().startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            LOG.e(TAG, "Error loading url " + url, e);
        }
    }