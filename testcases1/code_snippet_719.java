@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .rememberMe().disable()
                .authorizeRequests()
                    .anyRequest().fullyAuthenticated()
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // x509
        http.addFilterBefore(x509FilterBean(), AnonymousAuthenticationFilter.class);

        // jwt
        http.addFilterBefore(jwtFilterBean(), AnonymousAuthenticationFilter.class);

        // otp
        http.addFilterBefore(otpFilterBean(), AnonymousAuthenticationFilter.class);

        // knox
        http.addFilterBefore(knoxFilterBean(), AnonymousAuthenticationFilter.class);

        // anonymous
        http.anonymous().authenticationFilter(anonymousFilterBean());
    }