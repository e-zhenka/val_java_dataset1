@Before
    public void setUp() throws Exception {
        SecurityContextHolder.clearContext();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Map<String, List<DescribedApproval>> approvalsByClientId = new HashMap<String, List<DescribedApproval>>();

        DescribedApproval readApproval = new DescribedApproval();
        readApproval.setUserId("userId");
        readApproval.setClientId("app");
        readApproval.setScope("thing.read");
        readApproval.setStatus(APPROVED);
        readApproval.setDescription("Read your thing resources");

        DescribedApproval writeApproval = new DescribedApproval();
        writeApproval.setUserId("userId");
        writeApproval.setClientId("app");
        writeApproval.setScope("thing.write");
        writeApproval.setStatus(APPROVED);
        writeApproval.setDescription("Write to your thing resources");

        approvalsByClientId.put("app", Arrays.asList(readApproval, writeApproval));

        Mockito.when(approvalsService.getCurrentApprovalsByClientId()).thenReturn(approvalsByClientId);

        BaseClientDetails appClient = new BaseClientDetails("app","thing","thing.read,thing.write","authorization_code", "");
        appClient.addAdditionalInformation(ClientConstants.CLIENT_NAME, THE_ULTIMATE_APP);
        Mockito.when(clientDetailsService.loadClientByClientId("app")).thenReturn(appClient);
    }