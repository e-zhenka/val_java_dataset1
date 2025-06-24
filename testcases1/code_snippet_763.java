public <T> T readObject() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStreamEx(in,
                getClass().getClassLoader(), ClassFilter.DEFAULT);
        return (T)ois.readObject();
    }