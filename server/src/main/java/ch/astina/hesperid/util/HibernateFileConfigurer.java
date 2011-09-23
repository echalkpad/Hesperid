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
package ch.astina.hesperid.util;

import ch.astina.hesperid.web.services.systemenvironment.SystemEnvironment;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.apache.tapestry5.hibernate.HibernateConfigurer;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 107 $, $Date: 2011-09-21 16:07:03 +0200 (Mi, 21 Sep 2011) $
 */
public class HibernateFileConfigurer implements HibernateConfigurer 
{
    private String url;
    private String username;
    private String password;
    private SystemEnvironment systemEnvironment;
    

    public HibernateFileConfigurer(SystemEnvironment systemEnvironment)
    {
        this.systemEnvironment = systemEnvironment;
    }

    @Override
    public void configure(Configuration configuration)
    {
        readConfiguration();
        configuration.setProperty("hibernate.connection.url", url);
        configuration.setProperty("hibernate.connection.username", username);
        configuration.setProperty("hibernate.connection.password", password);
        configuration.setProperty("hibernate.connection.autocommit", "false");

        // Change naming strategy to ImprovedNamingStrategy, converting
        // camel case names to underscore names.
        configuration.setNamingStrategy(ImprovedNamingStrategy.INSTANCE);
    }

    private void readConfiguration()
    {
    	org.apache.commons.configuration.Configuration config = systemEnvironment.getHibernateConfiguration();
        this.url = config.getString("database.databaseURL");
        this.username = config.getString("database.username");
        this.password = config.getString("database.password");
    }
}
