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
package ch.astina.hesperid.model.mail;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class HtmlMailMessage extends MailMessage
{
    private String htmlBody;
    private String altBody;
    private String htmlContentType;
    private String altContentType;

    public HtmlMailMessage()
    {
        htmlContentType = "text/html; charset=utf-8";
        altContentType = "text/plain; charset=utf-8";
    }

    public String getHtmlBody()
    {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody)
    {
        this.htmlBody = htmlBody;
    }

    public String getAltBody()
    {
        return altBody;
    }

    public void setAltBody(String altBody)
    {
        this.altBody = altBody;
    }

    public String getHtmlContentType()
    {
        return htmlContentType;
    }

    public void setHtmlContentType(String htmlContentType)
    {
        this.htmlContentType = htmlContentType;
    }

    public String getAltContentType()
    {
        return altContentType;
    }

    public void setAltContentType(String altContentType)
    {
        this.altContentType = altContentType;
    }
}
