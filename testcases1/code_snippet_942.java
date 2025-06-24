private String escapeString(final String string)
  {
    if (string == null || string.length() == 0) {
      return "\"\"";
    }
    char c = 0;
    int i;
    final int len = string.length();
    final StringBuilder sb = new StringBuilder(len + 4);
    String t;
    sb.append('"');
    for (i = 0; i < len; i += 1) {
      c = string.charAt(i);
      switch (c) {
        case '\\':
        case '"':
          sb.append('\\');
          sb.append(c);
          break;
        case '/':
          // if (b == '<') {
          sb.append('\\');
          // }
          sb.append(c);
          break;
        case '\b':
          sb.append("\\b");
          break;
        case '\t':
          sb.append("\\t");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\f':
          sb.append("\\f");
          break;
        case '\r':
          sb.append("\\r");
          break;
        default:
          if (c < ' ') {
            t = "000" + Integer.toHexString(c);
            sb.append("\\u" + t.substring(t.length() - 4));
          } else {
            if (escapeHtml == true) {
              switch (c) {
                case '<':
                  sb.append("&lt;");
                  break;
                case '>':
                  sb.append("&gt;");
                  break;
                case '&':
                  sb.append("&amp;");
                  break;
                case '"':
                  sb.append("&quot;");
                  break;
                case '\'':
                  sb.append("&#x27;");
                  break;
                case '/':
                  sb.append("&#x2F;");
                  break;
                default:
                  sb.append(c);
              }
            } else {
              sb.append(c);
            }
          }
      }
    }
    sb.append('"');
    return sb.toString();
  }