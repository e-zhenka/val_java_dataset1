private static Object deserialize(String serialized) throws ClassNotFoundException, IOException {
        byte[] bytes = Base64.decode(serialized);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(bis);
            DelegatingSerializationFilter.builder()
                    .addAllowedClass(KerberosTicket.class)
                    .addAllowedClass(KerberosPrincipal.class)
                    .addAllowedClass(InetAddress.class)
                    .addAllowedPattern("javax.security.auth.kerberos.KeyImpl")
                    .setFilter(in);
            return in.readObject();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }