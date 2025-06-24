@Override
      public boolean process(final TProtocol inProt, final TProtocol outProt) throws TException {
        TTransport trans = inProt.getTransport();
        if (!(trans instanceof TSaslServerTransport)) {
          throw new TException("Unexpected non-SASL transport " + trans.getClass());
        }
        TSaslServerTransport saslTrans = (TSaslServerTransport)trans;
        SaslServer saslServer = saslTrans.getSaslServer();
        String authId = saslServer.getAuthorizationID();
        authenticationMethod.set(AuthenticationMethod.KERBEROS);
        LOG.debug("AUTH ID ======>" + authId);
        String endUser = authId;

        if(saslServer.getMechanismName().equals("DIGEST-MD5")) {
          try {
            TokenIdentifier tokenId = SaslRpcServer.getIdentifier(authId,
                secretManager);
            endUser = tokenId.getUser().getUserName();
            authenticationMethod.set(AuthenticationMethod.TOKEN);
          } catch (InvalidToken e) {
            throw new TException(e.getMessage());
          }
        }
        Socket socket = ((TSocket)(saslTrans.getUnderlyingTransport())).getSocket();
        remoteAddress.set(socket.getInetAddress());
        UserGroupInformation clientUgi = null;
        try {
          if (useProxy) {
            clientUgi = UserGroupInformation.createProxyUser(
                endUser, UserGroupInformation.getLoginUser());
            remoteUser.set(clientUgi.getShortUserName());
            return clientUgi.doAs(new PrivilegedExceptionAction<Boolean>() {
              @Override
              public Boolean run() {
                try {
                  return wrapped.process(inProt, outProt);
                } catch (TException te) {
                  throw new RuntimeException(te);
                }
              }
            });
          } else {
            remoteUser.set(endUser);
            return wrapped.process(inProt, outProt);
          }
        } catch (RuntimeException rte) {
          if (rte.getCause() instanceof TException) {
            throw (TException)rte.getCause();
          }
          throw rte;
        } catch (InterruptedException ie) {
          throw new RuntimeException(ie); // unexpected!
        } catch (IOException ioe) {
          throw new RuntimeException(ioe); // unexpected!
        }
        finally {
          if (clientUgi != null) {
            try { FileSystem.closeAllForUGI(clientUgi); }
            catch(IOException exception) {
              LOG.error("Could not clean up file-system handles for UGI: " + clientUgi, exception);
            }
          }
        }
      }