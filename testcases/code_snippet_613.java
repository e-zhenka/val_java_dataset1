public void processPacket(Packet packet)
        {
            if(!(packet instanceof org.jivesoftware.smack.packet.Message))
                return;

            org.jivesoftware.smack.packet.Message msg =
                (org.jivesoftware.smack.packet.Message)packet;

            boolean isForwardedSentMessage = false;
            if(msg.getBody() == null)
            {

                CarbonPacketExtension carbonExt
                    = (CarbonPacketExtension) msg.getExtension(
                        CarbonPacketExtension.NAMESPACE);
                if(carbonExt == null)
                    return;

                isForwardedSentMessage
                    = (carbonExt.getElementName()
                        == CarbonPacketExtension.SENT_ELEMENT_NAME);
                List<ForwardedPacketExtension> extensions
                    = carbonExt.getChildExtensionsOfType(
                        ForwardedPacketExtension.class);
                if(extensions.isEmpty())
                    return;

                // according to xep-0280 all carbons should come from
                // our bare jid
                if (!msg.getFrom().equals(
                        StringUtils.parseBareAddress(
                            jabberProvider.getOurJID())))
                {
                    logger.info("Received a carbon copy with wrong from!");
                    return;
                }

                ForwardedPacketExtension forwardedExt = extensions.get(0);
                msg = forwardedExt.getMessage();
                if(msg == null || msg.getBody() == null)
                    return;

            }

            Object multiChatExtension =
                msg.getExtension("x", "http://jabber.org/protocol/muc#user");

            // its not for us
            if(multiChatExtension != null)
                return;

            String userFullId
                = isForwardedSentMessage? msg.getTo() : msg.getFrom();

            String userBareID = StringUtils.parseBareAddress(userFullId);

            boolean isPrivateMessaging = false;
            ChatRoom privateContactRoom = null;
            OperationSetMultiUserChatJabberImpl mucOpSet =
                (OperationSetMultiUserChatJabberImpl)jabberProvider
                    .getOperationSet(OperationSetMultiUserChat.class);
            if(mucOpSet != null)
                privateContactRoom = mucOpSet.getChatRoom(userBareID);

            if(privateContactRoom != null)
            {
                isPrivateMessaging = true;
            }

            if(logger.isDebugEnabled())
            {
                if (logger.isDebugEnabled())
                    logger.debug("Received from "
                             + userBareID
                             + " the message "
                             + msg.toXML());
            }

            Message newMessage = createMessage(msg.getBody(),
                    DEFAULT_MIME_TYPE, msg.getPacketID());

            //check if the message is available in xhtml
            PacketExtension ext = msg.getExtension(
                            "http://jabber.org/protocol/xhtml-im");

            if(ext != null)
            {
                XHTMLExtension xhtmlExt
                    = (XHTMLExtension)ext;

                //parse all bodies
                Iterator<String> bodies = xhtmlExt.getBodies();
                StringBuffer messageBuff = new StringBuffer();
                while (bodies.hasNext())
                {
                    String body = bodies.next();
                    messageBuff.append(body);
                }

                if (messageBuff.length() > 0)
                {
                    // we remove body tags around message cause their
                    // end body tag is breaking
                    // the visualization as html in the UI
                    String receivedMessage =
                        messageBuff.toString()
                        // removes body start tag
                        .replaceAll("\\<[bB][oO][dD][yY].*?>","")
                        // removes body end tag
                        .replaceAll("\\</[bB][oO][dD][yY].*?>","");

                    // for some reason &apos; is not rendered correctly
                    // from our ui, lets use its equivalent. Other
                    // similar chars(< > & ") seem ok.
                    receivedMessage =
                            receivedMessage.replaceAll("&apos;", "&#39;");

                    newMessage = createMessage(receivedMessage,
                            HTML_MIME_TYPE, msg.getPacketID());
                }
            }

            PacketExtension correctionExtension =
                    msg.getExtension(MessageCorrectionExtension.NAMESPACE);
            String correctedMessageUID = null;
            if (correctionExtension != null)
            {
                correctedMessageUID = ((MessageCorrectionExtension)
                        correctionExtension).getCorrectedMessageUID();
            }

            Contact sourceContact
                = opSetPersPresence.findContactByID(
                    (isPrivateMessaging? userFullId : userBareID));
            if(msg.getType()
                            == org.jivesoftware.smack.packet.Message.Type.error)
            {
                // error which is multichat and we don't know about the contact
                // is a muc message error which is missing muc extension
                // and is coming from the room, when we try to send message to
                // room which was deleted or offline on the server
                if(isPrivateMessaging && sourceContact == null)
                {
                    if(privateContactRoom != null)
                    {
                        XMPPError error = packet.getError();
                        int errorResultCode
                            = ChatRoomMessageDeliveryFailedEvent.UNKNOWN_ERROR;

                        if(error != null && error.getCode() == 403)
                        {
                            errorResultCode
                                = ChatRoomMessageDeliveryFailedEvent.FORBIDDEN;
                        }

                        String errorReason = error.getMessage();

                        ChatRoomMessageDeliveryFailedEvent evt =
                            new ChatRoomMessageDeliveryFailedEvent(
                                privateContactRoom,
                                null,
                                errorResultCode,
                                errorReason,
                                new Date(),
                                newMessage);
                        ((ChatRoomJabberImpl)privateContactRoom)
                            .fireMessageEvent(evt);
                    }

                    return;
                }

                if (logger.isInfoEnabled())
                    logger.info("Message error received from " + userBareID);

                int errorResultCode = MessageDeliveryFailedEvent.UNKNOWN_ERROR;
                if (packet.getError() != null)
                {
                    int errorCode = packet.getError().getCode();
    
                    if(errorCode == 503)
                    {
                        org.jivesoftware.smackx.packet.MessageEvent msgEvent =
                            (org.jivesoftware.smackx.packet.MessageEvent)
                                packet.getExtension("x", "jabber:x:event");
                        if(msgEvent != null && msgEvent.isOffline())
                        {
                            errorResultCode =
                                MessageDeliveryFailedEvent
                                    .OFFLINE_MESSAGES_NOT_SUPPORTED;
                        }
                    }
                }

                if (sourceContact == null)
                {
                    sourceContact = opSetPersPresence.createVolatileContact(
                        userFullId, isPrivateMessaging);
                }

                MessageDeliveryFailedEvent ev
                    = new MessageDeliveryFailedEvent(newMessage,
                                                     sourceContact,
                                                     correctedMessageUID,
                                                     errorResultCode);

                // ev = messageDeliveryFailedTransform(ev);

                if (ev != null)
                    fireMessageEvent(ev);
                return;
            }
            putJidForAddress(userFullId, msg.getThread());

            // In the second condition we filter all group chat messages,
            // because they are managed by the multi user chat operation set.
            if(sourceContact == null)
            {
                if (logger.isDebugEnabled())
                    logger.debug("received a message from an unknown contact: "
                                   + userBareID);
                //create the volatile contact
                sourceContact = opSetPersPresence
                    .createVolatileContact(
                        userFullId,
                        isPrivateMessaging);
            }

            Date timestamp = new Date();
            //Check for XEP-0091 timestamp (deprecated)
            PacketExtension delay = msg.getExtension("x", "jabber:x:delay");
            if(delay != null && delay instanceof DelayInformation)
            {
                timestamp = ((DelayInformation)delay).getStamp();
            }
            //check for XEP-0203 timestamp
            delay = msg.getExtension("delay", "urn:xmpp:delay");
            if(delay != null && delay instanceof DelayInfo)
            {
                timestamp = ((DelayInfo)delay).getStamp();
            }

            ContactResource resource = ((ContactJabberImpl) sourceContact)
                    .getResourceFromJid(userFullId);

            EventObject msgEvt = null;
            if(!isForwardedSentMessage)
                msgEvt
                    = new MessageReceivedEvent( newMessage,
                                                sourceContact,
                                                resource,
                                                timestamp,
                                                correctedMessageUID,
                                                isPrivateMessaging,
                                                privateContactRoom);
            else
                msgEvt = new MessageDeliveredEvent(newMessage, sourceContact, timestamp);
            // msgReceivedEvt = messageReceivedTransform(msgReceivedEvt);
            if (msgEvt != null)
                fireMessageEvent(msgEvt);
        }