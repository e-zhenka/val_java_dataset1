private void cacheInput(Message outMessage) {
        if (outMessage.getExchange() == null) {
            return;
        }
        Message inMessage = outMessage.getExchange().getInMessage();
        if (inMessage == null) {
            return;
        }
        Collection<Attachment> atts = inMessage.getAttachments();
        if (atts != null) {
            for (Attachment a : atts) {
                if (a.getDataHandler().getDataSource() instanceof AttachmentDataSource) {
                    try {
                        ((AttachmentDataSource)a.getDataHandler().getDataSource()).cache(inMessage);
                    } catch (IOException e) {
                        throw new Fault(e);
                    }
                }
            }
        }
        DelegatingInputStream in = inMessage.getContent(DelegatingInputStream.class);
        if (in != null) {
            in.cacheInput();
        }
    }