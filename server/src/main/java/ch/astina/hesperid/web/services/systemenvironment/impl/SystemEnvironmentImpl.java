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
package ch.astina.hesperid.web.services.systemenvironment.impl;

import ch.astina.hesperid.installer.web.services.InstallationManager;
import ch.astina.hesperid.web.services.systemenvironment.SystemEnvironment;

import java.io.File;
import javax.servlet.ServletContext;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2010-04-29 14:14:20 +0200 (Thu, 29 Apr
 *          2010) $
 */
public class SystemEnvironmentImpl extends InstallationManager implements SystemEnvironment
{
    @Override
    public Configuration getHibernateConfiguration()
    {
        return getMainConfiguration();
    }

    @Override
    public String getEnvironmentVariableValue(String environmentVariableName)
    {
        try {
            // check if property is set
            return System.getProperty(environmentVariableName);
        } catch (Exception e) {
            // property is not set
        }

        try {
            // check if environment variable is set
            return System.getenv(environmentVariableName);
        } catch (Exception e) {
            // environment variable is not set
        }

        return null;
    }

    @Override
    public String getApplicationLogfilePath() 
    {
        String logfilePath = getApplicationHomeDirectoryPath();
        
        if (!logfilePath.endsWith(FILE_SEPARATOR)) {
            logfilePath += FILE_SEPARATOR;
        }
        
        logfilePath += "logs" + FILE_SEPARATOR;
        
        File logfile = new File(logfilePath);
        
        if (!logfile.isDirectory()) {
            logfile.mkdirs();
        }
        
        return logfilePath + "hesperid.out";
    }
}
