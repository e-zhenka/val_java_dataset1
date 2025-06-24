public static RootPersistentEntity createAclEntity(String entityType, String uuid) {
        // Validate the uuid first, exception will be thrown if the uuid string is not a valid uuid
        UUID uuidObj = UUID.fromString(uuid);
        uuid = uuidObj.toString();

        if (CUBE_INSTANCE.equals(entityType)) {
            CubeInstance cubeInstance = new CubeInstance();
            cubeInstance.setUuid(uuid);

            return cubeInstance;
        }

        if (DATA_MODEL_DESC.equals(entityType)) {
            DataModelDesc modelInstance = new DataModelDesc();
            modelInstance.setUuid(uuid);

            return modelInstance;
        }

        if (JOB_INSTANCE.equals(entityType)) {
            JobInstance jobInstance = new JobInstance();
            jobInstance.setUuid(uuid);

            return jobInstance;
        }

        if (PROJECT_INSTANCE.equals(entityType)) {
            ProjectInstance projectInstance = new ProjectInstance();
            projectInstance.setUuid(uuid);

            return projectInstance;
        }

        throw new RuntimeException("Unsupported entity type!");
    }