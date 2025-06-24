@Override public SubversionChangeLogSet parse(@SuppressWarnings("rawtypes") Run build, RepositoryBrowser<?> browser, File changelogFile) throws IOException, SAXException {
        // http://svn.apache.org/repos/asf/subversion/trunk/subversion/svn/schema/log.rnc

        Digester digester = new Digester2();
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