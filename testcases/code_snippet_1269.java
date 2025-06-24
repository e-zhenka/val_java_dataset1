public void register(ContainerBuilder builder, LocatableProperties props) {
        alias(ObjectFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY, builder, props);
        alias(FileManagerFactory.class, StrutsConstants.STRUTS_FILE_MANAGER_FACTORY, builder, props, Scope.SINGLETON);

        alias(XWorkConverter.class, StrutsConstants.STRUTS_XWORKCONVERTER, builder, props);
        alias(CollectionConverter.class, StrutsConstants.STRUTS_CONVERTER_COLLECTION, builder, props);
        alias(ArrayConverter.class, StrutsConstants.STRUTS_CONVERTER_ARRAY, builder, props);
        alias(DateConverter.class, StrutsConstants.STRUTS_CONVERTER_DATE, builder, props);
        alias(NumberConverter.class, StrutsConstants.STRUTS_CONVERTER_NUMBER, builder, props);
        alias(StringConverter.class, StrutsConstants.STRUTS_CONVERTER_STRING, builder, props);

        alias(ConversionPropertiesProcessor.class, StrutsConstants.STRUTS_CONVERTER_PROPERTIES_PROCESSOR, builder, props);
        alias(ConversionFileProcessor.class, StrutsConstants.STRUTS_CONVERTER_FILE_PROCESSOR, builder, props);
        alias(ConversionAnnotationProcessor.class, StrutsConstants.STRUTS_CONVERTER_ANNOTATION_PROCESSOR, builder, props);
        alias(TypeConverterCreator.class, StrutsConstants.STRUTS_CONVERTER_CREATOR, builder, props);
        alias(TypeConverterHolder.class, StrutsConstants.STRUTS_CONVERTER_HOLDER, builder, props);

        alias(TextProvider.class, StrutsConstants.STRUTS_XWORKTEXTPROVIDER, builder, props, Scope.DEFAULT);

        alias(LocaleProvider.class, StrutsConstants.STRUTS_LOCALE_PROVIDER, builder, props);
        alias(ActionProxyFactory.class, StrutsConstants.STRUTS_ACTIONPROXYFACTORY, builder, props);
        alias(ObjectTypeDeterminer.class, StrutsConstants.STRUTS_OBJECTTYPEDETERMINER, builder, props);
        alias(ActionMapper.class, StrutsConstants.STRUTS_MAPPER_CLASS, builder, props);
        alias(MultiPartRequest.class, StrutsConstants.STRUTS_MULTIPART_PARSER, builder, props, Scope.DEFAULT);
        alias(FreemarkerManager.class, StrutsConstants.STRUTS_FREEMARKER_MANAGER_CLASSNAME, builder, props);
        alias(VelocityManager.class, StrutsConstants.STRUTS_VELOCITY_MANAGER_CLASSNAME, builder, props);
        alias(UrlRenderer.class, StrutsConstants.STRUTS_URL_RENDERER, builder, props);
        alias(ActionValidatorManager.class, StrutsConstants.STRUTS_ACTIONVALIDATORMANAGER, builder, props);
        alias(ValueStackFactory.class, StrutsConstants.STRUTS_VALUESTACKFACTORY, builder, props);
        alias(ReflectionProvider.class, StrutsConstants.STRUTS_REFLECTIONPROVIDER, builder, props);
        alias(ReflectionContextFactory.class, StrutsConstants.STRUTS_REFLECTIONCONTEXTFACTORY, builder, props);
        alias(PatternMatcher.class, StrutsConstants.STRUTS_PATTERNMATCHER, builder, props);
        alias(StaticContentLoader.class, StrutsConstants.STRUTS_STATIC_CONTENT_LOADER, builder, props);
        alias(UnknownHandlerManager.class, StrutsConstants.STRUTS_UNKNOWN_HANDLER_MANAGER, builder, props);
        alias(UrlHelper.class, StrutsConstants.STRUTS_URL_HELPER, builder, props);

        alias(TextParser.class, StrutsConstants.STRUTS_EXPRESSION_PARSER, builder, props);

        if ("true".equalsIgnoreCase(props.getProperty(StrutsConstants.STRUTS_DEVMODE))) {
            props.setProperty(StrutsConstants.STRUTS_I18N_RELOAD, "true");
            props.setProperty(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, "true");
            props.setProperty(StrutsConstants.STRUTS_FREEMARKER_TEMPLATES_CACHE, "false");
            props.setProperty(StrutsConstants.STRUTS_FREEMARKER_TEMPLATES_CACHE_UPDATE_DELAY, "0");
            // Convert struts properties into ones that xwork expects
            props.setProperty(XWorkConstants.DEV_MODE, "true");
        } else {
            props.setProperty(XWorkConstants.DEV_MODE, "false");
        }

        // Convert Struts properties into XWork properties
        convertIfExist(props, StrutsConstants.STRUTS_LOG_MISSING_PROPERTIES, XWorkConstants.LOG_MISSING_PROPERTIES);
        convertIfExist(props, StrutsConstants.STRUTS_ENABLE_OGNL_EXPRESSION_CACHE, XWorkConstants.ENABLE_OGNL_EXPRESSION_CACHE);
        convertIfExist(props, StrutsConstants.STRUTS_ALLOW_STATIC_METHOD_ACCESS, XWorkConstants.ALLOW_STATIC_METHOD_ACCESS);
        convertIfExist(props, StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, XWorkConstants.RELOAD_XML_CONFIGURATION);

        LocalizedTextUtil.addDefaultResourceBundle("org/apache/struts2/struts-messages");
        loadCustomResourceBundles(props);
    }