private void doEncryptBeforeSign() {
        try {
            AbstractTokenWrapper encryptionWrapper = getEncryptionToken();
            assertTokenWrapper(encryptionWrapper);
            AbstractToken encryptionToken = encryptionWrapper.getToken();
            List<WSEncryptionPart> encrParts = getEncryptedParts();
            List<WSEncryptionPart> sigParts = getSignedParts();
            
            if (encryptionToken != null) {
                //The encryption token can be an IssuedToken or a 
                //SecureConversationToken
                String tokenId = null;
                SecurityToken tok = null;
                if (encryptionToken instanceof IssuedToken 
                    || encryptionToken instanceof KerberosToken
                    || encryptionToken instanceof SecureConversationToken
                    || encryptionToken instanceof SecurityContextToken
                    || encryptionToken instanceof SpnegoContextToken) {
                    tok = getSecurityToken();
                } else if (encryptionToken instanceof X509Token) {
                    if (isRequestor()) {
                        tokenId = setupEncryptedKey(encryptionWrapper, encryptionToken);
                    } else {
                        tokenId = getEncryptedKey();
                    }
                } else if (encryptionToken instanceof UsernameToken) {
                    if (isRequestor()) {
                        tokenId = setupUTDerivedKey((UsernameToken)encryptionToken);
                    } else {
                        tokenId = getUTDerivedKey();
                    }
                }
                assertToken(encryptionToken);
                if (tok == null) {
                    //if (tokenId == null || tokenId.length() == 0) {
                        //REVISIT - no tokenId?   Exception?
                    //}
                    if (tokenId != null && tokenId.startsWith("#")) {
                        tokenId = tokenId.substring(1);
                    }
                    
                    /*
                     * Get hold of the token from the token storage
                     */
                    tok = tokenStore.getToken(tokenId);
                }
    
                boolean attached = false;
                if (isTokenRequired(encryptionToken.getIncludeTokenType())) {
                    Element el = tok.getToken();
                    this.addEncryptedKeyElement(cloneElement(el));
                    attached = true;
                } else if (encryptionToken instanceof X509Token && isRequestor()) {
                    Element el = tok.getToken();
                    this.addEncryptedKeyElement(cloneElement(el));
                    attached = true;
                }
                
                WSSecBase encr = doEncryption(encryptionWrapper, tok, attached, encrParts, true);
                
                handleEncryptedSignedHeaders(encrParts, sigParts);
                
                if (timestampEl != null) {
                    WSEncryptionPart timestampPart = 
                        convertToEncryptionPart(timestampEl.getElement());
                    sigParts.add(timestampPart);        
                }
                
                addSupportingTokens(sigParts);
                if (!isRequestor()) {
                    addSignatureConfirmation(sigParts);
                }
                
                //Sign the message
                //We should use the same key in the case of EncryptBeforeSig
                if (sigParts.size() > 0) {
                    signatures.add(this.doSignature(sigParts, encryptionWrapper, encryptionToken, 
                                                    tok, attached));
                }
                
                if (isRequestor()) {
                    this.doEndorse();
                }
                
                //Check for signature protection and encryption of UsernameToken
                if (sbinding.isEncryptSignature() 
                    || encryptedTokensList.size() > 0 && isRequestor()) {
                    List<WSEncryptionPart> secondEncrParts = new ArrayList<WSEncryptionPart>();
                    
                    //Now encrypt the signature using the above token
                    if (sbinding.isEncryptSignature()) {
                        if (this.mainSigId != null) {
                            WSEncryptionPart sigPart = 
                                new WSEncryptionPart(this.mainSigId, "Element");
                            sigPart.setElement(bottomUpElement);
                            secondEncrParts.add(sigPart);
                        }
                        if (sigConfList != null && !sigConfList.isEmpty()) {
                            secondEncrParts.addAll(sigConfList);
                        }
                        assertPolicy(
                            new QName(sbinding.getName().getNamespaceURI(), SPConstants.ENCRYPT_SIGNATURE));
                    }
                    
                    if (isRequestor()) {
                        secondEncrParts.addAll(encryptedTokensList);
                    }
                    
                    Element secondRefList = null;
                    
                    if (encryptionToken.getDerivedKeys() == DerivedKeys.RequireDerivedKeys 
                        && !secondEncrParts.isEmpty()) {
                        secondRefList = ((WSSecDKEncrypt)encr).encryptForExternalRef(null, 
                                secondEncrParts);
                        this.addDerivedKeyElement(secondRefList);
                    } else if (!secondEncrParts.isEmpty()) {
                        //Encrypt, get hold of the ref list and add it
                        secondRefList = ((WSSecEncrypt)encr).encryptForRef(null, encrParts);
                        this.addDerivedKeyElement(secondRefList);
                    }
                }
            }
        } catch (RuntimeException ex) {
            LOG.log(Level.FINE, ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            LOG.log(Level.FINE, ex.getMessage(), ex);
            throw new Fault(ex);
        }
    }