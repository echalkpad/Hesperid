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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public abstract class MailMessage
{
    private String subject;
    private InternetAddress sender;
    private Set<InternetAddress> toRecipients = new HashSet<InternetAddress>();
    private Set<InternetAddress> ccRecipients = new HashSet<InternetAddress>();
    private Set<InternetAddress> bccRecipients = new HashSet<InternetAddress>();
    private List<MailAttachment> attachments = new ArrayList<MailAttachment>();

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public InternetAddress getSender()
    {
        return sender;
    }

    public void setSender(InternetAddress sender)
    {
        this.sender = sender;
    }

    public Set<InternetAddress> getToRecipients()
    {
        return toRecipients;
    }

    public void setToRecipients(Set<InternetAddress> toRecipients)
    {
        this.toRecipients = toRecipients;
    }

    public void addToRecipient(InternetAddress recipient)
    {
        if (getToRecipients().contains(recipient) == false) {
            getToRecipients().add(recipient);
        }
    }

    public Set<InternetAddress> getCcRecipients()
    {
        return ccRecipients;
    }

    public void setCcRecipients(Set<InternetAddress> ccRecipients)
    {
        this.ccRecipients = ccRecipients;
    }

    public void addCcRecipient(InternetAddress recipient)
    {
        if (getCcRecipients().contains(recipient) == false) {
            getCcRecipients().add(recipient);
        }
    }

    public Set<InternetAddress> getBccRecipients()
    {
        return bccRecipients;
    }

    public void setBccRecipients(Set<InternetAddress> bccRecipients)
    {
        this.bccRecipients = bccRecipients;
    }

    public void addBccRecipient(InternetAddress recipient)
    {
        if (getBccRecipients().contains(recipient) == false) {
            getBccRecipients().add(recipient);
        }
    }

    public InternetAddress[] getToRecipientsArray()
    {
        return toAddressArray(getToRecipients());
    }

    public InternetAddress[] getCcRecipientsArray()
    {
        return toAddressArray(getCcRecipients());
    }

    public InternetAddress[] getBccRecipientsArray()
    {
        return toAddressArray(getBccRecipients());
    }

    private InternetAddress[] toAddressArray(Set<InternetAddress> recipients)
    {
        InternetAddress[] addresses = new InternetAddress[recipients.size()];
        int i = 0;
        for (InternetAddress a : recipients) {
            addresses[i] = a;
            i++;
        }
        return addresses;
    }

    public void addAttachment(MailAttachment mailAttachment)
    {
        attachments.add(mailAttachment);
    }

    public void addAttachments(List<MailAttachment> mailAttachments)
    {
        attachments.addAll(mailAttachments);
    }

    public List<MailAttachment> getAttachments()
    {
        return attachments;
    }

    public boolean hasAttachments()
    {
        return attachments != null && attachments.size() > 0;
    }
}
