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

/**
 * @author $Author: kstarosta $
 * @version $Revision: 121 $, $Date: 2011-09-21 16:49:11 +0200 (Mi, 21 Sep 2011) $
 */
@Entity
public class EscalationLevel 
{
    private Long id;

    private EscalationScheme escalationScheme;

    private String username;

    private String projectCode;

    private EscalationChannel channel;

    private int level;

    private int timeout;

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
    public EscalationScheme getEscalationScheme() 
    {
        return escalationScheme;
    }

    public void setEscalationScheme(EscalationScheme escalationScheme)
    {
        this.escalationScheme = escalationScheme;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getProjectCode()
    {
        return projectCode;
    }

    public void setProjectCode(String projectCode)
    {
        this.projectCode = projectCode;
    }

    @Enumerated(EnumType.STRING)
    public EscalationChannel getChannel()
    {
        return channel == null ? EscalationChannel.EMAIL : channel;
    }

    public void setChannel(EscalationChannel channel)
    {
        this.channel = channel;
    }
    
    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level) 
    {
        this.level = level;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }
}
