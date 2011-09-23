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

import ch.astina.hesperid.mails.InputStreamDataSource;
import java.io.IOException;

import javax.activation.DataSource;

import org.apache.tapestry5.StreamResponse;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class StreamResponseMailAttachment extends MailAttachment
{
    private StreamResponse response;
    private String name;

    public StreamResponseMailAttachment(StreamResponse response, String name)
    {
        this.response = response;
        this.name = name;
    }

    @Override
    public DataSource getDataSource() throws IOException
    {
        return this.response != null ? new InputStreamDataSource("",
                this.response.getContentType(), this.response.getStream()) : null;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getMimeType()
    {
        return this.response != null ? this.response.getContentType() : null;
    }
}
