@Override
   public boolean apply(Run<?, ?> run) {
      boolean retVal = false;
      M2ReleaseBadgeAction a = run.getAction(M2ReleaseBadgeAction.class);
      if (a != null) {
          if (!run.isBuilding()) {
              if (!a.isDryRun() && run.getResult() == Result.SUCCESS) {
                  retVal = true;
              }
          }
      }
      return retVal;
    }