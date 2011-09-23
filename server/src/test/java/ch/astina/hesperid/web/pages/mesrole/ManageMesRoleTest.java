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
package ch.astina.hesperid.web.pages.mesrole;

import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.test.PageTester;
import org.hibernate.Session;
import org.testng.Assert;
import org.testng.annotations.Test;

import ch.astina.hesperid.dao.MesRoleDAO;
import ch.astina.hesperid.dao.hibernate.MesRoleDAOHibernate;
import ch.astina.hesperid.model.base.MesRole;
import ch.astina.hesperid.web.PageTesterFactory;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 118 $, $Date: 2011-09-21 16:33:28 +0200 (Mi, 21 Sep 2011) $
 */
public class ManageMesRoleTest
{
    private Long mesRoleId = null;

    @Test
    public void test1() throws Exception
    {
        PageTester pageTester = PageTesterFactory.getInstance();

        HibernateSessionSource hibernateSessionSource = pageTester.getService(HibernateSessionSource.class);
        Session session = hibernateSessionSource.create();
        MesRoleDAO mesRoleDAO = new MesRoleDAOHibernate(session);
        session.beginTransaction();

        MesRole mesRole = new MesRole();
        mesRole.setName("testmesrolename");

        mesRoleDAO.saveOrUpdateMesRole(mesRole);
        session.getTransaction().commit();

        mesRoleId = mesRole.getId();

        MesRole newMesRole = mesRoleDAO.getMesRoleForId(mesRoleId);

        Assert.assertNotNull(newMesRole);
    }


    @Test(dependsOnMethods = {"test1"})
    public void test2() throws Exception
    {
        PageTester pageTester = PageTesterFactory.getInstance();

        HibernateSessionSource hibernateSessionSource = pageTester.getService(HibernateSessionSource.class);
        Session session = hibernateSessionSource.create();
        MesRoleDAO mesRoleDAO = new MesRoleDAOHibernate(session);
        session.beginTransaction();

        MesRole newMesRole = mesRoleDAO.getMesRoleForId(mesRoleId);
        mesRoleDAO.deleteMesRole(newMesRole);
        session.getTransaction().commit();

        newMesRole = mesRoleDAO.getMesRoleForId(mesRoleId);

        Assert.assertNull(newMesRole);
    }
}
