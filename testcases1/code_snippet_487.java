private void readObject(java.io.ObjectInputStream s)
      throws java.io.IOException, ClassNotFoundException {
    s.defaultReadObject();

    // Read in array length and allocate array
    int length = s.readInt();
    this.longs = new AtomicLongArray(length);

    // Read in all elements in the proper order.
    for (int i = 0; i < length; i++) {
      set(i, s.readDouble());
    }
  }