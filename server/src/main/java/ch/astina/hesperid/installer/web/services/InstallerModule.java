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
package ch.astina.hesperid.installer.web.services;

import com.spreadthesource.tapestry.installer.InstallerConstants;
import com.spreadthesource.tapestry.installer.services.ConfigurationTask;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class InstallerModule 
{
    public static void bind(ServiceBinder binder)
    {
        binder.bind(IBasicConfigTask.class, BasicConfigTask.class);
        binder.bind(InstallationManager.class);
    }
    
    public void contributeApplicationDefaults(MappedConfiguration<String, String> configuration)
    {
        configuration.add(InstallerConstants.INSTALLER_VERSION, "1.0");
    }
    
    public static void contributeConfigurationManager(OrderedConfiguration<ConfigurationTask> configuration, @Inject IBasicConfigTask bct)
    {
        configuration.add("BasicConfig", bct);
    }
}
