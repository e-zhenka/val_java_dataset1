private static void processHeaderConfig(MultivaluedMap<String, String> httpHeaders, Object object, String key, String prefix) {
        try {
            String property = StringUtils.removeStart(key, prefix);
            Field field = object.getClass().getDeclaredField(StringUtils.uncapitalize(property));

            field.setAccessible(true);
            if (field.getType() == String.class) {
                field.set(object, httpHeaders.getFirst(key));
            } else if (field.getType() == int.class) {
                field.setInt(object, Integer.parseInt(httpHeaders.getFirst(key)));
            } else if (field.getType() == double.class) {
                field.setDouble(object, Double.parseDouble(httpHeaders.getFirst(key)));
            } else if (field.getType() == boolean.class) {
                field.setBoolean(object, Boolean.parseBoolean(httpHeaders.getFirst(key)));
            } else {
                //couldn't find a directly accessible field
                //try for setX(String s)
                String setter = StringUtils.uncapitalize(property);
                setter = "set"+setter.substring(0,1).toUpperCase(Locale.US)+setter.substring(1);
                Method m = null;
                try {
                    m = object.getClass().getMethod(setter, String.class);
                } catch (NoSuchMethodException e) {
                    //swallow
                }
                if (m != null) {
                    m.invoke(object, httpHeaders.getFirst(key));
                }
            }
        } catch (Throwable ex) {
            throw new WebApplicationException(String.format(Locale.ROOT,
                    "%s is an invalid %s header", key, X_TIKA_OCR_HEADER_PREFIX));
        }
    }