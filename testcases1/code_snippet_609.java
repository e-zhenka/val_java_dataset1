public void setMetaData(MetaData.Request request)
    {
        if (_metaData == null && _input != null && _channel != null)
        {
            _input.reopen();
            _channel.getResponse().getHttpOutput().reopen();
        }
        _metaData = request;
        _method = request.getMethod();
        _httpFields = request.getFields();
        final HttpURI uri = request.getURI();

        if (uri.isAmbiguous())
        {
            UriCompliance compliance = _channel == null || _channel.getHttpConfiguration() == null ? null : _channel.getHttpConfiguration().getUriCompliance();
            if (uri.hasAmbiguousSegment() && (compliance == null || !compliance.allows(UriCompliance.Violation.AMBIGUOUS_PATH_SEGMENT)))
                throw new BadMessageException("Ambiguous segment in URI");
            if (uri.hasAmbiguousSeparator() && (compliance == null || !compliance.allows(UriCompliance.Violation.AMBIGUOUS_PATH_SEPARATOR)))
                throw new BadMessageException("Ambiguous segment in URI");
            if (uri.hasAmbiguousParameter() && (compliance == null || !compliance.allows(UriCompliance.Violation.AMBIGUOUS_PATH_PARAMETER)))
                throw new BadMessageException("Ambiguous path parameter in URI");
        }

        if (uri.isAbsolute() && uri.hasAuthority() && uri.getPath() != null)
        {
            _uri = uri;
        }
        else
        {
            HttpURI.Mutable builder = HttpURI.build(uri);

            if (!uri.isAbsolute())
                builder.scheme(HttpScheme.HTTP.asString());

            if (uri.getPath() == null)
                builder.path("/");

            if (!uri.hasAuthority())
            {
                HttpField field = getHttpFields().getField(HttpHeader.HOST);
                if (field instanceof HostPortHttpField)
                {
                    HostPortHttpField authority = (HostPortHttpField)field;
                    builder.host(authority.getHost()).port(authority.getPort());
                }
                else
                {
                    builder.host(findServerName()).port(findServerPort());
                }
            }
            _uri = builder.asImmutable();
        }

        setSecure(HttpScheme.HTTPS.is(_uri.getScheme()));

        String encoded = _uri.getPath();
        String path;
        if (encoded == null)
            // TODO this is not really right for CONNECT
            path = _uri.isAbsolute() ? "/" : null;
        else if (encoded.startsWith("/"))
            path = (encoded.length() == 1) ? "/" : _uri.getDecodedPath();
        else if ("*".equals(encoded) || HttpMethod.CONNECT.is(getMethod()))
            path = encoded;
        else
            path = null;

        if (path == null || path.isEmpty())
        {
            _pathInContext = encoded == null ? "" : encoded;
            throw new BadMessageException(400, "Bad URI");
        }
        _pathInContext = path;
    }