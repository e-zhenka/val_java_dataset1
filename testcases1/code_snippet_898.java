@Test(description = "Test pullCount of a package from central", dependsOnMethods = "testPull", enabled = false)
    public void testPullCount() throws IOException {
        String url = RepoUtils.getRemoteRepoURL() + "/modules/info/" + orgName + "/" + moduleName + "/*/";
        HttpsURLConnection conn = createHttpsUrlConnection(convertToUrl(url), "", 0, "", "");
        conn.setInstanceFollowRedirects(false);
        setRequestMethod(conn, Utils.RequestMethod.GET);

        int statusCode = conn.getResponseCode();
        if (statusCode == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),
                    Charset.defaultCharset()))) {
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                Object payload = JSONParser.parse(result.toString());
                if (payload instanceof MapValue) {
                    long pullCount = ((MapValue) payload).getIntValue("totalPullCount");
                    Assert.assertEquals(pullCount, totalPullCount);
                } else {
                    Assert.fail("error: invalid response received");
                }
            }
        } else {
            Assert.fail("error: could not connect to remote repository to find the latest version of module");
        }
    }