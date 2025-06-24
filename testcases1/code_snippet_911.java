public URL getURL() throws MalformedURLException {
            String name = fileName;
            if (name.equals("hudson-cli.jar"))  name="jenkins-cli.jar";
            
            // Prevent the sandbox escaping (SECURITY-195)
            if (name.contains("..")) {
                throw new MalformedURLException("The specified file path " + fileName + " contains '..'. "
                        + "The path is not allowed due to security reasons");
            }
            
            URL res = Jenkins.getInstance().servletContext.getResource("/WEB-INF/" + name);
            if(res==null) {
                // during the development this path doesn't have the files.
                res = new URL(new File(".").getAbsoluteFile().toURI().toURL(),"target/jenkins/WEB-INF/"+name);
            }
            return res;
        }