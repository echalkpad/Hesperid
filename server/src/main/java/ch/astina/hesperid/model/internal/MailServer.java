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

/**
 * @author $Author: kstarosta $
 * @version $Revision: 103 $, $Date: 2011-09-21 15:51:59 +0200 (Mi, 21 Sep 2011) $
 */
@Entity
public class MailServer
{
    private Long id;
    private String name;
    private String host;
    private String userName;
    private String password;
    private String defaultSender;
    private String port;
    private MailServerSecureConnectionType mailServerSecureConnectionType;

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

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public MailServerSecureConnectionType getMailServerSecureConnectionType()
    {
        return mailServerSecureConnectionType;
    }

    public void setMailServerSecureConnectionType(MailServerSecureConnectionType mailServerSecureConnectionType)
    {
        this.mailServerSecureConnectionType = mailServerSecureConnectionType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDefaultSender()
    {
        return defaultSender;
    }

    public void setDefaultSender(String defaultSender)
    {
        this.defaultSender = defaultSender;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
}
