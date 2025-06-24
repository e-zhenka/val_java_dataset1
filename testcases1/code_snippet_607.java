protected void doSend(HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        // get changelog
        changes.insert(0, "<pre>");
        changes.append("</pre>");
        setProperty(PN_CHANGE_LOG, changes.toString());

        Writer out = response.getWriter();
        InputStream template = getClass().getResourceAsStream(TEMPLATE_NAME);
        Reader in = new BufferedReader(new InputStreamReader(template));
        StringBuffer varBuffer = new StringBuffer();
        int state = 0;
        int read;
        while ((read = in.read()) >= 0) {
            char c = (char) read;
            switch (state) {
                // initial
                case 0:
                    if (c == '$') {
                        state = 1;
                    } else {
                        out.write(c);
                    }
                    break;
                // $ read
                case 1:
                    if (c == '{') {
                        state = 2;
                    } else {
                        state = 0;
                        out.write('$');
                        out.write(c);
                    }
                    break;
                // { read
                case 2:
                    if (c == '}') {
                        state = 0;
                        Object prop = getProperty(varBuffer.toString());
                        if (prop != null) {
                            out.write(ResponseUtil.escapeXml(prop.toString()));
                        }
                        varBuffer.setLength(0);
                    } else {
                        varBuffer.append(c);
                    }
            }
        }
        in.close();
        out.flush();
    }