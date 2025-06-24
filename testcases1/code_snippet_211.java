public void handleMessage(Message message) throws Fault {
        String method = (String)message.get(Message.HTTP_REQUEST_METHOD);
        if ("GET".equals(method)) {
            return;
        }

        Message outMs = message.getExchange().getOutMessage();
        Message inMsg = outMs == null ? message : outMs.getExchange().getInMessage();

        XMLStreamReader originalXmlStreamReader = inMsg.getContent(XMLStreamReader.class);
        if (originalXmlStreamReader == null) {
            InputStream is = inMsg.getContent(InputStream.class);
            if (is != null) {
                originalXmlStreamReader = StaxUtils.createXMLStreamReader(is);
            }
        }

        registerStaxActionInInterceptor(inMsg);

        try {
            XMLSecurityProperties properties = new XMLSecurityProperties();
            configureDecryptionKeys(inMsg, properties);
            Crypto signatureCrypto = getSignatureCrypto(inMsg);
            configureSignatureKeys(signatureCrypto, inMsg, properties);

            SecurityEventListener securityEventListener =
                configureSecurityEventListener(signatureCrypto, inMsg, properties);
            InboundXMLSec inboundXMLSec = XMLSec.getInboundWSSec(properties);

            XMLStreamReader newXmlStreamReader =
                inboundXMLSec.processInMessage(originalXmlStreamReader, null, securityEventListener);
            inMsg.setContent(XMLStreamReader.class, newXmlStreamReader);

        } catch (XMLStreamException e) {
            throwFault(e.getMessage(), e);
        } catch (XMLSecurityException e) {
            throwFault(e.getMessage(), e);
        } catch (IOException e) {
            throwFault(e.getMessage(), e);
        } catch (UnsupportedCallbackException e) {
            throwFault(e.getMessage(), e);
        }
    }