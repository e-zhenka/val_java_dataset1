public Object getBody() throws JMSException {
        Message message = getMessage();
        if (message instanceof TextMessage) {
            return ((TextMessage) message).getText();
        }
        if (message instanceof ObjectMessage) {
            try {
                return ((ObjectMessage) message).getObject();
            } catch (Exception e) {
                //message could not be parsed, make the reason available
                return new String("Cannot display ObjectMessage body. Reason: " + e.getMessage());
            }
        }
        if (message instanceof MapMessage) {
            return createMapBody((MapMessage) message);
        }
        if (message instanceof BytesMessage) {
            BytesMessage msg = (BytesMessage) message;
            int len = (int) msg.getBodyLength();
            if (len > -1) {
                byte[] data = new byte[len];
                msg.readBytes(data);
                return new String(data);
            } else {
                return "";
            }
        }
        if (message instanceof StreamMessage) {
            return "StreamMessage is not viewable";
        }

        // unknown message type
        if (message != null) {
            return "Unknown message type [" + message.getClass().getName() + "] " + message;
        }

        return null;
    }