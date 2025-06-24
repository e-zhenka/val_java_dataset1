public static File newTmpFile( final String content, final Charset encoding ) throws IOException {
        final File f = File.createTempFile( "jspwiki", null );
        try( final Reader in = new StringReader( content );
            final Writer out = new OutputStreamWriter( Files.newOutputStream( f.toPath() ), encoding ) ) {
            copyContents( in, out );
        }

        return f;
    }