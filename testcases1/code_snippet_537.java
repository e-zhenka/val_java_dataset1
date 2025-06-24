public synchronized TopLevelItem createProjectFromXML(String name, InputStream xml) throws IOException {
        acl.checkPermission(Item.CREATE);

        Jenkins.getInstance().getProjectNamingStrategy().checkName(name);
        if (parent.getItem(name) != null) {
            throw new IllegalArgumentException(parent.getDisplayName() + " already contains an item '" + name + "'");
        }
        // TODO what if we have no DISCOVER permission on the existing job?

        // place it as config.xml
        File configXml = Items.getConfigFile(getRootDirFor(name)).getFile();
        final File dir = configXml.getParentFile();
        dir.mkdirs();
        final AtomicFileWriter out = new AtomicFileWriter(configXml);

        try {
            XMLUtils.safeTransform((Source)new StreamSource(xml), new StreamResult(out));
            out.close();
            out.commit();

            // load it
            TopLevelItem result = Items.whileUpdatingByXml(new NotReallyRoleSensitiveCallable<TopLevelItem,IOException>() {
                @Override public TopLevelItem call() throws IOException {
                    return (TopLevelItem) Items.load(parent, dir);
                }
            });
            add(result);

            ItemListener.fireOnCreated(result);
            Jenkins.getInstance().rebuildDependencyGraphAsync();

            return result;
        } catch (TransformerException e) {
            // if anything fails, delete the config file to avoid further confusion
            Util.deleteRecursive(dir);
            throw new IOException("Failed to persist config.xml", e);
        } catch (SAXException e) {
            // if anything fails, delete the config file to avoid further confusion
            Util.deleteRecursive(dir);
            throw new IOException("Failed to persist config.xml", e);
        } catch (IOException e) {
            // if anything fails, delete the config file to avoid further confusion
            Util.deleteRecursive(dir);
            throw e;
        } finally {
            // don't leave anything behind
            out.abort();
        }
    }