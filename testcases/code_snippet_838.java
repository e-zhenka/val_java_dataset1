@Override
    public Collection<FileAnnotation> parse(final InputStream file, final String moduleName)
            throws InvocationTargetException {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(LintParser.class.getClassLoader());

            List<LintIssue> issues = new ArrayList<LintIssue>();
            digester.push(issues);

            String issueXPath = "issues/issue";
            digester.addObjectCreate(issueXPath, LintIssue.class);
            digester.addSetProperties(issueXPath);
            digester.addSetNext(issueXPath, "add");

            String locationXPath = issueXPath + "/location";
            digester.addObjectCreate(locationXPath, Location.class);
            digester.addSetProperties(locationXPath);
            digester.addSetNext(locationXPath, "addLocation", Location.class.getName());

            digester.parse(file);

            return convert(issues, moduleName);
        } catch (IOException exception) {
            throw new InvocationTargetException(exception);
        } catch (SAXException exception) {
            throw new InvocationTargetException(exception);
        }
    }