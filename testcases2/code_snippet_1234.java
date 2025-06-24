@Override
        protected void configure(HttpSecurity http) throws Exception {

            RESTRequestParameterProcessingFilter restAuthenticationFilter = new RESTRequestParameterProcessingFilter();
            restAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
            restAuthenticationFilter.setSecurityService(securityService);
            restAuthenticationFilter.setEventPublisher(eventPublisher);
            http = http.addFilterBefore(restAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            http
                    .csrf()
                    .requireCsrfProtectionMatcher(csrfSecurityRequestMatcher)
                    .and().headers()
                    .frameOptions()
                    .sameOrigin()
                    .and().authorizeRequests()
                    .antMatchers("/recover*", "/accessDenied*",
                            "/style/**", "/icons/**", "/flash/**", "/script/**",
                            "/sonos/**", "/crossdomain.xml", "/login", "/error")
                    .permitAll()
                    .antMatchers("/personalSettings*", "/passwordSettings*",
                            "/playerSettings*", "/shareSettings*", "/passwordSettings*")
                    .hasRole("SETTINGS")
                    .antMatchers("/generalSettings*", "/advancedSettings*", "/userSettings*",
                            "/musicFolderSettings*", "/databaseSettings*", "/transcodeSettings*", "/rest/startScan*")
                    .hasRole("ADMIN")
                    .antMatchers("/deletePlaylist*", "/savePlaylist*", "/db*")
                    .hasRole("PLAYLIST")
                    .antMatchers("/download*")
                    .hasRole("DOWNLOAD")
                    .antMatchers("/upload*")
                    .hasRole("UPLOAD")
                    .antMatchers("/createShare*")
                    .hasRole("SHARE")
                    .antMatchers("/changeCoverArt*", "/editTags*")
                    .hasRole("COVERART")
                    .antMatchers("/setMusicFileInfo*")
                    .hasRole("COMMENT")
                    .antMatchers("/podcastReceiverAdmin*")
                    .hasRole("PODCAST")
                    .antMatchers("/**")
                    .hasRole("USER")
                    .anyRequest().authenticated()
                    .and().formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .defaultSuccessUrl("/index", true)
                    .failureUrl(FAILURE_URL)
                    .usernameParameter("j_username")
                    .passwordParameter("j_password")
                    // see http://docs.spring.io/spring-security/site/docs/3.2.4.RELEASE/reference/htmlsingle/#csrf-logout
                    .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")).logoutSuccessUrl(
                    "/login?logout")
                    .and().rememberMe().key("airsonic");
        }