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
package ch.astina.hesperid.web.pages.microclient;

import ch.astina.hesperid.installer.web.services.InstallationManager;
import ch.astina.hesperid.web.services.systemenvironment.SystemEnvironment;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author $Author: kstarosta $
 * @version $Revision: 107 $, $Date: 2011-09-21 16:07:03 +0200 (Mi, 21 Sep 2011) $
 */
public class LatestAgentVersionDownload
{
    @Inject
    private SystemEnvironment systemEnvironment;

    public StreamResponse onActivate(final String filename)
    {
        StreamResponse streamResponse = new StreamResponse() {

            @Override
            public String getContentType() {
                return "application/x-java-archive";
            }

            @Override
            public InputStream getStream() throws IOException {
                String agentbundleFilePath = systemEnvironment.getApplicationHomeDirectoryPath() + 
                        InstallationManager.FILE_SEPARATOR + "agentbundle" + 
                        InstallationManager.FILE_SEPARATOR + filename;
                
                File agentbundleFile = new File(agentbundleFilePath);
                
                if (agentbundleFile.exists() && agentbundleFile.canRead()) {
                    return new FileInputStream(agentbundleFile);
                }
                
                InputStream is =  new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("/" + filename));
                
                byte[] byBuf = new byte[is.available()];
                is.read(byBuf);
                
                return new ByteArrayInputStream(byBuf);
            }

            @Override
            public void prepareResponse(Response response) {
                
            }
        };

        return streamResponse;
    }
}
