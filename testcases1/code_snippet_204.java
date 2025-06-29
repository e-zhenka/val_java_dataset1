@Override
  public ExitCode runWithoutHelp(CommandRunnerParams params)
      throws IOException, InterruptedException {

    if (saveFilename != null && loadFilename != null) {
      params.getConsole().printErrorText("Can't use both --load and --save");
      return ExitCode.COMMANDLINE_ERROR;
    }

    if (saveFilename != null) {
      invalidateChanges(params);
      RemoteDaemonicParserState state = params.getParser().storeParserState(params.getCell());
      try (FileOutputStream fos = new FileOutputStream(saveFilename);
          ZipOutputStream zipos = new ZipOutputStream(fos)) {
        zipos.putNextEntry(new ZipEntry("parser_data"));
        try (ObjectOutputStream oos = new ObjectOutputStream(zipos)) {
          oos.writeObject(state);
        }
      }
    } else if (loadFilename != null) {
      try (FileInputStream fis = new FileInputStream(loadFilename);
          ZipInputStream zipis = new ZipInputStream(fis)) {
        ZipEntry entry = zipis.getNextEntry();
        Preconditions.checkState(entry.getName().equals("parser_data"));
        try (ObjectInputStream ois = new ObjectInputStream(zipis)) {
          RemoteDaemonicParserState state;
          try {
            state = (RemoteDaemonicParserState) ois.readObject();
          } catch (ClassNotFoundException e) {
            params.getConsole().printErrorText("Invalid file format");
            return ExitCode.COMMANDLINE_ERROR;
          }
          params.getParser().restoreParserState(state, params.getCell());
        }
      }
      invalidateChanges(params);

      ParserConfig configView = params.getBuckConfig().getView(ParserConfig.class);
      if (configView.isParserCacheMutationWarningEnabled()) {
        params
            .getConsole()
            .printErrorText(
                params
                    .getConsole()
                    .getAnsi()
                    .asWarningText(
                        "WARNING: Buck injected a parser state that may not match the local state."));
      }
    }

    return ExitCode.SUCCESS;
  }