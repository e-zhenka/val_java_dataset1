protected InputStream findXsltInputStream(WebResource directory)
        throws IOException {

        if (localXsltFile != null) {
            WebResource resource = resources.getResource(
                    directory.getWebappPath() + localXsltFile);
            if (resource.isFile()) {
                InputStream is = resource.getInputStream();
                if (is != null) {
                    return is;
                }
            }
            if (debug > 10) {
                log("localXsltFile '" + localXsltFile + "' not found");
            }
        }

        if (contextXsltFile != null) {
            InputStream is =
                getServletContext().getResourceAsStream(contextXsltFile);
            if (is != null)
                return is;

            if (debug > 10)
                log("contextXsltFile '" + contextXsltFile + "' not found");
        }

        /*  Open and read in file in one fell swoop to reduce chance
         *  chance of leaving handle open.
         */
        if (globalXsltFile != null) {
            File f = validateGlobalXsltFile();
            if (f != null && f.exists()){
                try (FileInputStream fis = new FileInputStream(f)){
                    byte b[] = new byte[(int)f.length()]; /* danger! */
                    fis.read(b);
                    return new ByteArrayInputStream(b);
                }
            }
        }

        return null;
    }