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

import java.util.List;

import groovy.lang.GroovyClassLoader;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import ch.astina.hesperid.groovy.ParameterGatherer;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 121 $, $Date: 2011-09-21 16:49:11 +0200 (Mi, 21 Sep 2011) $
 */
@Entity
public class ObserverStrategy {

    private Long id;
    private String name;
    private String description;
    private ObserverResultType resultType;
    private String groovyScript;
    private String possibleParameters;
    private String resultParameterName;
    private ObservationScope observationScope;
    private List<Observer> observers;

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
    public String getDescription() 
    {
        return description;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    @Enumerated(EnumType.STRING)
    public ObserverResultType getResultType() 
    {
        return resultType;
    }

    public void setResultType(ObserverResultType resultType)
    {
        this.resultType = resultType;
    }

    @Lob
    public String getGroovyScript() 
    {
        return groovyScript;
    }

    public void setGroovyScript(String groovyScript)
    {
        this.groovyScript = groovyScript;
    }

    public String getPossibleParameters() 
    {
        return possibleParameters;
    }

    public void setPossibleParameters(String possibleParameters) 
    {
        this.possibleParameters = possibleParameters;
    }

    public String getResultParameterName() 
    {
        return resultParameterName;
    }

    public void setResultParameterName(String resultParameterName) 
    {
        this.resultParameterName = resultParameterName;
    }

    @Enumerated(EnumType.STRING)
    public ObservationScope getObservationScope()
    {
        return observationScope;
    }

    public void setObservationScope(ObservationScope observationScope) 
    {
        this.observationScope = observationScope;
    }

    @OneToMany(mappedBy = "observerStrategy", fetch = FetchType.LAZY)
    @XmlTransient
    public List<Observer> getObservers()
    {
        return observers;
    }

    public void setObservers(List<Observer> observers)
    {
        this.observers = observers;
    }

    @SuppressWarnings("rawtypes")
    @Transient
    public ParameterGatherer getGroovyScriptInstance() throws InstantiationException,
            IllegalAccessException 
    {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
        Class clazz = groovyClassLoader.parseClass(getGroovyScript());
        return (ParameterGatherer) clazz.newInstance();
    }

    @Override
    public String toString()
    {
        return name + " (" + observationScope.name() + ")";
    }
}
