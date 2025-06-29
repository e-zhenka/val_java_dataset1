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
        setProperty(dataFormat, camelContext, "allowAnyType", allowAnyType);

        if (typeFilters != null && !typeFilters.isEmpty()) {
            List<String> typeFilterDefinitions = new ArrayList<>(typeFilters.size());
            for (YAMLTypeFilterDefinition definition : typeFilters) {
                String value = definition.getValue();

                if (!value.startsWith("type") && !value.startsWith("regexp")) {
                    YAMLTypeFilterType type = definition.getType();
                    if (type == null) {
                        type = YAMLTypeFilterType.type;
                    }

                    value = type.name() + ":" + value;
                }

                typeFilterDefinitions.add(value);
            }

            setProperty(dataFormat, camelContext, "typeFilterDefinitions", typeFilterDefinitions);
        }

        setPropertyRef(dataFormat, camelContext, "constructor", constructor);
        setPropertyRef(dataFormat, camelContext, "representer", representer);
        setPropertyRef(dataFormat, camelContext, "dumperOptions", dumperOptions);
        setPropertyRef(dataFormat, camelContext, "resolver", resolver);
    }