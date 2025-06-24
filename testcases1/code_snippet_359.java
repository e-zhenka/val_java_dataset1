public String findFilter( String url_suffix )
    {
        if( url_suffix == null )
        {
            throw new IllegalArgumentException( "The url_suffix must not be null." );
        }
        
        CaptureType type = em.find( CaptureType.class, url_suffix );
        
        if( type != null )
        {
            return type.getCaptureFilter();
        }
        
        return null;
    }