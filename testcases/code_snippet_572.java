private void initialJpsClassList(String packageName) throws Exception {
                Package pk = Package.getPackage(packageName);
                List<Class> classesForPackage = new ArrayList<>();
                if (pk != null) {
                        classesForPackage.addAll(getClassesForPackage(pk));
                } else {
                        classesForPackage.addAll(getClassesForPackage(packageName));
                }
                for (Class<Object> clazz : classesForPackage) {

                        jpaClasses.add(clazz);
                }
        }