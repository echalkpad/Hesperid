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

import org.apache.tapestry5.hibernate.HibernateGridDataSource;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class FilterGridDataSource extends HibernateGridDataSource
{
    List<Criterion> filters = new ArrayList<Criterion>();
    List<String> joins = new ArrayList<String>();
    Map<String, String> aliases = new HashMap<String, String>();
    Order order;

    @SuppressWarnings("rawtypes")
    public FilterGridDataSource(Session session, Class clazz)
    {
        super(session, clazz);
    }

    public void addFilter(Criterion crit)
    {
        filters.add(crit);
    }

    public void addJoin(String name)
    {
        joins.add(name);
    }

    public void addAlias(String path)
    {
        aliases.put(path, path);
    }

    public void addAlias(String path, String alias)
    {
        aliases.put(path, alias);
    }

    public Order getOrder()
    {
        return order;
    }

    public void setOrder(Order order)
    {
        this.order = order;
    }

    @Override
    protected void applyAdditionalConstraints(Criteria criteria)
    {
        for (String join : joins) {
            criteria.setFetchMode(join, FetchMode.JOIN);
        }

        for (Entry<String, String> alias : aliases.entrySet()) {
            criteria.createAlias(alias.getKey(), alias.getValue());
        }

        for (Criterion filter : filters) {
            criteria.add(filter);
        }

        if (order != null) {
            criteria.addOrder(order);
        }
    }
}
