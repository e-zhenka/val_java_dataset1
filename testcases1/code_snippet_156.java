public FormValidation doMatchText(
                @QueryParameter("pattern") final String testPattern,
                @QueryParameter("testText") String testText,
                @QueryParameter("textSourceIsUrl") final boolean textSourceIsUrl) {
            if (textSourceIsUrl) {
                testText = testText.replaceAll("/\\./", "/").replaceAll("/view/change-requests", "");
                Matcher urlMatcher = URL_PATTERN.matcher(testText);
                if (urlMatcher.matches()) {
                    String[] urlParts = new String[NUM_OF_URL_PARTS];
                    for (int i = 0; i < urlParts.length; i++) {
                        urlParts[i] = urlMatcher.group(i + 1);
                    }

                    Run build = null;
                    ItemGroup getItemInstance;

                    if (urlParts[0].split("/job/").length > 1) {
                        /*
                         * We matched a folders job. Let's get the jobs up to the part were the next
                         * iteration can be continued from
                         */
                        StringBuilder fullFolderName = new StringBuilder();
                        /* The interestingJobParts string created below is meant to discard everything
                         * that comes before the first '/job' occurrent which is either nothing or the
                         * prefix from where jenkins is served, ie: http://localhost/jenkins/job/<job>/<buildNumber>
                         */
                        String[] interestingJobParts = urlParts[0].split("/job/", 2);
                        String[] jobParts = interestingJobParts[interestingJobParts.length - 1].split("/job/");
                        for (String part: jobParts) {
                            fullFolderName.append("/").append(part);
                        }
                        getItemInstance = (ItemGroup)Jenkins.getInstance().getItemByFullName(fullFolderName.toString());
                    } else {
                        getItemInstance = (ItemGroup)Jenkins.getInstance();
                    }

                    if (getItemInstance == null) {
                        throw new AssertionError("Folder not found!");
                    }

                    /*
                       Find out which of the following url types testText matches, if any,
                       and assign to build accordingly. The url types are checked in the
                       given order.
                       Type 1: .../<job>/<buildNumber>/
                       Type 2: .../<job>/<matrixInfo>/<buildNumber>/
                       Type 3: .../<job>/<buildNumber>/<matrixInfo>/
                     */

                    final Item itemFromPart2 = getItemInstance.getItem(urlParts[2]);
                    if (itemFromPart2 instanceof Job
                            && isValidBuildId(urlParts[3])) {
                        Job project = (Job)itemFromPart2;
                        build = getBuildById(project, urlParts[3]);
                    } else {
                        final Item itemFromPart1 = getItemInstance.getItem(urlParts[1]);
                        if (itemFromPart1 instanceof MatrixProject
                                && isValidBuildId(urlParts[3])) {
                            MatrixProject project = (MatrixProject)itemFromPart1;
                            MatrixConfiguration configuration = project.getItem(urlParts[2]);
                            build = getBuildById(configuration, urlParts[3]);
                        } else if (itemFromPart1 instanceof MatrixProject
                                && isValidBuildId(urlParts[2])) {
                            MatrixProject matrixProject = (MatrixProject)itemFromPart1;
                            MatrixConfiguration configuration = matrixProject.getItem(urlParts[3]);
                            build = getBuildById(configuration, urlParts[2]);
                        }
                    }
                    if (build != null) {
                        try {
                            final FailureReader failureReader = getFailureReader(testPattern);
                            final FoundIndication foundIndication = failureReader.scan(build);
                            if (foundIndication == null) {
                                return FormValidation.warning(Messages.StringDoesNotMatchPattern());
                            }
                            return FormValidation.okWithMarkup(foundIndication.getFirstMatchingLine());
                        } catch (IOException e) {
                            return FormValidation.error(Messages.FailedToScanFile_Error());
                        }
                    }
                }
                return FormValidation.error(Messages.InvalidURL_Error());
            } else {
                try {
                    if (testText.matches(testPattern)) {
                        return FormValidation.ok(Messages.StringMatchesPattern());
                    }
                    return FormValidation.warning(Messages.StringDoesNotMatchPattern());
                } catch (PatternSyntaxException e) {
                    return FormValidation.error(Messages.InvalidPattern_Error());
                }
            }
        }