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
package ch.astina.hesperid.dao.hibernate;

import ch.astina.hesperid.dao.MailServerDAO;
import ch.astina.hesperid.model.internal.MailServer;
import org.hibernate.Session;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2010-11-13 13:13:10 +0100 (Sam, 13. Nov
 *          2010) $
 */
public class MailServerDAOHibernate implements MailServerDAO
{
    private Session session;

    public MailServerDAOHibernate(Session session)
    {
        this.session = session;
    }

    public void saveOrUpdateMailServer(MailServer mailServer)
    {
        session.saveOrUpdate(mailServer);
    }

    public MailServer getMailServerForId(Long mailServerId)
    {
        return (MailServer) session.get(MailServer.class, mailServerId);
    }
}
