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
package ch.astina.hesperid.web.pages.admin.agentbundle;

import java.io.File;
import java.util.Date;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.springframework.security.annotation.Secured;

import ch.astina.hesperid.dao.AgentBundleDAO;
import ch.astina.hesperid.installer.web.services.InstallationManager;
import ch.astina.hesperid.model.base.AgentBundle;
import ch.astina.hesperid.web.services.systemenvironment.SystemEnvironment;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 118 $, $Date: 2011-09-21 16:33:28 +0200 (Mi, 21 Sep 2011) $
 */
@Secured({"ROLE_ADMIN"})
public class ManageAgentBundle
{
    @Property
    private AgentBundle agentBundle;

    @SuppressWarnings("unused")
    @Property
    @Persist(PersistenceConstants.FLASH)
    private boolean success;

    @Inject
    private AgentBundleDAO agentBundleDAO;

    @Inject
    private SystemEnvironment systemEnvironment;

    @Property
    private UploadedFile software;

    public void onActivate(Long agentBundleId)
    {
        agentBundle = agentBundleDAO.getAgentBundleForId(agentBundleId);
    }

    public void onActivate()
    {
        if (agentBundle == null) {
            agentBundle = new AgentBundle();
        }
    }

    public Long onPassivate()
    {
        if (agentBundle != null) {
            return agentBundle.getId();
        }

        return null;
    }

    @CommitAfter
    public void onSuccessFromAgentBundleForm()
    {  
        if (software != null) {
            
            File file = new File(systemEnvironment.getApplicationHomeDirectoryPath() + 
                    InstallationManager.FILE_SEPARATOR + "agentbundle" +
                    InstallationManager.FILE_SEPARATOR);
            
            if (!file.exists()) {
                file.mkdirs();
            }
            
            File newSoftware = new File(file,software.getFileName());
            
            software.write(newSoftware);

            agentBundle.setFilename(software.getFileName());
        }
        
        agentBundle.setPublished(new Date());

        agentBundleDAO.saveOrUpdateAgentBundle(agentBundle);

        success = true;
    }
}
