protected Class<?> resolveClass(ObjectStreamClass desc)
            throws IOException,
            ClassNotFoundException
        {
            if (!found)
            {
                if (!desc.getName().equals(mainClass.getName()))
                {
                    throw new InvalidClassException(
                        "unexpected class: ", desc.getName());
                }
                else
                {
                    found = true;
                }
            }
            return super.resolveClass(desc);
        }