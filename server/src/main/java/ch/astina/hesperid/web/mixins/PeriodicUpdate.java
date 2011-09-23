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
package ch.astina.hesperid.web.mixins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * Copied from http://blog.bolkey.com/2010/05/creating-a-news-feed-in-tapestry-5/
 * 
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@Import(library = "periodicupdate.js")
public class PeriodicUpdate
{
    /**
     * The name of the event to call to update the zone.
     */
    @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
    private String event;
    /**
     * The context for the triggered event. The clientId of the containing zone is always added as
     * the final context item.
     */
    @Parameter("defaultContext")
    private Object[] context;
    /**
     * How long, in seconds, to wait between the end of one request and the beginning of the next.
     */
    @Parameter(defaultPrefix = BindingConstants.LITERAL, value = "10")
    private int period;
    @InjectContainer
    private Zone zone;
    @Inject
    private ComponentResources resources;
    @Inject
    private JavaScriptSupport javaScriptSupport;

    public Object[] getDefaultContext()
    {
        return new Object[0];
    }

    void afterRender()
    {
        final String id = zone.getClientId();

        final List<Object> context = new ArrayList<Object>(Arrays.asList(this.context));
        context.add(zone.getClientId());

        final Link link = resources.createEventLink(event,
                context.toArray(new Object[context.size()]));

        final JSONObject options = new JSONObject();

        options.put("id", id);
        options.put("uri", link.toAbsoluteURI());
        options.put("period", period);

        javaScriptSupport.addInitializerCall("periodicupdater", options);
    }
}
