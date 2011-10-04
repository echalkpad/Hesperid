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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 121 $, $Date: 2011-09-21 16:49:11 +0200 (Mi, 21 Sep 2011) $
 */
@Entity
public class Asset 
{
    private Long id;
    private String assetIdentifier;
    private String assetName;
    private String host;
    private String description;
    private System system;
    private Location location;
    private String roomNumber;
    private String carePack;
    private Date purchased;
    private List<ClientHierarchy> clientHierarchies;
    private List<Observer> observers;
    private List<AssetContact> assetContacts;
    private List<AssetSoftwareLicense> assetSoftwareLicenses;
    private Date lastUpdatedObserver;
    private Date lastTickReceived;
    private boolean managed;
    private EscalationScheme escalationScheme;
    private BigDecimal costPerYear;

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

    @Column(unique = true)
    public String getAssetIdentifier() 
    {
        return assetIdentifier;
    }

    public void setAssetIdentifier(String assetIdentifier) 
    {
        this.assetIdentifier = assetIdentifier;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) 
    {
        this.assetName = assetName;
    }

    public String getCarePack()
    {
        return carePack;
    }

    public void setCarePack(String carePack) 
    {
        this.carePack = carePack;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getHost() 
    {
        return host;
    }

    public void setHost(String host) 
    {
        this.host = host;
    }

    @ManyToOne
    public Location getLocation() 
    {
        return location;
    }

    public void setLocation(Location location) 
    {
        this.location = location;
    }

    @Temporal(TemporalType.DATE)
    public Date getPurchased() 
    {
        return purchased;
    }

    public void setPurchased(Date purchased) 
    {
        this.purchased = purchased;
    }

    public String getRoomNumber()
    {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber)
    {
        this.roomNumber = roomNumber;
    }

    @ManyToOne
    public System getSystem() {
        return system;
    }

    public void setSystem(System system) 
    {
        this.system = system;
    }

    @XmlTransient
    @OneToMany(mappedBy = "firstAsset")
    public List<ClientHierarchy> getClientHierarchies()
    {
        return clientHierarchies;
    }

    public void setClientHierarchies(List<ClientHierarchy> clientHierarchies)
    {
        this.clientHierarchies = clientHierarchies;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdatedObserver()
    {
        return lastUpdatedObserver;
    }

    public void setLastUpdatedObserver(Date lastUpdatedObserver)
    {
        this.lastUpdatedObserver = lastUpdatedObserver;
    }

    @XmlTransient
    @OneToMany(mappedBy = "asset")
    public List<Observer> getObservers() 
    {
        return observers;
    }

    public void setObservers(List<Observer> observers)
    {
        this.observers = observers;
    }

    public BigDecimal getCostPerYear() 
    {
        return costPerYear;
    }

    public void setCostPerYear(BigDecimal costPerYear) 
    {    
        this.costPerYear = costPerYear;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastTickReceived()
    {
        return lastTickReceived;
    }

    public void setLastTickReceived(Date lastTickReceived) 
    {
        this.lastTickReceived = lastTickReceived;
    }

    public boolean isManaged()
    {
        return managed;
    }

    public void setManaged(boolean managed)
    {
        this.managed = managed;
    }

    @XmlTransient
    @ManyToOne
    public EscalationScheme getEscalationScheme()
    {
        return escalationScheme;
    }

    public void setEscalationScheme(EscalationScheme escalationScheme)
    {
        this.escalationScheme = escalationScheme;
    }

    @XmlTransient
    @OneToMany(mappedBy="asset")
    public List<AssetContact> getAssetContacts() 
    {
        return assetContacts;
    }

    public void setAssetContacts(List<AssetContact> assetContacts) 
    {
        this.assetContacts = assetContacts;
    }

    @XmlTransient
    @OneToMany(mappedBy="asset")
    public List<AssetSoftwareLicense> getAssetSoftwareLicenses() 
    {
        return assetSoftwareLicenses;
    }

    public void setAssetSoftwareLicenses(List<AssetSoftwareLicense> assetSoftwareLicenses) 
    {
        this.assetSoftwareLicenses = assetSoftwareLicenses;
    }

    @XmlTransient
    @Transient
    public boolean getErrorOccured() 
    {
        for (Observer observer : getObservers()) {
            if (observer.isFailed()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString()
    {
        return getAssetName() + " (" + getAssetIdentifier() + ")";
    }

    @Transient
    public boolean isAgentInstalled()
    {
        return lastTickReceived != null;
    }
}
