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
package ch.astina.hesperid.web.pages.admin;

import ch.astina.hesperid.dao.SystemHealthDAO;
import ch.astina.hesperid.global.GlobalConstants;
import ch.astina.hesperid.model.internal.SystemHealth;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.annotation.Secured;

import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
@Secured({"ROLE_ADMIN"})
public class SystemHealthLog
{
    @Inject
    private SystemHealthDAO systemHealthDAO;

    @Property
    private SystemHealth systemHealth;
	
	@Property
	@Persist
	private String searchString;


    public List<SystemHealth> getSystemHealthEntries()
    {
	    List<SystemHealth> systemHealthEntries = systemHealthDAO.findByLog(searchString);

	    if(systemHealthEntries != null) {
		    return systemHealthEntries;
	    } else {
		    return new ArrayList<SystemHealth>();
	    }
    }

    @CommitAfter
    public void onActionFromDelete()
    {
        systemHealthDAO.deleteAllSystemHeathEntries();
    }

    public String getLogDate()
    {
    	return GlobalConstants.DATETIME_FORMAT.format(systemHealth.getLogDate());
    }
}
