public C3P0Config findConfig() throws Exception
    {
	C3P0Config out;

	HashMap flatDefaults = C3P0ConfigUtils.extractHardcodedC3P0Defaults();

	// this includes System properties, but we have to check for System properties
	// again, since we want system properties to override unspecified user, default-config
	// properties in the XML
	flatDefaults.putAll( C3P0ConfigUtils.extractC3P0PropertiesResources() );

	String cfgFile = C3P0Config.getPropsFileConfigProperty( XML_CFG_FILE_KEY );
	boolean usePermissiveParser = findUsePermissiveParser();
	
	if (cfgFile == null)
	    {
		C3P0Config xmlConfig = C3P0ConfigXmlUtils.extractXmlConfigFromDefaultResource( usePermissiveParser );
		if (xmlConfig != null)
		    {
			insertDefaultsUnderNascentConfig( flatDefaults, xmlConfig );
			out = xmlConfig;
			
			mbOverrideWarning("resource", C3P0ConfigXmlUtils.XML_CONFIG_RSRC_PATH);
		    }
		else
		    out = C3P0ConfigUtils.configFromFlatDefaults( flatDefaults );
	    }
	else
	    {
		cfgFile = cfgFile.trim();

		InputStream is = null;
		try
		    {
			if ( cfgFile.startsWith( CLASSLOADER_RESOURCE_PREFIX ) )
			{
			    ClassLoader cl = this.getClass().getClassLoader();
			    String rsrcPath = cfgFile.substring( CLASSLOADER_RESOURCE_PREFIX.length() );

			    // eliminate leading slash because ClassLoader.getResource
			    // is always absolute and does not expect a leading slash
			    if (rsrcPath.startsWith("/")) 
				rsrcPath = rsrcPath.substring(1);

			    is = cl.getResourceAsStream( rsrcPath );
			    if ( is == null )
				throw new FileNotFoundException("Specified ClassLoader resource '" + rsrcPath + "' could not be found. " +
								"[ Found in configuration: " + XML_CFG_FILE_KEY + '=' + cfgFile + " ]");

			    mbOverrideWarning( "resource", rsrcPath );
			}
			else
			{
			    is = new BufferedInputStream( new FileInputStream( cfgFile ) );
			    mbOverrideWarning( "file", cfgFile );
			}

			C3P0Config xmlConfig = C3P0ConfigXmlUtils.extractXmlConfigFromInputStream( is, usePermissiveParser );
			insertDefaultsUnderNascentConfig( flatDefaults, xmlConfig );
			out = xmlConfig;
		    }
		finally
		    {
			try { if (is != null) is.close();}
			catch (Exception e)
			    { e.printStackTrace(); }
		    }
	    }

	// overwrite default, unspecified user config with System properties
	// defined values
	Properties sysPropConfig = C3P0ConfigUtils.findAllC3P0SystemProperties();
	out.defaultConfig.props.putAll( sysPropConfig );

	return out;
    }