private synchronized TaskBuilder loadTasks() throws IOException {
        File projectsDir = new File(root,"jobs");
        if(!projectsDir.getCanonicalFile().isDirectory() && !projectsDir.mkdirs()) {
            if(projectsDir.exists())
                throw new IOException(projectsDir+" is not a directory");
            throw new IOException("Unable to create "+projectsDir+"\nPermission issue? Please create this directory manually.");
        }
        File[] subdirs = projectsDir.listFiles();

        final Set<String> loadedNames = Collections.synchronizedSet(new HashSet<String>());

        TaskGraphBuilder g = new TaskGraphBuilder();
        Handle loadJenkins = g.requires(EXTENSIONS_AUGMENTED).attains(JOB_LOADED).add("Loading global config", new Executable() {
            public void run(Reactor session) throws Exception {
                loadConfig();
                // if we are loading old data that doesn't have this field
                if (slaves != null && !slaves.isEmpty() && nodes.isLegacy()) {
                    nodes.setNodes(slaves);
                    slaves = null;
                } else {
                    nodes.load();
                }

                clouds.setOwner(Jenkins.this);
            }
        });

        for (final File subdir : subdirs) {
            g.requires(loadJenkins).attains(JOB_LOADED).notFatal().add("Loading item " + subdir.getName(), new Executable() {
                public void run(Reactor session) throws Exception {
                    if(!Items.getConfigFile(subdir).exists()) {
                        //Does not have job config file, so it is not a jenkins job hence skip it
                        return;
                    }
                    TopLevelItem item = (TopLevelItem) Items.load(Jenkins.this, subdir);
                    items.put(item.getName(), item);
                    loadedNames.add(item.getName());
                }
            });
        }

        g.requires(JOB_LOADED).add("Cleaning up obsolete items deleted from the disk", new Executable() {
            public void run(Reactor reactor) throws Exception {
                // anything we didn't load from disk, throw them away.
                // doing this after loading from disk allows newly loaded items
                // to inspect what already existed in memory (in case of reloading)

                // retainAll doesn't work well because of CopyOnWriteMap implementation, so remove one by one
                // hopefully there shouldn't be too many of them.
                for (String name : items.keySet()) {
                    if (!loadedNames.contains(name))
                        items.remove(name);
                }
            }
        });

        g.requires(JOB_LOADED).add("Finalizing set up",new Executable() {
            public void run(Reactor session) throws Exception {
                rebuildDependencyGraph();

                {// recompute label objects - populates the labels mapping.
                    for (Node slave : nodes.getNodes())
                        // Note that not all labels are visible until the agents have connected.
                        slave.getAssignedLabels();
                    getAssignedLabels();
                }

                // initialize views by inserting the default view if necessary
                // this is both for clean Jenkins and for backward compatibility.
                if(views.size()==0 || primaryView==null) {
                    View v = new AllView(AllView.DEFAULT_VIEW_NAME);
                    setViewOwner(v);
                    views.add(0,v);
                    primaryView = v.getViewName();
                }
                primaryView = AllView.migrateLegacyPrimaryAllViewLocalizedName(views, primaryView);

                if (useSecurity!=null && !useSecurity) {
                    // forced reset to the unsecure mode.
                    // this works as an escape hatch for people who locked themselves out.
                    authorizationStrategy = AuthorizationStrategy.UNSECURED;
                    setSecurityRealm(SecurityRealm.NO_AUTHENTICATION);
                } else {
                    // read in old data that doesn't have the security field set
                    if(authorizationStrategy==null) {
                        if(useSecurity==null)
                            authorizationStrategy = AuthorizationStrategy.UNSECURED;
                        else
                            authorizationStrategy = new LegacyAuthorizationStrategy();
                    }
                    if(securityRealm==null) {
                        if(useSecurity==null)
                            setSecurityRealm(SecurityRealm.NO_AUTHENTICATION);
                        else
                            setSecurityRealm(new LegacySecurityRealm());
                    } else {
                        // force the set to proxy
                        setSecurityRealm(securityRealm);
                    }
                }


                // Initialize the filter with the crumb issuer
                setCrumbIssuer(crumbIssuer);

                // auto register root actions
                for (Action a : getExtensionList(RootAction.class))
                    if (!actions.contains(a)) actions.add(a);
            }
        });

        return g;
    }