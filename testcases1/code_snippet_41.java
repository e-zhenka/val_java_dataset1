public Session createSession(String from) throws MessagingException {
        Properties props = new Properties(System.getProperties());

        MailAccount acc = mailAccount;
        if(StringUtils.isNotBlank(from)){
            InternetAddress fromAddress = new InternetAddress(from);
            for(MailAccount ma : addAccounts) {
                if(ma == null || !ma.isValid() || !ma.getAddress().equalsIgnoreCase(fromAddress.getAddress())) continue;
                acc = ma;
                break;
            }
        }

        if(!acc.isValid()) {
            // what do we want to do here?
        }

        if (acc.getSmtpHost() != null) {
            props.put("mail.smtp.host", acc.getSmtpHost());
        }
        if (acc.getSmtpPort() != null) {
            props.put("mail.smtp.port", acc.getSmtpPort());
        }
        if (acc.isUseSsl()) {
            /* This allows the user to override settings by setting system properties but
             * also allows us to use the default SMTPs port of 465 if no port is already set.
             * It would be cleaner to use smtps, but that's done by calling session.getTransport()...
             * and thats done in mail sender, and it would be a bit of a hack to get it all to
             * coordinate, and we can make it work through setting mail.smtp properties.
             */
            if (props.getProperty("mail.smtp.socketFactory.port") == null) {
                String port = acc.getSmtpPort() == null ? "465" : mailAccount.getSmtpPort();
                props.put("mail.smtp.port", port);
                props.put("mail.smtp.socketFactory.port", port);
            }
            if (props.getProperty("mail.smtp.socketFactory.class") == null) {
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }
            props.put("mail.smtp.socketFactory.fallback", "false");

            // RFC 2595 specifies additional checks that must be performed on the server's
            // certificate to ensure that the server you connected to is the server you intended
            // to connect to. This reduces the risk of "man in the middle" attacks.
            if (props.getProperty("mail.smtp.ssl.checkserveridentity") == null) {
                props.put("mail.smtp.ssl.checkserveridentity", "true");
            }
        }
        if (!StringUtils.isBlank(acc.getSmtpUsername())) {
            props.put("mail.smtp.auth", "true");
        }

        // avoid hang by setting some timeout.
        props.put("mail.smtp.timeout", "60000");
        props.put("mail.smtp.connectiontimeout", "60000");

        try {
            String ap = acc.getAdvProperties();
            if (ap != null && !isBlank(ap.trim())) {
                props.load(new StringReader(ap));
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Parameters parse fail.", e);
        }

        return Session.getInstance(props, getAuthenticator(acc));
    }