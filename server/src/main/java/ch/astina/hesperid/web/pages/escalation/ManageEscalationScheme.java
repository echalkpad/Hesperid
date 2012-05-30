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
package ch.astina.hesperid.web.pages.escalation;

import ch.astina.hesperid.dao.EscalationDAO;
import ch.astina.hesperid.model.base.EscalationScheme;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.springframework.security.access.annotation.Secured;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class ManageEscalationScheme 
{
    @Inject
    private EscalationDAO escalationDAO;
    
    @Property 
    private EscalationScheme escalationScheme;
    
    @Inject
    private PageRenderLinkSource pageRenderLinkSource;
    
    @Secured({"ROLE_ADMIN"})
    public void onActivate(Long escalationSchemeId)
    {
        escalationScheme = escalationDAO.getEscalationSchemeById(escalationSchemeId);
    }

    @Secured({"ROLE_ADMIN"})
    public void onActivate()
    {
        if (escalationScheme == null) {
            escalationScheme = new EscalationScheme();
        }
    }  
    
    public Long onPassivate()
    {
        if (escalationScheme != null) {
            return escalationScheme.getId();
        }
        return null;
    }
    
    @CommitAfter
    public Link onSuccess()
    {
        escalationDAO.save(escalationScheme);

        return pageRenderLinkSource.createPageRenderLinkWithContext(EscalationView.class, escalationScheme);
    }
}
