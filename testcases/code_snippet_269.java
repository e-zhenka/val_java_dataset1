@RequirePOST
        public FormValidation doCheckUrl(@QueryParameter String value) {
            Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
            
            try {
                URLConnection conn = ProxyConfiguration.open(new URL(value));
                conn.connect();
                if (conn instanceof HttpURLConnection) {
                    if (((HttpURLConnection) conn).getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return FormValidation.error(Messages.ZipExtractionInstaller_bad_connection());
                    }
                }
                return FormValidation.ok();
            } catch (MalformedURLException x) {
                return FormValidation.error(Messages.ZipExtractionInstaller_malformed_url());
            } catch (IOException x) {
                return FormValidation.error(x,Messages.ZipExtractionInstaller_could_not_connect());
            }
        }