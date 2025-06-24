public static Document parse(LSInput source)
    {
        try {
            LSParser p = LS_IMPL.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
            // Disable validation, since this takes a lot of time and causes unneeded network traffic
            p.getDomConfig().setParameter("validate", false);
            if (p.getDomConfig().canSetParameter(DISABLE_DTD_PARAM, false)) {
                p.getDomConfig().setParameter(DISABLE_DTD_PARAM, false);
            }

            // Avoid XML eXternal Entity injection (XXE)
            if (p.getDomConfig().canSetParameter(DISABLE_EXTERNAL_DOCTYPE_DECLARATION, false)) {
                p.getDomConfig().setParameter(DISABLE_EXTERNAL_DOCTYPE_DECLARATION, false);
            }
            if (p.getDomConfig().canSetParameter(DISABLE_EXTERNAL_PARAMETER_ENTITIES, false)) {
                p.getDomConfig().setParameter(DISABLE_EXTERNAL_PARAMETER_ENTITIES, false);
            }
            if (p.getDomConfig().canSetParameter(DISABLE_EXTERNAL_GENERAL_ENTITIES, false)) {
                p.getDomConfig().setParameter(DISABLE_EXTERNAL_GENERAL_ENTITIES, false);
            }
            return p.parse(source);
        } catch (Exception ex) {
            LOGGER.warn("Cannot parse XML document: [{}]", ex.getMessage());
            return null;
        }
    }