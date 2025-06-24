public static String checkParameter(String commandParameter) {
        String repaired = commandParameter.replaceAll(COMMAND_INJECT_REX, "");
        if (repaired.length() != commandParameter.length()) {
            logger.info("Detected illegal character in command {}, replace it to {}.", commandParameter, repaired);
        }
        return repaired;
    }