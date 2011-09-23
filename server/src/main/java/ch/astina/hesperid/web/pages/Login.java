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
package ch.astina.hesperid.web.pages;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Value;
import org.apache.tapestry5.services.Request;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class Login
{
    @Inject
    @Value("${spring-security.check.url}")
    private String checkUrl;
    @Inject
    private Request request;
    private boolean failed = false;

    public Object onActivate()
    {
        //String password = "admin";
        //ShaPasswordEncoder pe = new ShaPasswordEncoder();
        //System.out.println("pass: " + pe.encodePassword(password, "DEADBEEF"));

        return null;
    }

    public boolean isFailed()
    {
        return failed;
    }

    public String getLoginCheckUrl()
    {
        return request.getContextPath() + checkUrl;
    }

    void onActivate(String extra)
    {
        if (extra.equals("failed")) {
            failed = true;
        }
    }
}
