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
package ch.astina.hesperid.web.pages.system;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.springframework.security.annotation.Secured;

import ch.astina.hesperid.dao.SystemDAO;
import ch.astina.hesperid.model.base.System;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 118 $, $Date: 2011-09-21 16:33:28 +0200 (Mi, 21 Sep 2011) $
 */
public class ManageSystem
{
    @Property
    private System system;

    @Inject
    private SystemDAO systemDAO;
    
    @Inject
    private PageRenderLinkSource linkSource;

    @Secured({"ROLE_ADMIN"})
    public void onActivate(Long id)
    {
        system = systemDAO.getById(id);
    }

    @Secured({"ROLE_ADMIN"})
    public void onActivate()
    {
        if (system == null) {
            system = new System();
        }
    }

    public Long onPassivate()
    {
        if (system != null) {
            return system.getId();
        }

        return null;
    }

    @CommitAfter
    public Link onSuccess()
    {
    	systemDAO.save(system);
    	return linkSource.createPageRenderLinkWithContext(ViewSystem.class, system);
    }
}
