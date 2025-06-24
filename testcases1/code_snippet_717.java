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
            else
            {
                if (!components.contains(desc.getName()))
                {
                    throw new InvalidClassException(
                          "unexpected class: ", desc.getName());
                }
            }
            return super.resolveClass(desc);
        }