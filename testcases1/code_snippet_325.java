@Override
    public AuthenticationResult handleResponse(final byte[] response)
    {
        if (_state == State.COMPLETE)
        {
            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.ERROR,
                                            new IllegalStateException("Multiple Authentications not permitted."));
        }
        else if (_state == State.INITIAL && (response == null || response.length == 0))
        {
            _state = State.CHALLENGE_SENT;
            return new AuthenticationResult(new byte[0], AuthenticationResult.AuthenticationStatus.CONTINUE);
        }

        _state = State.COMPLETE;
        if (response == null || response.length == 0)
        {
            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.ERROR,
                                            new IllegalArgumentException(
                                                    "Invalid PLAIN encoding, authzid null terminator not found"));
        }

        int authzidNullPosition = findNullPosition(response, 0);
        if (authzidNullPosition < 0)
        {
            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.ERROR,
                                            new IllegalArgumentException(
                                                    "Invalid PLAIN encoding, authzid null terminator not found"));
        }
        int authcidNullPosition = findNullPosition(response, authzidNullPosition + 1);
        if (authcidNullPosition < 0)
        {
            return new AuthenticationResult(AuthenticationResult.AuthenticationStatus.ERROR,
                                            new IllegalArgumentException(
                                                    "Invalid PLAIN encoding, authcid null terminator not found"));
        }

        String password;
        try
        {
            _username = new String(response, authzidNullPosition + 1, authcidNullPosition - authzidNullPosition - 1, UTF8);
            // TODO: should not get pwd as a String but as a char array...
            int passwordLen = response.length - authcidNullPosition - 1;
            password = new String(response, authcidNullPosition + 1, passwordLen, UTF8);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("JVM does not support UTF8", e);
        }
        return _usernamePasswordAuthenticationProvider.authenticate(_username, password);
    }