@Override public SubversionChangeLogSet parse(@SuppressWarnings("rawtypes") Run build, RepositoryBrowser<?> browser, File changelogFile) throws IOException, SAXException {
        // http://svn.apache.org/repos/asf/subversion/trunk/subversion/svn/schema/log.rnc

        Digester digester = new Digester2();
        if (!Boolean.getBoolean(SubversionChangeLogParser.class.getName() + ".UNSAFE")) {
            try {
                digester.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                digester.setFeature("http://xml.org/sax/features/external-general-entities", false);
                digester.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                digester.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            } catch (ParserConfigurationException ex) {
                LOGGER.log(Level.WARNING, "Failed to securely configure Subversion changelog parser", ex);
                throw new SAXException("Failed to securely configure Subversion changelog parser", ex);
            }
            digester.setXIncludeAware(false);
        }
        ArrayList<LogEntry> r = new ArrayList<>();
        digester.push(r);

        digester.addObjectCreate("*/logentry", LogEntry.class);
        digester.addSetProperties("*/logentry");
        digester.addBeanPropertySetter("*/logentry/author","user");
        digester.addBeanPropertySetter("*/logentry/date");
        digester.addBeanPropertySetter("*/logentry/msg");
        digester.addSetNext("*/logentry","add");

        digester.addObjectCreate("*/logentry/paths/path", Path.class);
        digester.addSetProperties("*/logentry/paths/path");
        digester.addBeanPropertySetter("*/logentry/paths/path","value");
        digester.addSetNext("*/logentry/paths/path","addPath");

        try {
            digester.parse(changelogFile);
        } catch (IOException | SAXException e) {
            throw new IOException("Failed to parse " + changelogFile,e);
        }

        for (LogEntry e : r) {
            e.finish();
        }
        return new SubversionChangeLogSet(build, browser, r, ignoreDirPropChanges);
    }