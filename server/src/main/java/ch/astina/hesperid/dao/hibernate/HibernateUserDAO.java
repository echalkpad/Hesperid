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

import ch.astina.hesperid.dao.UserDAO;
import ch.astina.hesperid.model.user.User;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class HibernateUserDAO implements UserDAO
{
    private Session session;

    public HibernateUserDAO(Session session)
    {
        this.session = session;
    }

    public User getUser(Long id)
    {
        return (User) session.load(User.class, id);
    }

    public User getUserByName(String name)
    {
        return (User) session.createCriteria(User.class).add(
                Restrictions.eq("username", name)).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<User> getAllUsers()
    {
        return session.createCriteria(User.class).list();
    }

    public void save(User user)
    {
        session.saveOrUpdate(user);
    }

    public void delete(User user)
    {
        session.delete(user);
    }
}
