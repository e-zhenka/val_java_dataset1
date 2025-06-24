@SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "False positive")
    public synchronized int run(String[] args) throws Exception{
        if (used) {
            throw new IllegalStateException("CLI instance already used");
        }

        used = true;

        CLIParser parser = new CLIParser("oozie-setup.sh", HELP_INFO);
        String oozieHome = System.getProperty(OOZIE_HOME);
        parser.addCommand(HELP_CMD, "", "display usage for all commands or specified command", new Options(), false);
        parser.addCommand(CREATE_CMD, "", "create a new timestamped version of oozie sharelib",
                createUpgradeOptions(CREATE_CMD), false);
        parser.addCommand(UPGRADE_CMD, "",
                "[deprecated][use command \"create\" to create new version]   upgrade oozie sharelib \n",
                createUpgradeOptions(UPGRADE_CMD), false);

        try {
            final CLIParser.Command command = parser.parse(args);
            String sharelibAction = command.getName();

            if (sharelibAction.equals(HELP_CMD)){
                parser.showHelp(command.getCommandLine());
                return 0;
            }

            if (!command.getCommandLine().hasOption(FS_OPT)){
                throw new Exception("-fs option must be specified");
            }

            int threadPoolSize = Integer.valueOf(command.getCommandLine().getOptionValue(CONCURRENCY_OPT, "1"));
            File srcFile = null;

            //Check whether user provided locallib
            if (command.getCommandLine().hasOption(LIB_OPT)){
                srcFile = new File(command.getCommandLine().getOptionValue(LIB_OPT));
            }
            else {
                //Since user did not provide locallib, find the default one under oozie home dir
                Collection<File> files =
                        FileUtils.listFiles(new File(oozieHome), new WildcardFileFilter("oozie-sharelib*.tar.gz"), null);

                if (files.size() > 1){
                    throw new IOException("more than one sharelib tar found at " + oozieHome);
                }

                if (files.isEmpty()){
                    throw new IOException("default sharelib tar not found in oozie home dir: " + oozieHome);
                }

                srcFile = files.iterator().next();
            }

            Map<String, String> extraLibs = new HashMap<>();
            if (command.getCommandLine().hasOption(EXTRALIBS)) {
                String[] param = command.getCommandLine().getOptionValues(EXTRALIBS);
                extraLibs = getExtraLibs(param);
            }

           File temp = Files.createTempDirectory("oozie").toFile();
            temp.deleteOnExit();

            //Check whether the lib is a tar file or folder
            if (!srcFile.isDirectory()){
                FileUtil.unTar(srcFile, temp);
                srcFile = new File(temp.toString() + "/share/lib");
            }
            else {
                //Get the lib directory since it's a folder
                srcFile = new File(srcFile, "lib");
            }

            String hdfsUri = command.getCommandLine().getOptionValue(FS_OPT);
            Path srcPath = new Path(srcFile.toString());

            Services services = new Services();
            services.getConf().set(Services.CONF_SERVICE_CLASSES,
                "org.apache.oozie.service.LiteWorkflowAppService, org.apache.oozie.service.HadoopAccessorService");
            services.getConf().set(Services.CONF_SERVICE_EXT_CLASSES, "");
            services.init();
            WorkflowAppService lwas = services.get(WorkflowAppService.class);
            HadoopAccessorService has = services.get(HadoopAccessorService.class);
            Path dstPath = lwas.getSystemLibPath();

            URI uri = new Path(hdfsUri).toUri();
            Configuration fsConf = has.createConfiguration(uri.getAuthority());
            FileSystem fs = FileSystem.get(uri, fsConf);

            if (!fs.exists(dstPath)) {
                fs.mkdirs(dstPath);
            }
            ECPolicyDisabler.tryDisableECPolicyForPath(fs, dstPath);

            if (sharelibAction.equals(CREATE_CMD) || sharelibAction.equals(UPGRADE_CMD)){
                dstPath= new Path(dstPath.toString() +  Path.SEPARATOR +  SHARE_LIB_PREFIX + getTimestampDirectory()  );
            }

            System.out.println("the destination path for sharelib is: " + dstPath);

            checkIfSourceFilesExist(srcFile);
            copyToSharelib(threadPoolSize, srcFile, srcPath, dstPath, fs);
            copyExtraLibs(threadPoolSize, extraLibs, dstPath, fs);

            if (sharelibAction.equals(CREATE_CMD) || sharelibAction.equals(UPGRADE_CMD)) {
                applySharelibPermission(fs, dstPath);
            }

            services.destroy();
            FileUtils.deleteDirectory(temp);

            return 0;
        }
        catch (ParseException ex) {
            System.err.println("Invalid sub-command: " + ex.getMessage());
            System.err.println();
            System.err.println(parser.shortHelp());
            return 1;
        }
        catch (NumberFormatException ex) {
            logError("Invalid configuration value: ", ex);
            return 1;
        }
        catch (Exception ex) {
            logError(ex.getMessage(), ex);
            return 1;
        }
    }