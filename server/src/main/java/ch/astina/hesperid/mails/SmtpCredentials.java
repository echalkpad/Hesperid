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
package ch.astina.hesperid.mails;

import javax.mail.Authenticator;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class SmtpCredentials
{
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean startTls;
    private boolean smtpSsl;
    private static final int DEFAULT_PORT = 25;

    public SmtpCredentials(String host, String username, String password)
    {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = DEFAULT_PORT;
    }

    public Authenticator getAuthenticator()
    {
        return new MailAuthenticator(username, password);
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public boolean isStartTls()
    {
        return startTls;
    }

    public void setStartTls(boolean startTls)
    {
        this.startTls = startTls;
    }

    public boolean isSmtpSsl()
    {
        return smtpSsl;
    }

    public void setSmtpSsl(boolean smtpSsl)
    {
        this.smtpSsl = smtpSsl;
    }

    public boolean isAuthenticationRequired()
    {
        return username != null;
    }
}
