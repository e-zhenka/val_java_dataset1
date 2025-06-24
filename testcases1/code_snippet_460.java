@Override
	public Environment setUp(@SuppressWarnings("rawtypes")AbstractBuild build, Launcher launcher,
			final BuildListener listener) throws IOException, InterruptedException
	{
		DescriptorImpl DESCRIPTOR = Hudson.getInstance().getDescriptorByType(DescriptorImpl.class);
		String vnc2swf = Util.escape(Util.nullify(DESCRIPTOR.getVnc2swf()));
		if(vnc2swf.equals(CANT_FIND_VNC2SWF))
		{
			listener.fatalError("VNC Recorder: can't find 'vnc2swf' please check your jenkins global settings!");
			return null;
		}
		else 
		{
			File vnc2swfFile = new File(vnc2swf);
			if (!vnc2swfFile.exists())
			{
				listener.fatalError("VNC Recorder: can't find '" + vnc2swf + "' please check your jenkins global settings!");
				return null;
			}
		}

		final VncRecorder vr = new VncRecorder();
		vr.setLoggingStream(listener.getLogger());
//		final Logger vncLogger = vr.getLoggerForPrintStream(listener.getLogger());
		if (!SystemUtils.IS_OS_UNIX)
		{
			listener.fatalError("Feature \"Record VNC session\" works only under Unix/Linux!");
			return null;
		}
		String vncServReplaced = Util.replaceMacro(vncServ,build.getEnvironment(listener));
		if (vncServReplaced.indexOf(":") > 0 && vncServReplaced.split(":")[1].length() == 4 && vncServReplaced.split(":")[1].startsWith("59") )
		{
			vncServReplaced = vncServReplaced.replace(":59", ":");
		}
		
		String vncPasswFilePathReplaced = Util.replaceMacro(vncPasswFilePath,build.getEnvironment(listener));
		//String outFileBase = build.getEnvironment(listener).get("JOB_NAME") + "_" +  build.getEnvironment(listener).get("BUILD_NUMBER") + ".swf";
		if (outFileName == null || outFileName.equalsIgnoreCase("null"))
		{
			outFileName = "${JOB_NAME}_${BUILD_NUMBER}";
		}
		String outFileBase =  Util.replaceMacro(outFileName,build.getEnvironment(listener)) + ".swf";
		listener.getLogger().println("Recording from vnc server: " + vncServReplaced);
		listener.getLogger().println("Using vnc passwd file: " + vncPasswFilePathReplaced);
		//		listener.getLogger().printf("Using vnc passwd file: %s\n",vncPasswFilePath);	


		File vncPasswFile = new File(vncPasswFilePathReplaced);
		if (vncPasswFilePathReplaced.isEmpty())
		{
			listener.getLogger().println("VNC password file is an empty string, trying vnc connection without password");
			vncPasswFile = null;
		}
		else if (!vncPasswFile.exists())
		{
			listener.getLogger().println("Can't find " +vncPasswFile  +", trying vnc connection without password ");
			vncPasswFile = null;
		}

		File artifactsDir = build.getArtifactsDir();
		listener.getLogger().print(build.getUrl());
		if(!artifactsDir.exists())
		{
			if (!artifactsDir.mkdir())
			{
			  listener.error("Can't create " + artifactsDir.getAbsolutePath());
			}
		}

		if (outFileBase == null || outFileBase.equalsIgnoreCase("null.swf"))
		{
			outFileBase = build.getNumber() + ".swf";

		}
		final File outFileSwf = new File(artifactsDir,outFileBase); 
		final File outFileHtml = new File(outFileSwf.getAbsolutePath().replace(".swf", ".html"));

		final Date from = new Date();
		final Future<Integer> recordState = vr.record(vncServReplaced, outFileSwf.getAbsolutePath(), vncPasswFile,vnc2swf);

		return new Environment() {
			@Override
			public void buildEnvVars(Map<String, String> env) {
				//				env.put("PATH",env.get("PATH"));
				//				env.put("DISPLAY", vncServ);
				if (setDisplay && env != null && vncServ != null)
					env.put("DISPLAY",Util.replaceMacro(vncServ,env));
			}
			@Override
			public boolean tearDown(AbstractBuild build, BuildListener listener)
					throws IOException, InterruptedException {
				final Date to = new Date();
				if (recordState != null)
				{	
					recordState.cancel(true);
					Thread.sleep(1000);
				}
				if (removeIfSuccessful == null)
					removeIfSuccessful = false;


				if ((removeIfSuccessful && outFileSwf.exists()) && (build == null || build.getResult() == Result.SUCCESS || build.getResult() == null)  )
				{
					listener.getLogger().println("Build successful: Removing video file " + outFileSwf.getAbsolutePath() + " \n");
					
					if(!outFileSwf.delete())
					 listener.error("Can't delete " + outFileSwf.getAbsolutePath());
					
					if(!outFileHtml.delete())
	                     listener.error("Can't delete " + outFileHtml.getAbsolutePath());
					
					return true;
				}

				if (!outFileSwf.exists())
				{
					listener.error("File " + outFileSwf.getAbsolutePath() +" doesn't exist. \nFeature \"Record VNC session\" failed!");
					if (failJobIfFailed)
					    return false;
					else
					  return true;
				}  


				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd 'T' HH:mm:ss");
				listener.hyperlink("artifact/" + outFileHtml.getName(),"Video from " + sf.format(from) + " to " + sf.format(to));
				listener.getLogger().print("\n");
				//					String con = com.google.common.io.Files.toString(outFileHtml, Charset.forName("utf-8"));
				//					con = con.replaceAll("<embed src=\""+ outFileSwf.getName() +"\"", "<embed src=\""+ "artifact/" + outFileSwf.getName()  +"\"");
				//					ExpandableDetailsNote dn = new ExpandableDetailsNote(new Date().toString(),con);
				//					listener.annotate(dn);
				return true;
			}
		};

	}