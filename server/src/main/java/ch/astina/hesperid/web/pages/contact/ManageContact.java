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

import ch.astina.hesperid.dao.ContactDAO;
import ch.astina.hesperid.dao.LocationDAO;
import ch.astina.hesperid.dao.MesRoleDAO;
import ch.astina.hesperid.model.base.BusinessRole;
import ch.astina.hesperid.model.base.Contact;
import ch.astina.hesperid.model.base.Location;
import ch.astina.hesperid.util.GenericSelectModel;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.springframework.security.access.annotation.Secured;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class ManageContact
{
    @Property
    private Contact contact;
    @SuppressWarnings("unused")
    @Property
    private GenericSelectModel<Location> locations;
    @SuppressWarnings("unused")
    @Property
    private GenericSelectModel<BusinessRole> mesRoles;
    @SuppressWarnings("unused")
    @Property
    @Persist(PersistenceConstants.FLASH)
    private boolean success;
    @Inject
    private ContactDAO contactDAO;
    @Inject
    private MesRoleDAO mesRoleDAO;
    @Inject
    private LocationDAO manufacturingLocationDAO;
    @Inject
    private PropertyAccess propertyAccess;

    @Secured({"ROLE_ADMIN"})
    public void onActivate(Long contactId)
    {
        contact = contactDAO.getContactForId(contactId);
    }

    public void onActivate()
    {
        if (contact == null) {
            contact = new Contact();
        }

        locations = new GenericSelectModel<Location>(manufacturingLocationDAO.getAllLocations(), Location.class, "name", "id", propertyAccess);
        mesRoles = new GenericSelectModel<BusinessRole>(mesRoleDAO.getAllMesRoles(), BusinessRole.class, "name", "id", propertyAccess);
    }

    public Long onPassivate()
    {
        if (contact != null) {
            return contact.getId();
        }

        return null;
    }

    @CommitAfter
    public void onSuccessFromContactForm()
    {
        contactDAO.saveOrUpdateContact(contact);

        success = true;
    }
}
