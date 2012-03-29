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
package ch.astina.hesperid.model.base;

import javax.persistence.*;
import java.util.Date;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 121 $, $Date: 2011-09-21 16:49:11 +0200 (Mi, 21 Sep 2011) $
 */
@Entity
public class ObserverParameter 
{
    private Long id;
    private Observer observer;
    private String value;
    private String error;
    private Date updated;

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

    @ManyToOne
    public Observer getObserver()
    {
        return observer;
    }

    public void setObserver(Observer observer)
    {
        this.observer = observer;
    }

    @Lob
    @Column(nullable = true)
    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    @Column(nullable = true)
    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getUpdated() 
    {
        return updated;
    }

    public void setUpdated(Date updated)
    {
        this.updated = updated;
    }

	@Override
	public String toString()
	{
		return "ObserverParameter{" +
				"id=" + id +
				", observer=" + observer +
				", value='" + value + '\'' +
				", error='" + error + '\'' +
				", updated=" + updated +
				'}';
	}
}
