protected void configureSnakeDataFormat(DataFormat dataFormat, CamelContext camelContext) {
        Class<?> yamlUnmarshalType =  unmarshalType;
        if (yamlUnmarshalType == null && unmarshalTypeName != null) {
            try {
                yamlUnmarshalType = camelContext.getClassResolver().resolveMandatoryClass(unmarshalTypeName);
            } catch (ClassNotFoundException e) {
                throw ObjectHelper.wrapRuntimeCamelException(e);
            }
        }

        setProperty(dataFormat, camelContext, "unmarshalType", yamlUnmarshalType);
        setProperty(dataFormat, camelContext, "classLoader", classLoader);
        setProperty(dataFormat, camelContext, "useApplicationContextClassLoader", useApplicationContextClassLoader);
        setProperty(dataFormat, camelContext, "prettyFlow", prettyFlow);

        setPropertyRef(dataFormat, camelContext, "constructor", constructor);
        setPropertyRef(dataFormat, camelContext, "representer", representer);
        setPropertyRef(dataFormat, camelContext, "dumperOptions", dumperOptions);
        setPropertyRef(dataFormat, camelContext, "resolver", resolver);
    }