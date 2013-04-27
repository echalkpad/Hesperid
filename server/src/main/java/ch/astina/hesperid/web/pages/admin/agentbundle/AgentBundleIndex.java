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

import ch.astina.hesperid.dao.AgentBundleDAO;
import ch.astina.hesperid.installer.web.services.InstallationManager;
import ch.astina.hesperid.model.base.AgentBundle;
import ch.astina.hesperid.web.services.systemenvironment.SystemEnvironment;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;
import org.springframework.security.access.annotation.Secured;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 118 $, $Date: 2011-09-21 16:33:28 +0200 (Mi, 21 Sep 2011) $
 */
@Secured({"ROLE_ADMIN"})
public class AgentBundleIndex
{
    @Inject
    private AgentBundleDAO agentBundleDAO;

    @SuppressWarnings("unused")
	@Property
    private AgentBundle agentBundle;

    @Inject
    private SystemEnvironment systemEnvironment;

    public List<AgentBundle> getAllAgentBundles()
    {
        return agentBundleDAO.getAllAgentBundles();
    }

    Object onActionFromDownload(Long agentBundleId)
    {
        final AgentBundle a = agentBundleDAO.getAgentBundleForId(agentBundleId);

        String dir = systemEnvironment.getApplicationHomeDirectoryPath() +
                InstallationManager.FILE_SEPARATOR + "agentbundle" +
                InstallationManager.FILE_SEPARATOR;

        final File file = new File(dir, a.getFilename());

        return new StreamResponse()
        {
            @Override
            public String getContentType()
            {
                return "application/octet-stream";
            }

            @Override
            public InputStream getStream() throws IOException
            {
                return new FileInputStream(file);
            }

            @Override
            public void prepareResponse(Response response)
            {
                response.setContentLength((int) file.length());
                response.setHeader("content-disposition", "attachment; filename=" + encodeStr(a.getFilename()));
            }
        };
    }

    private String encodeStr(String str)
    {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
