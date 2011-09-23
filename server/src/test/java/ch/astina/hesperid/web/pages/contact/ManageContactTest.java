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
package ch.astina.hesperid.web.pages.contact;

import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.test.PageTester;
import org.hibernate.Session;
import org.testng.Assert;
import org.testng.annotations.Test;

import ch.astina.hesperid.dao.ContactDAO;
import ch.astina.hesperid.dao.hibernate.ContactDAOHibernate;
import ch.astina.hesperid.model.base.Contact;
import ch.astina.hesperid.web.PageTesterFactory;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 118 $, $Date: 2011-09-21 16:33:28 +0200 (Mi, 21 Sep 2011) $
 */
public class ManageContactTest
{
    private Long contactId = null;

    @Test
    public void test1() throws Exception
    {
        PageTester pageTester = PageTesterFactory.getInstance();
        
        HibernateSessionSource hibernateSessionSource = pageTester.getService(HibernateSessionSource.class);
        Session session = hibernateSessionSource.create();
        ContactDAO contactDao = new ContactDAOHibernate(session);
        session.beginTransaction();

        Contact contact = new Contact();
        contact.setFirstName("testcontactfirstname");
        contact.setLastName("testcontactlastname");

        contactDao.saveOrUpdateContact(contact);
        session.getTransaction().commit();

        contactId = contact.getId();

        Contact newContact = contactDao.getContactForId(contactId);

        Assert.assertNotNull(newContact);
    }


    @Test(dependsOnMethods = {"test1"})
    public void test2() throws Exception
    {
        PageTester pageTester = PageTesterFactory.getInstance();

        HibernateSessionSource hibernateSessionSource = pageTester.getService(HibernateSessionSource.class);
        Session session = hibernateSessionSource.create();
        ContactDAO contactDao = new ContactDAOHibernate(session);
        session.beginTransaction();

        Contact newContact = contactDao.getContactForId(contactId);
        contactDao.deleteContact(newContact);
        session.getTransaction().commit();

        newContact = contactDao.getContactForId(contactId);
        
        Assert.assertNull(newContact);
    }
}
