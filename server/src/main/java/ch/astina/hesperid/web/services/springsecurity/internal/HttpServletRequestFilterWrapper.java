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
package ch.astina.hesperid.web.services.springsecurity.internal;

import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.HttpServletRequestHandler;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class HttpServletRequestFilterWrapper implements HttpServletRequestFilter
{
    private Filter filter;

    public HttpServletRequestFilterWrapper(final Filter filter)
    {
        this.filter = filter;
    }

    public final boolean service(final HttpServletRequest request,
            final HttpServletResponse response,
            final HttpServletRequestHandler handler) throws IOException
    {
        // TODO: Thread safety!
        // Assume request handled if filter chain is NOT executed
        final boolean[] res = new boolean[]{true};
        try {
            filter.doFilter(request, response, new FilterChain() {

                public void doFilter(final ServletRequest request,
                        final ServletResponse response) throws IOException,
                        ServletException {
                    res[0] = handler.service((HttpServletRequest) request,
                            (HttpServletResponse) response);
                }
            });
        } catch (ServletException e) {
            IOException ex = new IOException(e.getMessage());
            ex.initCause(e);
            throw ex;
        } catch (Throwable e) {
        }
        return res[0];
    }
}
