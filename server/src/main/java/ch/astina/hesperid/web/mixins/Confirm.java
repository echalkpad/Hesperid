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

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * Copied from http://wiki.apache.org/tapestry/Tapestry5AndJavaScriptExplained
 * 
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@Import(library = {"confirm.js"})
public class Confirm
{
    @Parameter(value = "Are you sure?", defaultPrefix = BindingConstants.LITERAL)
    private String message;
    @Inject
    private JavaScriptSupport javaScriptSupport;
    @InjectContainer
    private ClientElement element;

    @AfterRender
    void afterRender()
    {
        javaScriptSupport.addScript(String.format("new Confirm('%s', '%s');",
                element.getClientId(), message));
    }
}
