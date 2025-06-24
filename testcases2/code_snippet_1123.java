public static String fillAuthentication(String mavenSettingsContent, final Boolean isReplaceAllServerDefinitions,
                                            Map<String, StandardUsernameCredentials> mavenServerId2jenkinsCredential,
                                            FilePath workDir, List<String> tempFiles) throws Exception {
        String content = mavenSettingsContent;

        if (mavenServerId2jenkinsCredential.isEmpty()) {
            return mavenSettingsContent;
        }

        // TODO: switch to XMLUtils.parse(Reader) when the baseline > 2.179 or  XMLUtils.parse(InputSteam) > 2.265
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        //documentBuilderFactory.isValidating() is false by default, so these attributes won't avoid to parse an usual maven settings.
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        Document doc = documentBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(content)));

        Map<String, Node> removedMavenServers = Collections.emptyMap();

        // locate the server node(s)
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node serversNode = (Node) xpath.evaluate("/settings/servers", doc, XPathConstants.NODE);
        if (serversNode == null) {
            // need to create a 'servers' node
            Node settingsNode = (Node) xpath.evaluate("/settings", doc, XPathConstants.NODE);
            serversNode = doc.createElement("servers");
            settingsNode.appendChild(serversNode);
        } else {
            // remove the server nodes
        	removedMavenServers = removeMavenServerDefinitions(serversNode, mavenServerId2jenkinsCredential.keySet(), Boolean.TRUE.equals(isReplaceAllServerDefinitions));
        }

        for (Entry<String, StandardUsernameCredentials> mavenServerId2JenkinsCredential : mavenServerId2jenkinsCredential.entrySet()) {

            final StandardUsernameCredentials credential = mavenServerId2JenkinsCredential.getValue();
            String mavenServerId = mavenServerId2JenkinsCredential.getKey();

            Node currentDefinition = removedMavenServers.get(mavenServerId);
            if (credential instanceof StandardUsernamePasswordCredentials) {

                StandardUsernamePasswordCredentials usernamePasswordCredentials = (StandardUsernamePasswordCredentials) credential;
                LOGGER.log(Level.FINE, "Maven Server ID {0}: use {1} / {2}", new Object[]{mavenServerId, usernamePasswordCredentials.getId(), usernamePasswordCredentials.getDescription()});

                final Element server = doc.createElement("server");

                // create and add the relevant xml elements
                final Element id = doc.createElement("id");
                id.setTextContent(mavenServerId);
                final Element password = doc.createElement("password");
                password.setTextContent(Secret.toString(usernamePasswordCredentials.getPassword()));
                final Element username = doc.createElement("username");
                username.setTextContent(usernamePasswordCredentials.getUsername());

                server.appendChild(id);
                server.appendChild(username);
                server.appendChild(password);
                copyServerAttributes(currentDefinition,	server);

                serversNode.appendChild(server);
            } else if (credential instanceof SSHUserPrivateKey) {
                SSHUserPrivateKey sshUserPrivateKey = (SSHUserPrivateKey) credential;
                List<String> privateKeys = sshUserPrivateKey.getPrivateKeys();
                String privateKeyContent;

                if (privateKeys.isEmpty()) {
                    LOGGER.log(Level.WARNING, "Maven Server ID {0}: not private key defined in {1}, skip", new Object[]{mavenServerId, sshUserPrivateKey.getId()});
                    continue;
                } else if (privateKeys.size() == 1) {
                    LOGGER.log(Level.FINE, "Maven Server ID {0}: use {1}", new Object[]{mavenServerId, sshUserPrivateKey.getId()});
                    privateKeyContent = privateKeys.get(0);
                } else {
                    LOGGER.log(Level.WARNING, "Maven Server ID {0}: more than one ({1}) private key defined in {1}, use first private key", new Object[]{mavenServerId, privateKeys.size(), sshUserPrivateKey.getId()});
                    privateKeyContent = privateKeys.get(0);
                }

                final Element server = doc.createElement("server");

                // create and add the relevant xml elements
                final Element id = doc.createElement("id");
                id.setTextContent(mavenServerId);

                final Element username = doc.createElement("username");
                username.setTextContent(sshUserPrivateKey.getUsername());

                workDir.mkdirs();
                FilePath privateKeyFile = workDir.createTextTempFile("private-key-", ".pem", privateKeyContent, true);
                privateKeyFile.chmod(0600);
                tempFiles.add(privateKeyFile.getRemote());
                LOGGER.log(Level.FINE, "Create {0}", new Object[]{privateKeyFile.getRemote()});

                final Element privateKey = doc.createElement("privateKey");
                privateKey.setTextContent(privateKeyFile.getRemote());

                final Element passphrase = doc.createElement("passphrase");
                passphrase.setTextContent(Secret.toString(sshUserPrivateKey.getPassphrase()));

                server.appendChild(id);
                server.appendChild(username);
                server.appendChild(privateKey);
                server.appendChild(passphrase);
                copyServerAttributes(currentDefinition,	server);

                serversNode.appendChild(server);
            } else {
                LOGGER.log(Level.WARNING, "Maven Server ID {0}: credentials type of {1} not supported: {2}",
                        new Object[]{mavenServerId, credential == null ? null : credential.getId(), credential == null ? null : credential.getClass()});
            }

        }

        // save the result
        StringWriter writer = new StringWriter();
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        xformer.transform(new DOMSource(doc), new StreamResult(writer));
        content = writer.toString();

        return content;
    }