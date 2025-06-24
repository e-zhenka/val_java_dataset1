private void parse(State state, final String uri)
        {
            int mark = 0; // the start of the current section being parsed
            int pathMark = 0; // the start of the path section
            int segment = 0; // the start of the current segment within the path
            boolean encoded = false; // set to true if the path contains % encoded characters
            boolean dot = false; // set to true if the path containers . or .. segments
            int escapedTwo = 0; // state of parsing a %2<x>
            int end = uri.length();
            for (int i = 0; i < end; i++)
            {
                char c = uri.charAt(i);

                switch (state)
                {
                    case START:
                    {
                        switch (c)
                        {
                            case '/':
                                mark = i;
                                state = State.HOST_OR_PATH;
                                break;
                            case ';':
                                mark = i + 1;
                                state = State.PARAM;
                                break;
                            case '?':
                                // assume empty path (if seen at start)
                                _path = "";
                                mark = i + 1;
                                state = State.QUERY;
                                break;
                            case '#':
                                mark = i + 1;
                                state = State.FRAGMENT;
                                break;
                            case '*':
                                _path = "*";
                                state = State.ASTERISK;
                                break;
                            case '%':
                                encoded = true;
                                escapedTwo = 1;
                                mark = pathMark = segment = i;
                                state = State.PATH;
                                break;
                            case '.':
                                dot = true;
                                pathMark = segment = i;
                                state = State.PATH;
                                break;
                            default:
                                mark = i;
                                if (_scheme == null)
                                    state = State.SCHEME_OR_PATH;
                                else
                                {
                                    pathMark = segment = i;
                                    state = State.PATH;
                                }
                                break;
                        }
                        continue;
                    }

                    case SCHEME_OR_PATH:
                    {
                        switch (c)
                        {
                            case ':':
                                // must have been a scheme
                                _scheme = uri.substring(mark, i);
                                // Start again with scheme set
                                state = State.START;
                                break;
                            case '/':
                                // must have been in a path and still are
                                segment = i + 1;
                                state = State.PATH;
                                break;
                            case ';':
                                // must have been in a path
                                mark = i + 1;
                                state = State.PARAM;
                                break;
                            case '?':
                                // must have been in a path
                                _path = uri.substring(mark, i);
                                mark = i + 1;
                                state = State.QUERY;
                                break;
                            case '%':
                                // must have be in an encoded path
                                encoded = true;
                                escapedTwo = 1;
                                state = State.PATH;
                                break;
                            case '#':
                                // must have been in a path
                                _path = uri.substring(mark, i);
                                state = State.FRAGMENT;
                                break;
                            default:
                                break;
                        }
                        continue;
                    }
                    case HOST_OR_PATH:
                    {
                        switch (c)
                        {
                            case '/':
                                _host = "";
                                mark = i + 1;
                                state = State.HOST;
                                break;
                            case '%':
                            case '@':
                            case ';':
                            case '?':
                            case '#':
                            case '.':
                                // was a path, look again
                                i--;
                                pathMark = mark;
                                segment = mark + 1;
                                state = State.PATH;
                                break;
                            default:
                                // it is a path
                                pathMark = mark;
                                segment = mark + 1;
                                state = State.PATH;
                        }
                        continue;
                    }

                    case HOST:
                    {
                        switch (c)
                        {
                            case '/':
                                _host = uri.substring(mark, i);
                                pathMark = mark = i;
                                segment = mark + 1;
                                state = State.PATH;
                                break;
                            case ':':
                                if (i > mark)
                                    _host = uri.substring(mark, i);
                                mark = i + 1;
                                state = State.PORT;
                                break;
                            case '@':
                                if (_user != null)
                                    throw new IllegalArgumentException("Bad authority");
                                _user = uri.substring(mark, i);
                                mark = i + 1;
                                break;
                            case '[':
                                state = State.IPV6;
                                break;
                            default:
                                break;
                        }
                        break;
                    }
                    case IPV6:
                    {
                        switch (c)
                        {
                            case '/':
                                throw new IllegalArgumentException("No closing ']' for ipv6 in " + uri);
                            case ']':
                                c = uri.charAt(++i);
                                _host = uri.substring(mark, i);
                                if (c == ':')
                                {
                                    mark = i + 1;
                                    state = State.PORT;
                                }
                                else
                                {
                                    pathMark = mark = i;
                                    state = State.PATH;
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    }
                    case PORT:
                    {
                        if (c == '@')
                        {
                            if (_user != null)
                                throw new IllegalArgumentException("Bad authority");
                            // It wasn't a port, but a password!
                            _user = _host + ":" + uri.substring(mark, i);
                            mark = i + 1;
                            state = State.HOST;
                        }
                        else if (c == '/')
                        {
                            _port = TypeUtil.parseInt(uri, mark, i - mark, 10);
                            pathMark = mark = i;
                            segment = i + 1;
                            state = State.PATH;
                        }
                        break;
                    }
                    case PATH:
                    {
                        switch (c)
                        {
                            case ';':
                                checkSegment(uri, segment, i, true);
                                mark = i + 1;
                                state = State.PARAM;
                                break;
                            case '?':
                                checkSegment(uri, segment, i, false);
                                _path = uri.substring(pathMark, i);
                                mark = i + 1;
                                state = State.QUERY;
                                break;
                            case '#':
                                checkSegment(uri, segment, i, false);
                                _path = uri.substring(pathMark, i);
                                mark = i + 1;
                                state = State.FRAGMENT;
                                break;
                            case '/':
                                checkSegment(uri, segment, i, false);
                                segment = i + 1;
                                break;
                            case '.':
                                dot |= segment == i;
                                break;
                            case '%':
                                encoded = true;
                                escapedTwo = 1;
                                break;
                            case '2':
                                escapedTwo = escapedTwo == 1 ? 2 : 0;
                                break;
                            case 'f':
                            case 'F':
                                if (escapedTwo == 2)
                                    _ambiguous.add(Ambiguous.SEPARATOR);
                                escapedTwo = 0;
                                break;
                            case '5':
                                if (escapedTwo == 2)
                                    _ambiguous.add(Ambiguous.ENCODING);
                                escapedTwo = 0;
                                break;
                            default:
                                escapedTwo = 0;
                                break;
                        }
                        break;
                    }
                    case PARAM:
                    {
                        switch (c)
                        {
                            case '?':
                                _path = uri.substring(pathMark, i);
                                _param = uri.substring(mark, i);
                                mark = i + 1;
                                state = State.QUERY;
                                break;
                            case '#':
                                _path = uri.substring(pathMark, i);
                                _param = uri.substring(mark, i);
                                mark = i + 1;
                                state = State.FRAGMENT;
                                break;
                            case '/':
                                encoded = true;
                                segment = i + 1;
                                state = State.PATH;
                                break;
                            case ';':
                                // multiple parameters
                                mark = i + 1;
                                break;
                            default:
                                break;
                        }
                        break;
                    }
                    case QUERY:
                    {
                        if (c == '#')
                        {
                            _query = uri.substring(mark, i);
                            mark = i + 1;
                            state = State.FRAGMENT;
                        }
                        break;
                    }
                    case ASTERISK:
                    {
                        throw new IllegalArgumentException("Bad character '*'");
                    }
                    case FRAGMENT:
                    {
                        _fragment = uri.substring(mark, end);
                        i = end;
                        break;
                    }
                    default:
                        throw new IllegalStateException(state.toString());
                }
            }

            switch (state)
            {
                case START:
                case ASTERISK:
                    break;
                case SCHEME_OR_PATH:
                case HOST_OR_PATH:
                    _path = uri.substring(mark, end);
                    break;
                case HOST:
                    if (end > mark)
                        _host = uri.substring(mark, end);
                    break;
                case IPV6:
                    throw new IllegalArgumentException("No closing ']' for ipv6 in " + uri);
                case PORT:
                    _port = TypeUtil.parseInt(uri, mark, end - mark, 10);
                    break;
                case PARAM:
                    _path = uri.substring(pathMark, end);
                    _param = uri.substring(mark, end);
                    break;
                case PATH:
                    checkSegment(uri, segment, end, false);
                    _path = uri.substring(pathMark, end);
                    break;
                case QUERY:
                    _query = uri.substring(mark, end);
                    break;
                case FRAGMENT:
                    _fragment = uri.substring(mark, end);
                    break;
                default:
                    throw new IllegalStateException(state.toString());
            }

            if (!encoded && !dot)
            {
                if (_param == null)
                    _decodedPath = _path;
                else
                    _decodedPath = _path.substring(0, _path.length() - _param.length() - 1);
            }
            else if (_path != null)
            {
                String canonical = URIUtil.canonicalPath(_path);
                if (canonical == null)
                    throw new BadMessageException("Bad URI");
                _decodedPath = URIUtil.decodePath(canonical);
            }
        }