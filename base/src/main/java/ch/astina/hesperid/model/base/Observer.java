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

import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 121 $, $Date: 2011-09-21 16:49:11 +0200 (Mi, 21 Sep 2011) $
 */
@Entity
public class Observer 
{
    private Long id;
    private String name;
    private Asset asset;
    private ObserverStrategy observerStrategy;
    private String parameters;
    private Long checkInterval;
    private Date lastCheck;
    private String expectedValue;
    private Float expectedValueMin;
    private Float expectedValueMax;
    private boolean monitor;
    private boolean showOnAssetOverview;
    private List<ObserverParameter> observerParameters;
    private List<Failure> failures;

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
    public ObserverStrategy getObserverStrategy() 
    {
        return observerStrategy;
    }

    public void setObserverStrategy(ObserverStrategy observerStrategy)
    {
        this.observerStrategy = observerStrategy;
    }

    @Lob
    public String getParameters()
    {
        return parameters;
    }

    public void setParameters(String parameters) 
    {
        this.parameters = parameters;
    }

    public Long getCheckInterval()
    {
        return checkInterval;
    }

    public void setCheckInterval(Long checkInterval)
    {
        this.checkInterval = checkInterval;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getLastCheck()
    {
        return lastCheck;
    }

    public void setLastCheck(Date lastCheck) 
    {
        this.lastCheck = lastCheck;
    }

    @Column(nullable = true)
    public String getExpectedValue() 
    {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) 
    {
        this.expectedValue = expectedValue;
    }

    @Column(nullable = true)
    public Float getExpectedValueMin() 
    {
        return expectedValueMin;
    }

    public void setExpectedValueMin(Float expectedValueMin)
    {
        this.expectedValueMin = expectedValueMin;
    }

    @Column(nullable = true)
    public Float getExpectedValueMax()
    {
        return expectedValueMax;
    }

    public void setExpectedValueMax(Float expectedValueMax) 
    {
        this.expectedValueMax = expectedValueMax;
    }

    public boolean isMonitor() 
    {
        return monitor;
    }

    public void setMonitor(boolean monitor) 
    {
        this.monitor = monitor;
    }

    public boolean isShowOnAssetOverview() 
    {
        return showOnAssetOverview;
    }

    public void setShowOnAssetOverview(boolean showOnAssetOverview) 
    {
        this.showOnAssetOverview = showOnAssetOverview;
    }

    @OneToMany(mappedBy = "observer", fetch = FetchType.LAZY)
    @OrderBy("updated desc")
    @XmlTransient
    public List<ObserverParameter> getObserverParameters() 
    {
        return observerParameters;
    }

    public void setObserverParameters(List<ObserverParameter> observerParameters) 
    {
        this.observerParameters = observerParameters;
    }

    @OneToMany(mappedBy = "observer", fetch = FetchType.LAZY)
    @Where(clause = "failure_status != 'RESOLVED'")
    @XmlTransient
    public List<Failure> getFailures() 
    {
        return failures;
    }

    public void setFailures(List<Failure> failures)
    {
        this.failures = failures;
    }

    @Transient
    public boolean isFailed() 
    {
        return failures != null && failures.size() > 0;
    }

    @Override
    public String toString() 
    {
        return "Observer [name=" + name + ", asset=" + asset.getAssetIdentifier() + ", observerStrategy="
                + observerStrategy.getName() + "]";
    }

    @Transient
    public Map<String, String> getParameterMap() 
    {
        Map<String, String> parameterMap = new HashMap<String, String>();
        if (parameters == null) {
            return parameterMap;
        }
        String[] nameValuePairs = parameters.split(",");

        for (String pair : nameValuePairs) {
            String[] nameAndValue = pair.split("=");

            if (nameAndValue.length == 2) {
                parameterMap.put(nameAndValue[0].trim(), nameAndValue[1].trim());
            }
        }

        return parameterMap;
    }
}
