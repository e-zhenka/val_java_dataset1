public UploadFileResponse getCloudUrl(String contextPath, String uri, String finalFilePath, HttpServletRequest request) {
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        // try push to cloud
        Map<String, String[]> map = new HashMap<>();
        map.put("fileInfo", new String[]{finalFilePath + "," + uri});
        map.put("name", new String[]{"uploadService"});
        String url;
        try {
            List<Map> urls = HttpUtil.getInstance().sendGetRequest(Constants.pluginServer + "/service", map
                    , new HttpJsonArrayHandle<Map>(), PluginHelper.genHeaderMapByRequest(request, AdminTokenThreadLocal.getUser())).getT();
            if (urls != null && !urls.isEmpty()) {
                url = (String) urls.get(0).get("url");
                if (!url.startsWith("https://") && !url.startsWith("http://")) {
                    String tUrl = url;
                    if (!url.startsWith("/")) {
                        tUrl = "/" + url;
                    }
                    url = contextPath + tUrl;
                }
            } else {
                url = contextPath + uri;
            }
        } catch (Exception e) {
            url = contextPath + uri;
            LOGGER.error(e);
        }
        uploadFileResponse.setUrl(url);
        return uploadFileResponse;
    }