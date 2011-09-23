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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class InputStreamDataSource implements DataSource
{
    private String name;
    private String contentType;
    private ByteArrayOutputStream baos;

    public InputStreamDataSource(String name, String contentType, InputStream inputStream)
            throws IOException
    {
        this.name = name;
        this.contentType = contentType;

        baos = new ByteArrayOutputStream();

        int read;
        byte[] buff = new byte[256];
        while ((read = inputStream.read(buff)) != -1) {
            baos.write(buff, 0, read);
        }
    }

    public String getContentType()
    {
        return contentType;
    }

    public InputStream getInputStream() throws IOException
    {
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public String getName()
    {
        return name;
    }

    public OutputStream getOutputStream() throws IOException
    {
        throw new IOException("Cannot write to this read-only resource");
    }
}
