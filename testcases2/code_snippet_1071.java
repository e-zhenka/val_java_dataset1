public URL getURL() throws MalformedURLException {
            String name = fileName;
            if (name.equals("hudson-cli.jar"))  name="jenkins-cli.jar";
            URL res = Jenkins.getInstance().servletContext.getResource("/WEB-INF/" + name);
            if(res==null) {
                // during the development this path doesn't have the files.
                res = new URL(new File(".").getAbsoluteFile().toURI().toURL(),"target/jenkins/WEB-INF/"+name);
            }
            return res;
        }