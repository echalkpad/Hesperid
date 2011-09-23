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
package ch.astina.hesperid.web.components;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.ioc.annotations.Inject;

import ch.astina.hesperid.web.services.sitemap.Sitemap;
import ch.astina.hesperid.web.services.version.Version;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@Import(stylesheet = {"context:styles/main.css", "context:styles/content.css",
    "context:styles/forms.css"})
public class Layout
{
    @Inject
    private Version version;
    @Inject
    private Sitemap sitemap;
    @InjectComponent
    private Feedback feedback;

    public Version getVersion()
    {
        return version;
    }

    public Sitemap getSitemap()
    {
        return sitemap;
    }

    public Feedback getFeedback()
    {
        return feedback;
    }
}
