@PreAuthorize(Constant.ACCESS_HAS_ROLE_ADMIN
            + " or hasPermission(#cube, 'ADMINISTRATION') or hasPermission(#cube, 'MANAGEMENT')")
    public void migrateCube(CubeInstance cube, String projectName) {
        KylinConfig config = KylinConfig.getInstanceFromEnv();
        if (!config.isAllowAutoMigrateCube()) {
            throw new InternalErrorException("One click migration is disabled, please contact your ADMIN");
        }

        for (CubeSegment segment : cube.getSegments()) {
            if (segment.getStatus() != SegmentStatusEnum.READY) {
                throw new InternalErrorException(
                        "At least one segment is not in READY state. Please check whether there are Running or Error jobs.");
            }
        }

        String srcCfgUri = config.getAutoMigrateCubeSrcConfig();
        String dstCfgUri = config.getAutoMigrateCubeDestConfig();

        Preconditions.checkArgument(StringUtils.isNotEmpty(srcCfgUri), "Source configuration should not be empty.");
        Preconditions.checkArgument(StringUtils.isNotEmpty(dstCfgUri),
                "Destination configuration should not be empty.");

        String stringBuilder = ("%s/bin/kylin.sh org.apache.kylin.tool.CubeMigrationCLI %s %s %s %s %s %s true true");
        String cmd = String.format(Locale.ROOT, stringBuilder, KylinConfig.getKylinHome(), srcCfgUri, dstCfgUri,
                cube.getName(), projectName, config.isAutoMigrateCubeCopyAcl(), config.isAutoMigrateCubePurge());

        logger.info("One click migration cmd: " + cmd);

        CliCommandExecutor exec = new CliCommandExecutor();
        PatternedLogger patternedLogger = new PatternedLogger(logger);

        try {
            exec.execute(cmd, patternedLogger);
        } catch (IOException e) {
            throw new InternalErrorException("Failed to perform one-click migrating", e);
        }
    }