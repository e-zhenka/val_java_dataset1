private static void processHeaderConfig(MultivaluedMap<String, String> httpHeaders, Object object, String key, String prefix) {

        try {String property = StringUtils.removeStart(key, prefix);
            Field field = null;
            try {
                field = object.getClass().getDeclaredField(StringUtils.uncapitalize(property));
            } catch (NoSuchFieldException e) {
                //swallow
            }
            String setter = property;
            setter = "set"+setter.substring(0,1).toUpperCase(Locale.US)+setter.substring(1);
            //default assume string class
            //if there's a more specific type, e.g. double, int, boolean
            //try that.
            Class clazz = String.class;
            if (field != null) {
                if (field.getType() == int.class || field.getType() == Integer.class) {
                    clazz = int.class;
                } else if (field.getType() == double.class) {
                    clazz = double.class;
                } else if (field.getType() == Double.class) {
                    clazz = Double.class;
                } else if (field.getType() == float.class) {
                    clazz = float.class;
                } else if (field.getType() == Float.class) {
                    clazz = Float.class;
                } else if (field.getType() == boolean.class) {
                    clazz = boolean.class;
                } else if (field.getType() == Boolean.class) {
                    clazz = Boolean.class;
                }
            }

            Method m = tryToGetMethod(object, setter, clazz);
            //if you couldn't find more specific setter, back off
            //to string setter and try that.
            if (m == null && clazz != String.class) {
                m = tryToGetMethod(object, setter, String.class);
            }

            if (m != null) {
                String val = httpHeaders.getFirst(key);
                val = val.trim();
                if (clazz == String.class) {
                    checkTrustWorthy(setter, val);
                    m.invoke(object, val);
                } else if (clazz == int.class || clazz == Integer.class) {
                    m.invoke(object, Integer.parseInt(val));
                } else if (clazz == double.class || clazz == Double.class) {
                    m.invoke(object, Double.parseDouble(val));
                } else if (clazz == boolean.class || clazz == Boolean.class) {
                    m.invoke(object, Boolean.parseBoolean(val));
                } else if (clazz == float.class || clazz == Float.class) {
                    m.invoke(object, Float.parseFloat(val));
                } else {
                    throw new IllegalArgumentException("setter must be String, int, float, double or boolean...for now");
                }
            } else {
                throw new NoSuchMethodException("Couldn't find: "+setter);
            }

        } catch (Throwable ex) {
            throw new WebApplicationException(String.format(Locale.ROOT,
                    "%s is an invalid %s header", key, X_TIKA_OCR_HEADER_PREFIX));
        }
    }