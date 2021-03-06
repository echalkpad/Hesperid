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
package ch.astina.hesperid.model.internal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 103 $, $Date: 2011-09-21 15:51:59 +0200 (Mi, 21 Sep 2011) $
 */
@Entity
public class ReportType
{
    private Long id;
    private String name;
    private String jasperXmlCode;
    private String hqlQuery;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Lob
    public String getJasperXmlCode() 
    {
        return jasperXmlCode;
    }

    public void setJasperXmlCode(String jasperXmlCode) 
    {
        this.jasperXmlCode = jasperXmlCode;
    }

    @Lob
    public String getHqlQuery() 
    {
        return hqlQuery;
    }

    public void setHqlQuery(String hqlQuery) 
    {
        this.hqlQuery = hqlQuery;
    }
}
