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

import ch.astina.hesperid.dao.LocationDAO;
import ch.astina.hesperid.model.base.Location;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class LocationDAOHibernate implements LocationDAO
{
    private Session session;

    public LocationDAOHibernate(Session session)
    {
        this.session = session;
    }

    @Override
    public Location getLocationById(Long id)
    {
        return (Location) session.get(Location.class,
                id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Location> getAllLocations()
    {
        return session.createCriteria(Location.class).list();
    }

    @Override
    public void save(Location location)
    {
        session.saveOrUpdate(location);
    }

    @Override
    public void delete(Location location)
    {
        String sql = "UPDATE contact SET location = NULL WHERE location = ?";
        Query query = session.createSQLQuery(sql);
        query.setLong(0, location.getId());
        query.executeUpdate();

        sql = "UPDATE asset SET location = NULL WHERE location = ?";
        query = session.createSQLQuery(sql);
        query.setLong(0, location.getId());
        query.executeUpdate();

        session.delete(location);
    }
}
