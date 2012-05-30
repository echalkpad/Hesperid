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
package ch.astina.hesperid.web.pages.asset.relation;

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.ClientHierarchy;
import ch.astina.hesperid.web.pages.asset.AssetIndex;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.access.annotation.Secured;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class RelationIndex 
{
    @Property
    private Asset asset;
    
    @Property
    private ClientHierarchy clientHierarchy;
    
    @Inject
    private AssetDAO assetDAO;
    
    @Secured({"ROLE_ADMIN"})
    public void onActivate(Long assetId)
    {
        asset = assetDAO.getAssetForId(assetId);
    }
    
    public Object onActivate()
    {
        if (asset == null) {
            return AssetIndex.class;
        }
        
        return null;
    }
    
    @CommitAfter
    public void onActionFromDelete(ClientHierarchy ch)
    {
        assetDAO.deleteClientHierarchy(ch);
    }
    
    public Long onPassivate()
    {
        if (asset != null) {
            return asset.getId();
        }
        
        return null;
    }
    
}
