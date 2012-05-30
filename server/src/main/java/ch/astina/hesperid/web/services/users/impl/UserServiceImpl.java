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
package ch.astina.hesperid.web.services.users.impl;

import ch.astina.hesperid.dao.UserDAO;
import ch.astina.hesperid.model.user.User;
import ch.astina.hesperid.web.services.users.UserService;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Value;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class UserServiceImpl implements UserService
{
    private UserDAO userDao;
    private PasswordEncoder encoder;
    private SaltSource salt;
    private String anonymousUsername;

    public UserServiceImpl(UserDAO userDao, PasswordEncoder encoder, SaltSource salt,
            @Inject @Value("${spring-security.anonymous.attribute}") String anonymousAttribute)
    {
        this.userDao = userDao;
        this.encoder = encoder;
        this.salt = salt;
        this.anonymousUsername = parseAnonymousUsername(anonymousAttribute);
    }

    public User getCurrentUser()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.isAuthenticated() == false) {
            return null;
        }
        String username = auth.getName();
        if (username.isEmpty()) {
            return null;
        }
        return userDao.getUserByName(username);
    }

    public boolean isAuthenticated()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && auth.getName() != null
                && auth.getName().isEmpty() == false
                && auth.getName().equals(anonymousUsername) == false;
    }

    public boolean isUsernameAvailable(String username)
    {
        return userDao.getUserByName(username) == null;
    }

    public void setUserPassword(User user, String password)
    {
        user.setPassword(encodePassword(password, user));
    }

    private String encodePassword(String password, UserDetails user)
    {
        return encoder.encodePassword(password, salt.getSalt(user));
    }

    private String parseAnonymousUsername(String anonymousAttribute)
    {
        String[] tokens = anonymousAttribute.split(",");
        return tokens.length == 0 ? "anonymous" : tokens[0];
    }
}
