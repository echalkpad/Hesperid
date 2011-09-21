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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 121 $, $Date: 2011-09-21 16:49:11 +0200 (Mi, 21 Sep 2011) $
 */
@Entity
public class ClientHierarchy
{
    private Long id;
    private Asset firstAsset;
    private Asset secondAsset;
    private ClientRelationType clientRelationType;

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

    public ClientRelationType getClientRelationType()
    {
        return clientRelationType;
    }

    public void setClientRelationType(ClientRelationType clientRelationType)
    {
        this.clientRelationType = clientRelationType;
    }

    @ManyToOne
    public Asset getFirstAsset()
    {
        return firstAsset;
    }

    public void setFirstAsset(Asset firstAsset)
    {
        this.firstAsset = firstAsset;
    }

    @ManyToOne
    public Asset getSecondAsset()
    {
        return secondAsset;
    }

    public void setSecondAsset(Asset secondAsset)
    {
        this.secondAsset = secondAsset;
    }
}
