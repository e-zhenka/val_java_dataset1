default URL findConfigFile( final String name ) {
        LogManager.getLogger( Engine.class ).info( "looking for " + name + " inside WEB-INF " );
        // Try creating an absolute path first
        File defaultFile = null;
        if( getRootPath() != null ) {
            defaultFile = new File( getRootPath() + "/WEB-INF/" + name );
        }
        if ( defaultFile != null && defaultFile.exists() ) {
            try {
                return defaultFile.toURI().toURL();
            } catch ( final MalformedURLException e ) {
                // Shouldn't happen, but log it if it does
                LogManager.getLogger( Engine.class ).warn( "Malformed URL: " + e.getMessage() );
            }
        }

        // Ok, the absolute path didn't work; try other methods
        URL path = null;

        if( getServletContext() != null ) {
            final File tmpFile;
            try {
                tmpFile = File.createTempFile( "temp." + name, "" );
            } catch( final IOException e ) {
                LogManager.getLogger( Engine.class ).error( "unable to create a temp file to load onto the policy", e );
                return null;
            }
            tmpFile.deleteOnExit();
            LogManager.getLogger( Engine.class ).info( "looking for /" + name + " on classpath" );
            //  create a tmp file of the policy loaded as an InputStream and return the URL to it
            try( final InputStream is = Engine.class.getResourceAsStream( "/" + name );
                    final OutputStream os = new FileOutputStream( tmpFile ) ) {
                if( is == null ) {
                    throw new FileNotFoundException( name + " not found" );
                }
                final URL url = getServletContext().getResource( "/WEB-INF/" + name );
                if( url != null ) {
                    return url;
                }

                final byte[] buff = new byte[1024];
                int bytes;
                while( ( bytes = is.read( buff ) ) != -1 ) {
                    os.write( buff, 0, bytes );
                }

                path = tmpFile.toURI().toURL();
            } catch( final MalformedURLException e ) {
                // This should never happen unless I screw up
                LogManager.getLogger( Engine.class ).fatal( "Your code is b0rked.  You are a bad person.", e );
            } catch( final IOException e ) {
                LogManager.getLogger( Engine.class ).error( "failed to load security policy from file " + name + ",stacktrace follows", e );
            }
        }
        return path;
    }