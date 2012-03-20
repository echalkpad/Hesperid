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
import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 121 $, $Date: 2011-09-21 16:49:11 +0200 (Mi, 21 Sep 2011) $
 */
@Entity
public class Failure {

    private Long id;
    private Asset asset;
    private EscalationLevel escalationLevel;
    private FailureStatus failureStatus;
    private Observer observer;
    private ObserverParameter observerParameter;
    private Date detected;
    /**
     * Date of last escalation
     */
    private Date escalated;
    private Date acknowledged;
    private Date resolved;
    private String message;
    private List<FailureEscalation> failureEscalations;

    @Override
    public String toString() 
    {
        return String.format("%s: %s", asset.getAssetIdentifier(), message);
    }

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
    public Asset getAsset() 
    {
        return asset;
    }

    public void setAsset(Asset asset) 
    {
        this.asset = asset;
    }

    @ManyToOne
    public EscalationLevel getEscalationLevel()
    {
        return escalationLevel;
    }

    public void setEscalationLevel(EscalationLevel escalationLevel) 
    {
        this.escalationLevel = escalationLevel;
    }

    @Enumerated(EnumType.STRING)
    public FailureStatus getFailureStatus()
    {
        return failureStatus;
    }

    public void setFailureStatus(FailureStatus failureStatus) 
    {
        this.failureStatus = failureStatus;
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

    @ManyToOne
    public ObserverParameter getObserverParameter() 
    {
        return observerParameter;
    }

    public void setObserverParameter(ObserverParameter observerParameter) 
    {
        this.observerParameter = observerParameter;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDetected()
    {
        return detected;
    }

    public void setDetected(Date detected) 
    {
        this.detected = detected;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getEscalated() 
    {
        return escalated;
    }

    public void setEscalated(Date escalated) 
    {
        this.escalated = escalated;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getAcknowledged() 
    {
        return acknowledged;
    }

    public void setAcknowledged(Date acknowledged) 
    {
        this.acknowledged = acknowledged;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getResolved() 
    {
        return resolved;
    }

    public void setResolved(Date resolved) 
    {
        this.resolved = resolved;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message) 
    {
        this.message = message;
    }

    @OneToMany(mappedBy = "failure", fetch = FetchType.LAZY)
    public List<FailureEscalation> getFailureEscalations() 
    {
        return failureEscalations;
    }

    public void setFailureEscalations(List<FailureEscalation> failureEscalations) 
    {
        this.failureEscalations = failureEscalations;
    }

    @Transient
    public boolean isStatusDetected()
    {
        return failureStatus != null && failureStatus.equals(FailureStatus.DETECTED);
    }

    @Transient
    public boolean isStatusAcknowledged()
    {
        return failureStatus != null && failureStatus.equals(FailureStatus.ACKNOWLEDGED);
    }

    @Transient
    public boolean isStatusResolved() 
    {
        return failureStatus != null && failureStatus.equals(FailureStatus.RESOLVED);
    }

	@Transient
	public boolean hasStatus(FailureStatus status)
	{
		return failureStatus != null && failureStatus.equals(status);
	}
}
