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
package ch.astina.hesperid.configuration;

import org.apache.tapestry5.hibernate.HibernateConfigurer;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class HibernateSimpleConfigurer implements HibernateConfigurer
{
    private Logger logger = LoggerFactory.getLogger(HibernateSimpleConfigurer.class);

    @Override
    public void configure(Configuration configuration)
    {
        logger.info("Test Connection URL " + SetupTest.url);
        logger.info("Test Connection User " + SetupTest.username);
        logger.info("Test Connection Password " + SetupTest.password);

        configuration.setProperty("hibernate.connection.url", SetupTest.url);
        configuration.setProperty("hibernate.connection.username", SetupTest.username);
        configuration.setProperty("hibernate.connection.password", SetupTest.password);

        // Change naming strategy to ImprovedNamingStrategy, converting
        // camel case names to underscore names.
        configuration.setNamingStrategy(ImprovedNamingStrategy.INSTANCE);
    }
}
