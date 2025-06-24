public void createWebXmlDigester(boolean namespaceAware,
            boolean validation) {
        
        if (!namespaceAware && !validation) {
            if (webDigesters[0] == null) {
                webDigesters[0] = DigesterFactory.newDigester(validation,
                        namespaceAware, webRuleSet);
                webFragmentDigesters[0] = DigesterFactory.newDigester(validation,
                        namespaceAware, webFragmentRuleSet);
            }
            webDigester = webDigesters[0];
            webFragmentDigester = webFragmentDigesters[0];
            
        } else if (!namespaceAware && validation) {
            if (webDigesters[1] == null) {
                webDigesters[1] = DigesterFactory.newDigester(validation,
                        namespaceAware, webRuleSet);
                webFragmentDigesters[1] = DigesterFactory.newDigester(validation,
                        namespaceAware, webFragmentRuleSet);
            }
            webDigester = webDigesters[1];
            webFragmentDigester = webFragmentDigesters[1];
            
        } else if (namespaceAware && !validation) {
            if (webDigesters[2] == null) {
                webDigesters[2] = DigesterFactory.newDigester(validation,
                        namespaceAware, webRuleSet);
                webFragmentDigesters[2] = DigesterFactory.newDigester(validation,
                        namespaceAware, webFragmentRuleSet);
            }
            webDigester = webDigesters[2];
            webFragmentDigester = webFragmentDigesters[2];
            
        } else {
            if (webDigesters[3] == null) {
                webDigesters[3] = DigesterFactory.newDigester(validation,
                        namespaceAware, webRuleSet);
                webFragmentDigesters[3] = DigesterFactory.newDigester(validation,
                        namespaceAware, webFragmentRuleSet);
            }
            webDigester = webDigesters[3];
            webFragmentDigester = webFragmentDigesters[3];
        }
    }