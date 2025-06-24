@Override
	protected void configure() {
		super.configure();
		
		bind(JettyRunner.class).to(DefaultJettyRunner.class);
		bind(ServletContextHandler.class).toProvider(DefaultJettyRunner.class);
		
		bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
		
		bind(ValidatorFactory.class).toProvider(new com.google.inject.Provider<ValidatorFactory>() {

			@Override
			public ValidatorFactory get() {
				Configuration<?> configuration = Validation
						.byDefaultProvider()
						.configure()
						.messageInterpolator(new ParameterMessageInterpolator());
				return configuration.buildValidatorFactory();
			}
			
		}).in(Singleton.class);
		
		bind(Validator.class).toProvider(ValidatorProvider.class).in(Singleton.class);

		// configure markdown
		bind(MarkdownManager.class).to(DefaultMarkdownManager.class);		
		
		configurePersistence();
		configureRestServices();
		configureWeb();
		configureBuild();
		
		bind(GitConfig.class).toProvider(GitConfigProvider.class);

		/*
		 * Declare bindings explicitly instead of using ImplementedBy annotation as
		 * HK2 to guice bridge can only search in explicit bindings in Guice   
		 */
		bind(StorageManager.class).to(DefaultStorageManager.class);
		bind(SettingManager.class).to(DefaultSettingManager.class);
		bind(DataManager.class).to(DefaultDataManager.class);
		bind(TaskScheduler.class).to(DefaultTaskScheduler.class);
		bind(PullRequestCommentManager.class).to(DefaultPullRequestCommentManager.class);
		bind(CodeCommentManager.class).to(DefaultCodeCommentManager.class);
		bind(PullRequestManager.class).to(DefaultPullRequestManager.class);
		bind(PullRequestUpdateManager.class).to(DefaultPullRequestUpdateManager.class);
		bind(ProjectManager.class).to(DefaultProjectManager.class);
		bind(UserManager.class).to(DefaultUserManager.class);
		bind(PullRequestReviewManager.class).to(DefaultPullRequestReviewManager.class);
		bind(BuildManager.class).to(DefaultBuildManager.class);
		bind(BuildDependenceManager.class).to(DefaultBuildDependenceManager.class);
		bind(JobManager.class).to(DefaultJobManager.class);
		bind(LogManager.class).to(DefaultLogManager.class);
		bind(PullRequestBuildManager.class).to(DefaultPullRequestBuildManager.class);
		bind(MailManager.class).to(DefaultMailManager.class);
		bind(IssueManager.class).to(DefaultIssueManager.class);
		bind(IssueFieldManager.class).to(DefaultIssueFieldManager.class);
		bind(BuildParamManager.class).to(DefaultBuildParamManager.class);
		bind(UserAuthorizationManager.class).to(DefaultUserAuthorizationManager.class);
		bind(GroupAuthorizationManager.class).to(DefaultGroupAuthorizationManager.class);
		bind(PullRequestWatchManager.class).to(DefaultPullRequestWatchManager.class);
		bind(RoleManager.class).to(DefaultRoleManager.class);
		bind(CommitInfoManager.class).to(DefaultCommitInfoManager.class);
		bind(UserInfoManager.class).to(DefaultUserInfoManager.class);
		bind(BatchWorkManager.class).to(DefaultBatchWorkManager.class);
		bind(GroupManager.class).to(DefaultGroupManager.class);
		bind(MembershipManager.class).to(DefaultMembershipManager.class);
		bind(PullRequestChangeManager.class).to(DefaultPullRequestChangeManager.class);
		bind(CodeCommentReplyManager.class).to(DefaultCodeCommentReplyManager.class);
		bind(AttachmentStorageManager.class).to(DefaultAttachmentStorageManager.class);
		bind(CodeCommentRelationInfoManager.class).to(DefaultCodeCommentRelationInfoManager.class);
		bind(CodeCommentRelationManager.class).to(DefaultCodeCommentRelationManager.class);
		bind(WorkExecutor.class).to(DefaultWorkExecutor.class);
		bind(PullRequestNotificationManager.class);
		bind(CommitNotificationManager.class);
		bind(BuildNotificationManager.class);
		bind(IssueNotificationManager.class);
		bind(EntityReferenceManager.class);
		bind(CodeCommentNotificationManager.class);
		bind(CodeCommentManager.class).to(DefaultCodeCommentManager.class);
		bind(IssueWatchManager.class).to(DefaultIssueWatchManager.class);
		bind(IssueChangeManager.class).to(DefaultIssueChangeManager.class);
		bind(IssueVoteManager.class).to(DefaultIssueVoteManager.class);
		bind(MilestoneManager.class).to(DefaultMilestoneManager.class);
		bind(Session.class).toProvider(SessionProvider.class);
		bind(EntityManager.class).toProvider(SessionProvider.class);
		bind(SessionFactory.class).toProvider(SessionFactoryProvider.class);
		bind(EntityManagerFactory.class).toProvider(SessionFactoryProvider.class);
		bind(IssueCommentManager.class).to(DefaultIssueCommentManager.class);
		bind(IssueQuerySettingManager.class).to(DefaultIssueQuerySettingManager.class);
		bind(PullRequestQuerySettingManager.class).to(DefaultPullRequestQuerySettingManager.class);
		bind(CodeCommentQuerySettingManager.class).to(DefaultCodeCommentQuerySettingManager.class);
		bind(CommitQuerySettingManager.class).to(DefaultCommitQuerySettingManager.class);
		bind(BuildQuerySettingManager.class).to(DefaultBuildQuerySettingManager.class);
		bind(WebHookManager.class);

		contribute(ObjectMapperConfigurator.class, GitObjectMapperConfigurator.class);
	    contribute(ObjectMapperConfigurator.class, HibernateObjectMapperConfigurator.class);
	    
		bind(Realm.class).to(OneAuthorizingRealm.class);
		bind(RememberMeManager.class).to(OneRememberMeManager.class);
		bind(WebSecurityManager.class).to(OneWebSecurityManager.class);
		bind(FilterChainResolver.class).to(OneFilterChainResolver.class);
		bind(BasicAuthenticationFilter.class);
		bind(PasswordService.class).to(OnePasswordService.class);
		bind(ShiroFilter.class);
		install(new ShiroAopModule());
        contribute(FilterChainConfigurator.class, new FilterChainConfigurator() {

            @Override
            public void configure(FilterChainManager filterChainManager) {
                filterChainManager.createChain("/**/info/refs", "noSessionCreation, authcBasic");
                filterChainManager.createChain("/**/git-upload-pack", "noSessionCreation, authcBasic");
                filterChainManager.createChain("/**/git-receive-pack", "noSessionCreation, authcBasic");
            }
            
        });
        contributeFromPackage(Authenticator.class, Authenticator.class);
        
		contribute(ImplementationProvider.class, new ImplementationProvider() {

			@Override
			public Class<?> getAbstractClass() {
				return JobExecutor.class;
			}

			@Override
			public Collection<Class<?>> getImplementations() {
				return Sets.newHashSet(AutoDiscoveredJobExecutor.class);
			}
			
		});
		
		contribute(CodePullAuthorizationSource.class, DefaultJobManager.class);
        
		bind(IndexManager.class).to(DefaultIndexManager.class);
		bind(SearchManager.class).to(DefaultSearchManager.class);
		
		bind(EntityValidator.class).to(DefaultEntityValidator.class);
		
		bind(GitFilter.class);
		bind(GitPreReceiveCallback.class);
		bind(GitPostReceiveCallback.class);
		
	    bind(ExecutorService.class).toProvider(new Provider<ExecutorService>() {

			@Override
			public ExecutorService get() {
		        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, 
		        		new SynchronousQueue<Runnable>()) {

					@Override
					public void execute(Runnable command) {
						try {
							super.execute(SecurityUtils.inheritSubject(command));
						} catch (RejectedExecutionException e) {
							if (!isShutdown())
								throw ExceptionUtils.unchecked(e);
						}
					}

		        };
			}
	    	
	    }).in(Singleton.class);
	    
	    bind(ForkJoinPool.class).toInstance(new ForkJoinPool() {

			@Override
			public ForkJoinTask<?> submit(Runnable task) {
				return super.submit(SecurityUtils.inheritSubject(task));
			}

			@Override
			public void execute(Runnable task) {
				super.execute(SecurityUtils.inheritSubject(task));
			}

			@Override
			public <T> ForkJoinTask<T> submit(Callable<T> task) {
				return super.submit(SecurityUtils.inheritSubject(task));
			}

			@Override
			public <T> ForkJoinTask<T> submit(Runnable task, T result) {
				return super.submit(SecurityUtils.inheritSubject(task), result);
			}

			@Override
			public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
					throws InterruptedException, ExecutionException {
				return super.invokeAny(SecurityUtils.inheritSubject(tasks));
			}

			@Override
			public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				return super.invokeAny(SecurityUtils.inheritSubject(tasks), timeout, unit);
			}

			@Override
			public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, 
					long timeout, TimeUnit unit) throws InterruptedException {
				return super.invokeAll(SecurityUtils.inheritSubject(tasks), timeout, unit);
			}

			@Override
			public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
				return super.invokeAll(SecurityUtils.inheritSubject(tasks));
			}

	    });
	}