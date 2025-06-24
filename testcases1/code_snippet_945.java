private boolean exractAndLoad(ArrayList<String> errors, String version, String customPath, String resourcePath) {
        URL resource = classLoader.getResource(resourcePath);
        if( resource !=null ) {

            String libName = name + "-" + getBitModel();
            if( version !=null) {
                libName += "-" + version;
            }
            String []libNameParts = map(libName).split("\\.");
            String prefix = libNameParts[0]+"-";
            String suffix = "."+libNameParts[1];

            if( customPath!=null ) {
                // Try to extract it to the custom path...
                File target = extract(errors, resource, prefix, suffix, file(customPath));
                if( target!=null ) {
                    if( load(errors, target) ) {
                        return true;
                    }
                }
            }
            
            // Fall back to extracting to the tmp dir
            customPath = System.getProperty("java.io.tmpdir");
            File target = extract(errors, resource, prefix, suffix, file(customPath));
            if( target!=null ) {
                if( load(errors, target) ) {
                    return true;
                }
            }
        }
        return false;
    }