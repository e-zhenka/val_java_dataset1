private boolean exractAndLoad(ArrayList<String> errors, String version, String customPath, String resourcePath) {
        URL resource = classLoader.getResource(resourcePath);
        if( resource !=null ) {
            
            String libName = name + "-" + getBitModel();
            if( version !=null) {
                libName += "-" + version;
            }
            
            if( customPath!=null ) {
                // Try to extract it to the custom path...
                File target = file(customPath, map(libName));
                if( extract(errors, resource, target) ) {
                    if( load(errors, target) ) {
                        return true;
                    }
                }
            }
            
            // Fall back to extracting to the tmp dir
            customPath = System.getProperty("java.io.tmpdir");
            File target = file(customPath, map(libName));
            if( extract(errors, resource, target) ) {
                if( load(errors, target) ) {
                    return true;
                }
            }
        }
        return false;
    }