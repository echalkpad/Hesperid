////////////////////////////////////////////////////////////////////////////////////////////////////
// Copyright 2011 Astina AG, Zurich
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
////////////////////////////////////////////////////////////////////////////////////////////////////
package ch.astina.hesperid.web.services;

import ch.astina.hesperid.dao.RoleDAO;
import ch.astina.hesperid.dao.UserDAO;
import ch.astina.hesperid.dao.hibernate.HibernateRoleDAO;
import ch.astina.hesperid.dao.hibernate.HibernateUserDAO;
import ch.astina.hesperid.web.services.springsecurity.LogoutService;
import ch.astina.hesperid.web.services.springsecurity.RequestInvocationDefinition;
import ch.astina.hesperid.web.services.springsecurity.SaltSourceService;
import ch.astina.hesperid.web.services.springsecurity.SpringSecurityServices;
import ch.astina.hesperid.web.services.springsecurity.internal.HttpServletRequestFilterWrapper;
import ch.astina.hesperid.web.services.springsecurity.internal.LogoutServiceImpl;
import ch.astina.hesperid.web.services.springsecurity.internal.RequestFilterWrapper;
import ch.astina.hesperid.web.services.springsecurity.internal.SaltSourceImpl;
import ch.astina.hesperid.web.services.springsecurity.internal.SecurityChecker;
import ch.astina.hesperid.web.services.springsecurity.internal.SpringSecurityExceptionTranslationFilter;
import ch.astina.hesperid.web.services.springsecurity.internal.SpringSecurityWorker;
import ch.astina.hesperid.web.services.springsecurity.internal.StaticSecurityChecker;
import ch.astina.hesperid.web.services.springsecurity.internal.T5AccessDeniedHandler;
import ch.astina.hesperid.web.services.users.CarpetUserDetailsService;
import ch.astina.hesperid.web.services.users.UserService;
import ch.astina.hesperid.web.services.users.impl.UserServiceImpl;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Value;
import org.apache.tapestry5.services.AliasContribution;
import org.apache.tapestry5.services.ComponentClassTransformWorker;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.springframework.security.AccessDecisionManager;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.AuthenticationTrustResolver;
import org.springframework.security.AuthenticationTrustResolverImpl;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.context.HttpSessionContextIntegrationFilter;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.security.intercept.web.DefaultFilterInvocationDefinitionSource;
import org.springframework.security.intercept.web.FilterSecurityInterceptor;
import org.springframework.security.intercept.web.RequestKey;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.ProviderManager;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationProvider;
import org.springframework.security.providers.anonymous.AnonymousProcessingFilter;
import org.springframework.security.providers.dao.DaoAuthenticationProvider;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;
import org.springframework.security.providers.rememberme.RememberMeAuthenticationProvider;
import org.springframework.security.ui.AuthenticationEntryPoint;
import org.springframework.security.ui.basicauth.BasicProcessingFilter;
import org.springframework.security.ui.logout.LogoutHandler;
import org.springframework.security.ui.logout.SecurityContextLogoutHandler;
import org.springframework.security.ui.rememberme.RememberMeProcessingFilter;
import org.springframework.security.ui.rememberme.RememberMeServices;
import org.springframework.security.ui.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilterEntryPoint;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.memory.UserAttribute;
import org.springframework.security.userdetails.memory.UserAttributeEditor;
import org.springframework.security.util.AntUrlPathMatcher;
import org.springframework.security.vote.AccessDecisionVoter;
import org.springframework.security.vote.AffirmativeBased;
import org.springframework.security.vote.RoleVoter;
import org.springframework.security.wrapper.SecurityContextHolderAwareRequestFilter;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * 
 * @author Ivan Dubrov
 * @author Robin Helgelin
 * @author Michael Gerzabek
 */
public class SecurityModule
{
    @SuppressWarnings("unchecked")
    public static void bind(final ServiceBinder binder)
    {
        binder.bind(LogoutService.class, LogoutServiceImpl.class).withMarker(
                SpringSecurityServices.class);

        binder.bind(AuthenticationTrustResolver.class, AuthenticationTrustResolverImpl.class).withMarker(SpringSecurityServices.class);

        binder.bind(UserDAO.class, HibernateUserDAO.class).withMarker(SpringSecurityServices.class);
        binder.bind(RoleDAO.class, HibernateRoleDAO.class).withMarker(SpringSecurityServices.class);
        binder.bind(UserService.class, UserServiceImpl.class);
    }

    @Marker(SpringSecurityServices.class)
    public static PasswordEncoder buildPasswordEncoder()
    {
        return new ShaPasswordEncoder();
    }

    public static void contributeAlias(@SpringSecurityServices SaltSourceService saltSource,
            @SpringSecurityServices AuthenticationProcessingFilter authenticationProcessingFilter,
            Configuration<AliasContribution<?>> configuration)
    {
        configuration.add(AliasContribution.create(SaltSourceService.class, saltSource));

        configuration.add(AliasContribution.create(AuthenticationProcessingFilter.class,
                authenticationProcessingFilter));
    }

    @Marker(SpringSecurityServices.class)
    public static SaltSourceService buildSaltSource(
            @Inject @Value("${spring-security.password.salt}") final String salt) throws Exception
    {
        SaltSourceImpl saltSource = new SaltSourceImpl();
        saltSource.setSystemWideSalt(salt);
        saltSource.afterPropertiesSet();
        return saltSource;
    }

    public static void contributeFactoryDefaults(
            final MappedConfiguration<String, String> configuration)
    {
        // spring security default config values
        configuration.add("spring-security.check.url", "/j_spring_security_check");
        configuration.add("spring-security.failure.url", "/login/failed");
        configuration.add("spring-security.target.url", "/");
        configuration.add("spring-security.afterlogout.url", "/");
        configuration.add("spring-security.accessDenied.url", "");
        configuration.add("spring-security.force.ssl.login", "false");
        configuration.add("spring-security.rememberme.key", "REMEMBERMEKEY");
        configuration.add("spring-security.rememberme.always", "false");
        configuration.add("spring-security.rememberme.lifetime", "1209600"); // 14 days
        configuration.add("spring-security.loginform.url", "/login");
        configuration.add("spring-security.anonymous.key", "spring_anonymous");
        configuration.add("spring-security.anonymous.attribute", "anonymous,ROLE_ANONYMOUS");
        configuration.add("spring-security.password.salt", "DEADBEEF");
    }

    /**
     * Adds spring security code to the tapestry proxies for security checks.
     */
    public static void contributeComponentClassTransformWorker(
            OrderedConfiguration<ComponentClassTransformWorker> configuration,
            SecurityChecker securityChecker)
    {
        configuration.add("SpringSecurity", new SpringSecurityWorker(securityChecker));
    }

    /**
     * Add filters to the HttpServletRequestHandler. This allows to control the request before
     * Tapestry handles it.
     */
    public static void contributeHttpServletRequestHandler(
            OrderedConfiguration<HttpServletRequestFilter> configuration,
            @InjectService("HttpSessionContextIntegrationFilter") HttpServletRequestFilter httpSessionContextIntegrationFilter,
            @InjectService("AuthenticationProcessingFilter") HttpServletRequestFilter authenticationProcessingFilter,
            @InjectService("BasicProcessingFilter") HttpServletRequestFilter basicProcessingFilter,
            @InjectService("RememberMeProcessingFilter") HttpServletRequestFilter rememberMeProcessingFilter,
            @InjectService("SecurityContextHolderAwareRequestFilter") HttpServletRequestFilter securityContextHolderAwareRequestFilter,
            @InjectService("AnonymousProcessingFilter") HttpServletRequestFilter anonymousProcessingFilter,
            @InjectService("FilterSecurityInterceptor") HttpServletRequestFilter filterSecurityInterceptor,
            @InjectService("SpringSecurityExceptionFilter") SpringSecurityExceptionTranslationFilter springSecurityExceptionFilter)
    {
        // Provides the SecurityContext through the SecurityContextHolder
        configuration.add("springSecurityHttpSessionContextIntegrationFilter",
                httpSessionContextIntegrationFilter, "before:springSecurity*");

        // Processes an authentication form
        configuration.add("springSecurityAuthenticationProcessingFilter",
                authenticationProcessingFilter);

        // Authentication by basic auth token
        configuration.add("springSecurityBasicProcessingFilter", basicProcessingFilter,
                "after:springSecurityAuthenticationProcessingFilter");

        // Authentication by remember-me authentication token
        configuration.add("springSecurityRememberMeProcessingFilter", rememberMeProcessingFilter);

        // Another wrapper for the ServletRequest
        configuration.add("springSecuritySecurityContextHolderAwareRequestFilter",
                securityContextHolderAwareRequestFilter,
                "after:springSecurityRememberMeProcessingFilter");

        // If not yet authenticated, an anonymous <code>Authentication</code>
        // can be populated by this filter
        configuration.add("springSecurityAnonymousProcessingFilter", anonymousProcessingFilter,
                "after:springSecurityRememberMeProcessingFilter",
                "after:springSecurityAuthenticationProcessingFilter");

        // Filter to catch <code>SpringSecurityException</code>s
        configuration.add("springSecurityExceptionFilter", new HttpServletRequestFilterWrapper(
                springSecurityExceptionFilter), "before:springSecurityFilterSecurityInterceptor");

        // Checks if access to the HTTP resource is allowed
        configuration.add("springSecurityFilterSecurityInterceptor", filterSecurityInterceptor,
                "after:springSecurity*");
    }

    /**
     * Checks if access to the HTTP resource is allowed (
     * {@link org.springframework.security.intercept.AbstractSecurityInterceptor#beforeInvocation(Object)}
     * ) and throws {@link org.springframework.security.AccessDeniedException} otherwise
     */
    @Marker(SpringSecurityServices.class)
    public static HttpServletRequestFilter buildFilterSecurityInterceptor(
            @SpringSecurityServices final AccessDecisionManager accessDecisionManager,
            @SpringSecurityServices final AuthenticationManager manager,
            final Collection<RequestInvocationDefinition> contributions) throws Exception
    {
        FilterSecurityInterceptor interceptor = new FilterSecurityInterceptor();
        LinkedHashMap<RequestKey, ConfigAttributeDefinition> requestMap = convertCollectionToLinkedHashMap(contributions);
        DefaultFilterInvocationDefinitionSource source = new DefaultFilterInvocationDefinitionSource(
                new AntUrlPathMatcher(true), requestMap);
        interceptor.setAccessDecisionManager(accessDecisionManager);
        interceptor.setAlwaysReauthenticate(false);
        interceptor.setAuthenticationManager(manager);
        interceptor.setObjectDefinitionSource(source);
        interceptor.setValidateConfigAttributes(true);
        interceptor.afterPropertiesSet();
        return new HttpServletRequestFilterWrapper(interceptor);
    }

    static LinkedHashMap<RequestKey, ConfigAttributeDefinition> convertCollectionToLinkedHashMap(
            Collection<RequestInvocationDefinition> urls)
    {
        LinkedHashMap<RequestKey, ConfigAttributeDefinition> requestMap = new LinkedHashMap<RequestKey, ConfigAttributeDefinition>();
        for (RequestInvocationDefinition url : urls) {

            requestMap.put(url.getRequestKey(), url.getConfigAttributeDefinition());
        }
        return requestMap;
    }

    /**
     * Provides the SecurityContext through the SecurityContextHolder
     */
    @Marker(SpringSecurityServices.class)
    public static HttpServletRequestFilter buildHttpSessionContextIntegrationFilter()
            throws Exception
    {
        HttpSessionContextIntegrationFilter filter = new HttpSessionContextIntegrationFilter();
        filter.setContextClass(SecurityContextImpl.class);
        filter.setAllowSessionCreation(true);
        filter.setForceEagerSessionCreation(false);
        filter.afterPropertiesSet();
        return new HttpServletRequestFilterWrapper(filter);
    }

    /**
     * Checks credentials entered using a form.
     */
    @Marker(SpringSecurityServices.class)
    public static AuthenticationProcessingFilter buildRealAuthenticationProcessingFilter(
            @SpringSecurityServices final AuthenticationManager manager,
            @SpringSecurityServices final RememberMeServices rememberMeServices,
            @Inject @Value("${spring-security.check.url}") final String authUrl,
            @Inject @Value("${spring-security.target.url}") final String targetUrl,
            @Inject @Value("${spring-security.failure.url}") final String failureUrl)
            throws Exception
    {
        AuthenticationProcessingFilter filter = new AuthenticationProcessingFilter();
        filter.setAuthenticationManager(manager);
        filter.setAuthenticationFailureUrl(failureUrl);
        filter.setDefaultTargetUrl(targetUrl);
        filter.setFilterProcessesUrl(authUrl);
        filter.setRememberMeServices(rememberMeServices);
        filter.afterPropertiesSet();
        return filter;
    }

    /**
     * Wrapper for
     * {@link #buildRealAuthenticationProcessingFilter(AuthenticationManager, RememberMeServices, String, String, String)}
     */
    @Marker(SpringSecurityServices.class)
    public static HttpServletRequestFilter buildAuthenticationProcessingFilter(
            final AuthenticationProcessingFilter filter) throws Exception
    {
        return new HttpServletRequestFilterWrapper(filter);
    }

    /**
     * Tries authentication using the basic authentication token
     */
    @Marker(SpringSecurityServices.class)
    public static HttpServletRequestFilter buildBasicProcessingFilter(
            @SpringSecurityServices final AuthenticationManager manager,
            @SpringSecurityServices final RememberMeServices rememberMeServices,
            final AuthenticationEntryPoint entryPoint) throws Exception
    {
        BasicProcessingFilter filter = new BasicProcessingFilter();
        filter.setAuthenticationManager(manager);
        filter.setAuthenticationEntryPoint(entryPoint);
        filter.setRememberMeServices(rememberMeServices);
        filter.afterPropertiesSet();
        return new HttpServletRequestFilterWrapper(filter);
    }

    /**
     * Checks for remember-me token
     */
    @Marker(SpringSecurityServices.class)
    public static HttpServletRequestFilter buildRememberMeProcessingFilter(
            @SpringSecurityServices final RememberMeServices rememberMe,
            @SpringSecurityServices final AuthenticationManager authManager) throws Exception
    {
        RememberMeProcessingFilter filter = new RememberMeProcessingFilter();
        filter.setRememberMeServices(rememberMe);
        filter.setAuthenticationManager(authManager);
        filter.afterPropertiesSet();
        return new HttpServletRequestFilterWrapper(filter);
    }

    @Marker(SpringSecurityServices.class)
    public static HttpServletRequestFilter buildSecurityContextHolderAwareRequestFilter()
    {
        return new HttpServletRequestFilterWrapper(new SecurityContextHolderAwareRequestFilter());
    }

    /**
     * Detects if there is no <code>Authentication</code> object in the
     * <code>SecurityContextHolder</code>, and populates it with one if needed.
     */
    @Marker(SpringSecurityServices.class)
    public static HttpServletRequestFilter buildAnonymousProcessingFilter(
            @Inject @Value("${spring-security.anonymous.attribute}") final String anonymousAttr,
            @Inject @Value("${spring-security.anonymous.key}") final String anonymousKey)
            throws Exception
    {
        AnonymousProcessingFilter filter = new AnonymousProcessingFilter();
        filter.setKey(anonymousKey);
        UserAttributeEditor attrEditor = new UserAttributeEditor();
        attrEditor.setAsText(anonymousAttr);
        UserAttribute attr = (UserAttribute) attrEditor.getValue();
        filter.setUserAttribute(attr);
        filter.afterPropertiesSet();
        return new HttpServletRequestFilterWrapper(filter);
    }

    /**
     * Identifies previously remembered users by a Base-64 encoded cookie.
     */
    @Marker(SpringSecurityServices.class)
    public static RememberMeServices build(final UserDetailsService userDetailsService,
            @Inject @Value("${spring-security.rememberme.key}") final String rememberMeKey,
            @Inject @Value("${spring-security.rememberme.always}") final boolean alwaysRemember,
            @Inject @Value("${spring-security.rememberme.lifetime}") final int tokenLifetime)
    {
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices();
        rememberMe.setUserDetailsService(userDetailsService);
        rememberMe.setKey(rememberMeKey);
        rememberMe.setAlwaysRemember(alwaysRemember);
        rememberMe.setTokenValiditySeconds(tokenLifetime);
        return rememberMe;
    }

    /**
     * Same as {@link #build(UserDetailsService, String)}. Not sure why it has to be a service of
     * its own.
     * {@link org.springframework.security.ui.rememberme.TokenBasedRememberMeServices#afterPropertiesSet()}
     * only does some assertions.
     */
    @Marker(SpringSecurityServices.class)
    public static LogoutHandler buildRememberMeLogoutHandler(
            final UserDetailsService userDetailsService,
            @Inject @Value("${spring-security.rememberme.key}") final String rememberMeKey)
            throws Exception
    {
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices();
        rememberMe.setUserDetailsService(userDetailsService);
        rememberMe.setKey(rememberMeKey);
        rememberMe.afterPropertiesSet();
        return rememberMe;
    }

    /**
     * Configuration for LogoutService created in {@link #bind(ServiceBinder)}
     */
    public static void contributeLogoutService(final OrderedConfiguration<LogoutHandler> cfg,
            @InjectService("RememberMeLogoutHandler") final LogoutHandler rememberMeLogoutHandler)
    {
        // clears the SecurityContextHolder and invalidates the session
        cfg.add("securityContextLogoutHandler", new SecurityContextLogoutHandler());

        // clears the remember-me cookie
        cfg.add("rememberMeLogoutHandler", rememberMeLogoutHandler);
    }

    /**
     * Iterates an <code>Authentication</code> request through a list of
     * <code>AuthenticationProvider</code>s.
     */
    @Marker(SpringSecurityServices.class)
    public static AuthenticationManager buildProviderManager(
            final List<AuthenticationProvider> providers) throws Exception
    {
        ProviderManager manager = new ProviderManager();
        manager.setProviders(providers);
        manager.afterPropertiesSet();
        return manager;
    }

    /**
     * Validates <code>AnonymousAuthenticationToken</code>s
     */
    @Marker(SpringSecurityServices.class)
    public final AuthenticationProvider buildAnonymousAuthenticationProvider(
            @Inject @Value("${spring-security.anonymous.key}") final String anonymousKey)
            throws Exception
    {
        AnonymousAuthenticationProvider provider = new AnonymousAuthenticationProvider();
        provider.setKey(anonymousKey);
        provider.afterPropertiesSet();
        return provider;
    }

    /**
     * Validates <code>RememberMeAuthenticationToken</code>s
     */
    @Marker(SpringSecurityServices.class)
    public final AuthenticationProvider buildRememberMeAuthenticationProvider(
            @Inject @Value("${spring-security.rememberme.key}") final String rememberMeKey)
            throws Exception
    {
        RememberMeAuthenticationProvider provider = new RememberMeAuthenticationProvider();
        provider.setKey(rememberMeKey);
        provider.afterPropertiesSet();
        return provider;
    }

    /**
     * Retrieves user details from an <code>UserDetailsService</code>
     */
    @Marker(SpringSecurityServices.class)
    public final AuthenticationProvider buildDaoAuthenticationProvider(
            final UserDetailsService userDetailsService, final PasswordEncoder passwordEncoder,
            final SaltSourceService saltSource) throws Exception
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setSaltSource(saltSource);
        provider.afterPropertiesSet();
        return provider;
    }

    public final void contributeProviderManager(
            final OrderedConfiguration<AuthenticationProvider> configuration,
            @InjectService("AnonymousAuthenticationProvider") final AuthenticationProvider anonymousAuthenticationProvider,
            @InjectService("RememberMeAuthenticationProvider") final AuthenticationProvider rememberMeAuthenticationProvider)
    {
        configuration.add("anonymousAuthenticationProvider", anonymousAuthenticationProvider);

        configuration.add("rememberMeAuthenticationProvider", rememberMeAuthenticationProvider);
    }

    /**
     * Decides uppon access to an object. It passes the object to a list of <code>RoleVoter</code>s.
     * Based on the AccessDecisionManager's implementation, access is granted or denied.
     */
    @Marker(SpringSecurityServices.class)
    public final AccessDecisionManager buildAccessDecisionManager(
            final List<AccessDecisionVoter> voters) throws Exception
    {
        AffirmativeBased manager = new AffirmativeBased();
        manager.setDecisionVoters(voters);
        manager.afterPropertiesSet();
        return manager;
    }

    /**
     * Used by the AccessDecisionManager
     */
    public final void contributeAccessDecisionManager(
            final OrderedConfiguration<AccessDecisionVoter> configuration)
    {
        configuration.add("RoleVoter", new RoleVoter());
    }

    /**
     * See {@link #contributeComponentClassTransformWorker(OrderedConfiguration, SecurityChecker)}
     */
    @Marker(SpringSecurityServices.class)
    public static SecurityChecker buildSecurityChecker(
            @SpringSecurityServices final AccessDecisionManager accessDecisionManager,
            @SpringSecurityServices final AuthenticationManager authenticationManager)
            throws Exception
    {
        StaticSecurityChecker checker = new StaticSecurityChecker();
        checker.setAccessDecisionManager(accessDecisionManager);
        checker.setAuthenticationManager(authenticationManager);
        checker.afterPropertiesSet();
        return checker;
    }

    /**
     * Web form <code>AuthenticationEntryPoint</code>
     */
    @Marker(SpringSecurityServices.class)
    public static AuthenticationEntryPoint buildAuthenticationEntryPoint(
            @Inject @Value("${spring-security.loginform.url}") final String loginFormUrl,
            @Inject @Value("${spring-security.force.ssl.login}") final String forceHttps)
            throws Exception
    {
        AuthenticationProcessingFilterEntryPoint entryPoint = new AuthenticationProcessingFilterEntryPoint();
        entryPoint.setLoginFormUrl(loginFormUrl);
        entryPoint.afterPropertiesSet();
        boolean forceSSL = Boolean.parseBoolean(forceHttps);
        entryPoint.setForceHttps(forceSSL);
        return entryPoint;
    }

    /**
     * Filter to catch <code>SpringSecurityException</code>s
     */
    public static SpringSecurityExceptionTranslationFilter buildSpringSecurityExceptionFilter(
            final AuthenticationEntryPoint aep,
            @Inject @Value("${spring-security.accessDenied.url}") final String accessDeniedUrl)
            throws Exception
    {
        SpringSecurityExceptionTranslationFilter filter = new SpringSecurityExceptionTranslationFilter();
        filter.setAuthenticationEntryPoint(aep);
        if (!accessDeniedUrl.equals("")) {
            T5AccessDeniedHandler accessDeniedHandler = new T5AccessDeniedHandler();
            accessDeniedHandler.setErrorPage(accessDeniedUrl);
            filter.setAccessDeniedHandler(accessDeniedHandler);
        }
        filter.afterPropertiesSet();
        return filter;
    }

    /**
     * To catch <code>SpringSecurityException</code>s inside a Tapestry request, the
     * SpringSecurityExceptionFilter is also used here (like in
     * {@link #contributeHttpServletRequestHandler()}).
     */
    public static void contributeRequestHandler(
            final OrderedConfiguration<RequestFilter> configuration,
            final RequestGlobals globals,
            @InjectService("SpringSecurityExceptionFilter") final SpringSecurityExceptionTranslationFilter springSecurityExceptionFilter)
    {
        configuration.add("SpringSecurityExceptionFilter", new RequestFilterWrapper(globals,
                springSecurityExceptionFilter), "after:ErrorFilter");
    }

    public static void contributeComponentClassResolver(
            final Configuration<LibraryMapping> configuration)
    {
        configuration.add(new LibraryMapping("security", "ch.astina.carp.springsecurity"));
    }

    public static UserDetailsService buildUserDetailsService(PasswordEncoder encoder,
            SaltSource salt, UserDAO userDao)
    {
        return new CarpetUserDetailsService(encoder, salt, userDao);
    }
}
