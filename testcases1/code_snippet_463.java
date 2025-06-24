public void updateByXml(Source source) throws IOException {
        checkPermission(CONFIGURE);
        XmlFile configXmlFile = getConfigFile();
        AtomicFileWriter out = new AtomicFileWriter(configXmlFile.getFile());
        try {
            try {
                // this allows us to use UTF-8 for storing data,
                // plus it checks any well-formedness issue in the submitted
                // data
                Transformer t = TransformerFactory.newInstance()
                        .newTransformer();
                t.transform(source,
                        new StreamResult(out));
                out.close();
            } catch (TransformerException e) {
                throw new IOException2("Failed to persist configuration.xml", e);
            }

            // try to reflect the changes by reloading
            Object o = new XmlFile(Items.XSTREAM, out.getTemporaryFile()).unmarshal(this);
            if (o!=this) {
                // ensure that we've got the same job type. extending this code to support updating
                // to different job type requires destroying & creating a new job type
                throw new IOException("Expecting "+this.getClass()+" but got "+o.getClass()+" instead");
            }

            Items.updatingByXml.set(true);
            try {
                onLoad(getParent(), getRootDir().getName());
            } finally {
                Items.updatingByXml.set(false);
            }
            Jenkins.getInstance().rebuildDependencyGraphAsync();

            // if everything went well, commit this new version
            out.commit();
            SaveableListener.fireOnChange(this, getConfigFile());
        } finally {
            out.abort(); // don't leave anything behind
        }
    }