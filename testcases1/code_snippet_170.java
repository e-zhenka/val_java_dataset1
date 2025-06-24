@Override
    public Response processControlCommand(ControlCommand command) throws Exception {
        String control = command.getCommand();
        if (control != null && control.equals("shutdown")) {
            System.exit(0);
        }
        return null;
    }