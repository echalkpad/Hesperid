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
package ch.astina.hesperid.web.services.impl;

import ch.astina.hesperid.dao.SystemHealthDAO;
import ch.astina.hesperid.dao.hibernate.SystemHealthDAOHibernate;
import ch.astina.hesperid.model.internal.SystemHealth;
import ch.astina.hesperid.util.StacktraceUtil;
import ch.astina.hesperid.web.services.SystemHealthService;
import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class SystemHealthServiceImpl implements SystemHealthService
{
    private HibernateSessionSource hibernateSessionSource;

    private static final Logger logger = LoggerFactory.getLogger(SystemHealthServiceImpl.class);
    
    public SystemHealthServiceImpl(HibernateSessionSource hibernateSessionSource)
    {
        this.hibernateSessionSource = hibernateSessionSource;
    }

    public void log(String title, String message, Throwable throwable)
    {
    	logger.info("System health log entry: " + title + " (" + message + ")");
    	
        Session session = null;

        try {
        	
            session = hibernateSessionSource.create();
            session.beginTransaction();
            
            SystemHealthDAO systemHealthDAO = new SystemHealthDAOHibernate(session);

            SystemHealth systemHealth = new SystemHealth();
            systemHealth.setTitle(title);
            systemHealth.setLog(message + "\n\n\n\n" + StacktraceUtil.getStackTrace(throwable));
            systemHealth.setLogDate(new Date());
            systemHealthDAO.saveOrUpdateSystemHealth(systemHealth);
            session.getTransaction().commit();

        } catch (Exception e) {
            //here we can not help anymore
            logger.error("Error while logging system health event", e);
        } finally {
            session.close();
        }

    }

}
