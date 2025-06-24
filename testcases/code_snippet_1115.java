protected List<String> getRoles(JNDIConnection connection, User user) throws NamingException {

        if (user == null) {
            return null;
        }

        // This is returned from the directory so will be attribute value
        // escaped if required
        String dn = user.getDN();
        // This is the name the user provided to the authentication process so
        // it will not be escaped
        String username = user.getUserName();
        String userRoleId = user.getUserRoleId();

        if (dn == null || username == null) {
            return null;
        }

        if (containerLog.isTraceEnabled()) {
            containerLog.trace("  getRoles(" + dn + ")");
        }

        // Start with roles retrieved from the user entry
        List<String> list = new ArrayList<>();
        List<String> userRoles = user.getRoles();
        if (userRoles != null) {
            list.addAll(userRoles);
        }
        if (commonRole != null) {
            list.add(commonRole);
        }

        if (containerLog.isTraceEnabled()) {
            containerLog.trace("  Found " + list.size() + " user internal roles");
            containerLog.trace("  Found user internal roles " + list.toString());
        }

        // Are we configured to do role searches?
        if ((connection.roleFormat == null) || (roleName == null)) {
            return list;
        }

        // Set up parameters for an appropriate search
        String filter = connection.roleFormat.format(new String[] {
                doFilterEscaping(dn),
                doFilterEscaping(doAttributeValueEscaping(username)),
                userRoleId });
        SearchControls controls = new SearchControls();
        if (roleSubtree) {
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        } else {
            controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        }
        controls.setReturningAttributes(new String[] {roleName});

        String base = null;
        if (connection.roleBaseFormat != null) {
            NameParser np = connection.context.getNameParser("");
            Name name = np.parse(dn);
            String nameParts[] = new String[name.size()];
            for (int i = 0; i < name.size(); i++) {
                nameParts[i] = name.get(i);
            }
            base = connection.roleBaseFormat.format(nameParts);
        } else {
            base = "";
        }

        // Perform the configured search and process the results
        NamingEnumeration<SearchResult> results = searchAsUser(connection.context, user, base, filter, controls,
                isRoleSearchAsUser());

        if (results == null) {
            return list;  // Should never happen, but just in case ...
        }

        Map<String, String> groupMap = new HashMap<>();
        try {
            while (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                if (attrs == null) {
                    continue;
                }
                String dname = getDistinguishedName(connection.context, roleBase, result);
                String name = getAttributeValue(roleName, attrs);
                if (name != null && dname != null) {
                    groupMap.put(dname, name);
                }
            }
        } catch (PartialResultException ex) {
            if (!adCompat) {
                throw ex;
            }
        } finally {
            results.close();
        }

        if (containerLog.isTraceEnabled()) {
            Set<Entry<String, String>> entries = groupMap.entrySet();
            containerLog.trace("  Found " + entries.size() + " direct roles");
            for (Entry<String, String> entry : entries) {
                containerLog.trace(  "  Found direct role " + entry.getKey() + " -> " + entry.getValue());
            }
        }

        // if nested group search is enabled, perform searches for nested groups until no new group is found
        if (getRoleNested()) {

            // The following efficient algorithm is known as memberOf Algorithm, as described in "Practices in
            // Directory Groups". It avoids group slurping and handles cyclic group memberships as well.
            // See http://middleware.internet2.edu/dir/ for details

            Map<String, String> newGroups = new HashMap<>(groupMap);
            while (!newGroups.isEmpty()) {
                Map<String, String> newThisRound = new HashMap<>(); // Stores the groups we find in this iteration

                for (Entry<String, String> group : newGroups.entrySet()) {
                    filter = connection.roleFormat.format(new String[] { doFilterEscaping(group.getKey()),
                            group.getValue(), group.getValue() });

                    if (containerLog.isTraceEnabled()) {
                        containerLog.trace("Perform a nested group search with base "+ roleBase +
                                " and filter " + filter);
                    }

                    results = searchAsUser(connection.context, user, roleBase, filter, controls, isRoleSearchAsUser());

                    try {
                        while (results.hasMore()) {
                            SearchResult result = results.next();
                            Attributes attrs = result.getAttributes();
                            if (attrs == null) {
                                continue;
                            }
                            String dname = getDistinguishedName(connection.context, roleBase, result);
                            String name = getAttributeValue(roleName, attrs);
                            if (name != null && dname != null && !groupMap.keySet().contains(dname)) {
                                groupMap.put(dname, name);
                                newThisRound.put(dname, name);

                                if (containerLog.isTraceEnabled()) {
                                    containerLog.trace("  Found nested role " + dname + " -> " + name);
                                }
                            }
                        }
                    } catch (PartialResultException ex) {
                        if (!adCompat) {
                            throw ex;
                        }
                    } finally {
                        results.close();
                    }
                }

                newGroups = newThisRound;
            }
        }

        list.addAll(groupMap.values());
        return list;
    }