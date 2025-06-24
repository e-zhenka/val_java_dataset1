public static boolean isSecureZooKeeper(Configuration conf) {
    // Detection for embedded HBase client with jaas configuration
    // defined for third party programs.
    try {
      javax.security.auth.login.Configuration testConfig =
          javax.security.auth.login.Configuration.getConfiguration();
      if (testConfig.getAppConfigurationEntry("Client") == null
          && testConfig.getAppConfigurationEntry(
            JaasConfiguration.CLIENT_KEYTAB_KERBEROS_CONFIG_NAME) == null
          && testConfig.getAppConfigurationEntry(
            JaasConfiguration.SERVER_KEYTAB_KERBEROS_CONFIG_NAME) == null) {
        return false;
      }
    } catch(Exception e) {
      // No Jaas configuration defined.
      return false;
    }

    // Master & RSs uses hbase.zookeeper.client.*
    return("kerberos".equalsIgnoreCase(conf.get("hbase.security.authentication")));
  }