@Override
    public void deactivate() {
        // Disassociate from the current conversation
        if (isActive()) {
            if (!isAssociated()) {
                throw ConversationLogger.LOG.mustCallAssociateBeforeDeactivate();
            }

            try {
                if (getCurrentConversation().isTransient() && getRequestAttribute(getRequest(), ConversationNamingScheme.PARAMETER_NAME) != null) {
                    // WELD-1746 Don't destroy ended conversations - these must be destroyed in a synchronized block - see also cleanUpConversationMap()
                    destroy();
                } else {
                    // Update the conversation timestamp
                    getCurrentConversation().touch();
                    if (!getBeanStore().isAttached()) {
                        /*
                         * This was a transient conversation at the beginning of the request, so we need to update the CID it uses, and attach it. We also add
                         * it to the conversations the session knows about.
                         */
                        if (!(getRequestAttribute(getRequest(), ConversationNamingScheme.PARAMETER_NAME) instanceof ConversationNamingScheme)) {
                            throw ConversationLogger.LOG.conversationNamingSchemeNotFound();
                        }
                        ((ConversationNamingScheme) getRequestAttribute(getRequest(), ConversationNamingScheme.PARAMETER_NAME)).setCid(getCurrentConversation()
                                .getId());

                        try {
                            getBeanStore().attach();
                            getConversationMap().put(getCurrentConversation().getId(), getCurrentConversation());
                        } catch (Exception cause) {
                            // possible session not found error..
                            ContextLogger.LOG.destroyingContextAfterBeanStoreAttachError(this, getBeanStore(), cause.getMessage());
                            ContextLogger.LOG.catchingDebug(cause);
                            destroy();
                        }
                    }
                }
            } finally {
                // WELD-1690 always try to unlock the current conversation
                getCurrentConversation().unlock();
                // WELD-1802
                setBeanStore(null);
                // Clean up any expired/ended conversations
                cleanUpConversationMap();
                // deactivate the context
                super.setActive(false);
            }
        } else {
            throw ConversationLogger.LOG.contextNotActive();
        }
    }