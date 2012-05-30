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
package ch.astina.hesperid.installer.web.services;

import com.spreadthesource.tapestry.installer.services.InstallerModule;
import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.ServletContextSymbolProvider;
import org.apache.tapestry5.internal.TapestryAppInitializer;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.ioc.internal.services.MapSymbolProvider;
import org.apache.tapestry5.ioc.internal.util.OneShotLock;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.services.HttpServletRequestHandler;
import org.apache.tapestry5.services.ServletApplicationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class TapestryDelayedFilter implements Filter
{
    private static final String TAPESTRY_INSTALLER_PACKAGE = "tapestry.installer-package";

    private final Logger logger = LoggerFactory.getLogger(TapestryDelayedFilter.class);

    private FilterConfig config;

    private Registry registry;

    private boolean installed;

    private HttpServletRequestHandler handler;

    private OneShotLock lock = new OneShotLock();

    /**
     * Key under which that Tapestry IoC {@link org.apache.tapestry5.ioc.Registry} is stored in the
     * ServletContext. This allows other code, beyond Tapestry, to obtain the Registry and, from it,
     * any Tapestry services. Such code should be careful about invoking
     * {@link org.apache.tapestry5.ioc.Registry#cleanupThread()} appropriately.
     */
    public static final String REGISTRY_CONTEXT_NAME = "org.apache.tapestry5.application-registry";

    private Filter tapestryFilter;

    /**
     * Placeholder for Tapestry filter so we do not have to copy/paste original code. Only
     * installation application has been implemented from scratch.
     */
    static class TapestryFilterPlaceHolder extends TapestryFilter
    {

        private final TapestryDelayedFilter delegate;

        public TapestryFilterPlaceHolder(TapestryDelayedFilter filter)
        {
            super();
            this.delegate = filter;
        }

        @Override
        protected void destroy(Registry registry)
        {
            delegate.destroy(registry);
        }

        @Override
        protected void init(Registry registry) throws ServletException
        {
            delegate.init(registry);
        }

        @Override
        protected ModuleDef[] provideExtraModuleDefs(ServletContext context)
        {
            return delegate.provideExtraModuleDefs(context);
        }

    }

    /**
     * Initializes the filter using the {@link TapestryAppInitializer}. The application name is the
     * capitalization of the filter name (as specified in web.xml).
     */
    public final void init(FilterConfig filterConfig) throws ServletException
    {
        // Init installation application
        config = filterConfig;

        ServletContext context = config.getServletContext();

        String filterName = config.getFilterName();

        SymbolProvider scprovider = new ServletContextSymbolProvider(context);

        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put(InternalConstants.DISABLE_DEFAULT_MODULES_PARAM, "true");
        configuration.put(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, scprovider
                .valueForSymbol(TAPESTRY_INSTALLER_PACKAGE));
        SymbolProvider provider = new MapSymbolProvider(configuration);

        String executionMode = System.getProperty("tapestry.execution-mode", "production");

        TapestryAppInitializer appInitializer = new TapestryAppInitializer(logger, provider,
                filterName, "servlet", executionMode);

        appInitializer.addModules(InstallerModule.class);

        String appPackage = provider.valueForSymbol(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM);

        String className = appPackage + ".services.InstallerModule";

        try
        {
            Class<?> moduleClass = Thread.currentThread().getContextClassLoader().loadClass(
                    className);

            appInitializer.addModules(moduleClass);
        }
        catch (ClassNotFoundException ex)
        {
            // That's OK, not all applications will have a module class, even though any
            // non-trivial application will.
        }

        registry = appInitializer.createRegistry();

        context.setAttribute(REGISTRY_CONTEXT_NAME, registry);

        ServletApplicationInitializer ai = registry.getService(
                "ServletApplicationInitializer",
                ServletApplicationInitializer.class);

        ai.initializeApplication(filterConfig.getServletContext());

        registry.performRegistryStartup();

        handler = registry.getService("HttpServletRequestHandler", HttpServletRequestHandler.class);

        init(registry);

        // 3 possibles cases
        // - application is already installed, then we start it directly
        // - application is not installed, but silent install is true, then we run all
        // configurations tasks and start the application
        // - application is not installed and we want normal install process, then we simply let
        // user go to thefirst configuration task start page

        // if already installed, automatically start the 'real' application
        InstallationManager installationManager = registry.getService(InstallationManager.class);
        
        if (installationManager.hasWorkingDatabaseConnection())
        {
            this.installed = true;
            this.start();            
        }
        else
        {
            appInitializer.announceStartup();
        }
    }

    protected final FilterConfig getFilterConfig()
    {
        return config;
    }

    /**
     * Invoked from {@link #init(FilterConfig)} after the Registry has been created, to allow any
     * additional initialization to occur. This implementation does nothing, and my be overriden in
     * subclasses.
     * 
     * @param registry
     *            from which services may be extracted
     * @throws ServletException
     */
    protected void init(Registry registry) throws ServletException
    {

    }

    /**
     * Overridden in subclasses to provide additional module definitions beyond those normally
     * located. This implementation returns an empty array.
     */
    protected ModuleDef[] provideExtraModuleDefs(ServletContext context)
    {
        return new ModuleDef[0];
    }

    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {

        if (!installed)
        {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if (httpRequest.getServletPath().equals(
                    com.spreadthesource.tapestry.installer.InternalConstants.RESTART_URI))
            {
                this.start();
                // Redirect to root path after restart
                ((HttpServletResponse) response).sendRedirect(httpRequest.getContextPath());
            }
            else
            {
                try
                {
                    handler.service(httpRequest, (HttpServletResponse) response);
                    return;
                }
                finally
                {
                    registry.cleanupThread();
                }
            }
            return;
        }

        chain.doFilter(request, response);

    }

    /**
     * Shuts down and discards the registry. Invokes
     * {@link #destroy(org.apache.tapestry5.ioc.Registry)} to allow subclasses to peform any
     * shutdown logic, then shuts down the registry, and removes it from the ServletContext.
     */
    public final void destroy()
    {
        destroy(registry);

        if (tapestryFilter != null)
        {
            tapestryFilter.destroy();
        }

        config.getServletContext().removeAttribute(REGISTRY_CONTEXT_NAME);

        registry = null;
        config = null;
        handler = null;
    }

    /**
     * Switch to the real context.
     */
    protected synchronized void start() throws ServletException
    {
        // Run the restart only once
        lock.lock();

        // Shutdown current registry.
        if (registry != null)
        {
            registry.shutdown();
        }

        tapestryFilter = new TapestryFilterPlaceHolder(this);
        tapestryFilter.init(config);

        this.installed = true;
    }

    /**
     * Invoked from {@link #destroy()} to allow subclasses to add additional shutdown logic to the
     * filter. The Registry will be shutdown after this call. This implementation does nothing, and
     * may be overridden in subclasses.
     * 
     * @param registry
     */
    protected void destroy(Registry registry)
    {

    }
}
