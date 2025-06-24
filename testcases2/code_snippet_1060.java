private void configureJwtProxySecureProvisioner(String infrastructure) {
    install(new FactoryModuleBuilder().build(JwtProxyProvisionerFactory.class));
    if (KubernetesInfrastructure.NAME.equals(infrastructure)) {
      install(
          new FactoryModuleBuilder()
              .build(
                  new TypeLiteral<JwtProxySecureServerExposerFactory<KubernetesEnvironment>>() {}));
      MapBinder.newMapBinder(
              binder(),
              new TypeLiteral<String>() {},
              new TypeLiteral<SecureServerExposerFactory<KubernetesEnvironment>>() {})
          .addBinding("jwtproxy")
          .to(new TypeLiteral<JwtProxySecureServerExposerFactory<KubernetesEnvironment>>() {});
    } else {
      install(
          new FactoryModuleBuilder()
              .build(
                  new TypeLiteral<JwtProxySecureServerExposerFactory<OpenShiftEnvironment>>() {}));
      MapBinder.newMapBinder(
              binder(),
              new TypeLiteral<String>() {},
              new TypeLiteral<SecureServerExposerFactory<OpenShiftEnvironment>>() {})
          .addBinding("jwtproxy")
          .to(new TypeLiteral<JwtProxySecureServerExposerFactory<OpenShiftEnvironment>>() {});
    }
  }