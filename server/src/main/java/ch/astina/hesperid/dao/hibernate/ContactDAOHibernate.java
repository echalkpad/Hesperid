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

import ch.astina.hesperid.dao.ContactDAO;
import ch.astina.hesperid.model.base.Contact;
import org.hibernate.Session;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class ContactDAOHibernate implements ContactDAO
{
    private Session session;

    public ContactDAOHibernate(Session session)
    {
        this.session = session;
    }

    @Override
    public Contact getContactForId(Long contactId)
    {
        return (Contact) session.get(Contact.class, contactId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Contact> getAllContacts()
    {
        return session.createCriteria(Contact.class).list();
    }

    @Override
    public void saveOrUpdateContact(Contact contact)
    {
        session.saveOrUpdate(contact);
    }

    @Override
    public void deleteContact(Contact contact)
    {
        session.createSQLQuery("DELETE FROM asset_contact WHERE contact = :contactId").setLong("contactId", contact.getId()).executeUpdate();
        
        session.delete(contact);
    }
}
