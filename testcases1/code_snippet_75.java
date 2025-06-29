@Override
    protected void configureDataFormat(DataFormat dataFormat, CamelContext camelContext) {
        if (encoding != null) {
            setProperty(camelContext, dataFormat, "encoding", encoding);
        }
        if (this.converters != null) {
            setProperty(camelContext, dataFormat, "converters", this.converters);
        }
        if (this.aliases != null) {
            setProperty(camelContext, dataFormat, "aliases", this.aliases);
        }
        if (this.omitFields != null) {
            setProperty(camelContext, dataFormat, "omitFields", this.omitFields);
        }
        if (this.implicitCollections != null) {
            setProperty(camelContext, dataFormat, "implicitCollections", this.implicitCollections);
        }
        if (this.mode != null) {
            setProperty(camelContext, dataFormat, "mode", mode);
        }
    }