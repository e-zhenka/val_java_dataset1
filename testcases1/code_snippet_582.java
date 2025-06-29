@Override
    protected void configureDataFormat(DataFormat dataFormat, CamelContext camelContext) {
        if (objectMapper != null) {
            // must be a reference value
            String ref = objectMapper.startsWith("#") ? objectMapper : "#" + objectMapper;
            setProperty(camelContext, dataFormat, "objectMapper", ref);
        }
        if (unmarshalType != null) {
            setProperty(camelContext, dataFormat, "unmarshalType", unmarshalType);
        }
        if (prettyPrint != null) {
            setProperty(camelContext, dataFormat, "prettyPrint", prettyPrint);
        }
        if (jsonView != null) {
            setProperty(camelContext, dataFormat, "jsonView", jsonView);
        }
        if (include != null) {
            setProperty(camelContext, dataFormat, "include", include);
        }
        if (allowJmsType != null) {
            setProperty(camelContext, dataFormat, "allowJmsType", allowJmsType);
        }
        if (collectionType != null) {
            setProperty(camelContext, dataFormat, "collectionType", collectionType);
        }
        if (useList != null) {
            setProperty(camelContext, dataFormat, "useList", useList);
        }
        if (enableJaxbAnnotationModule != null) {
            setProperty(camelContext, dataFormat, "enableJaxbAnnotationModule", enableJaxbAnnotationModule);
        }
        if (moduleClassNames != null) {
            setProperty(camelContext, dataFormat, "moduleClassNames", moduleClassNames);
        }
        if (moduleRefs != null) {
            setProperty(camelContext, dataFormat, "moduleRefs", moduleRefs);
        }
        if (enableFeatures != null) {
            setProperty(camelContext, dataFormat, "enableFeatures", enableFeatures);
        }
        if (disableFeatures != null) {
            setProperty(camelContext, dataFormat, "disableFeatures", disableFeatures);
        }
        if (permissions != null) {
            setProperty(camelContext, dataFormat, "permissions", permissions);
        }
        // if we have the unmarshal type, but no permission set, then use it to be allowed
        if (permissions == null && unmarshalType != null) {
            String allow = "+" + unmarshalType.getName();
            setProperty(camelContext, dataFormat, "permissions", allow);
        }
    }