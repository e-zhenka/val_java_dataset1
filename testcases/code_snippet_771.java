public String findFilter( String url_suffix )
    {
        if( url_suffix == null )
        {
            throw new IllegalArgumentException( "The url_suffix must not be null." );
        }
        
        CaptureType type = em.find( CaptureType.class, url_suffix );
        
        if( type == null )
        {
            throw new IllegalArgumentException( "The url_suffix must exist in the database." );
        }
        
        // It is okay for the capture filter itself to be null, but the CaptureType
        // must be in the database, otherwise the user could effectively forge
        // a capture filter for "all" just by requesting a non-existent filter.
        
        return type.getCaptureFilter();
    }