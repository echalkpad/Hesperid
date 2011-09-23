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
package ch.astina.hesperid.web.services.users;

import ch.astina.hesperid.dao.UserDAO;
import ch.astina.hesperid.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class CarpetUserDetailsService implements UserDetailsService
{
    private UserDAO userDAO;
    private static Logger logger = LoggerFactory.getLogger(CarpetUserDetailsService.class);

    public CarpetUserDetailsService(PasswordEncoder encoder, SaltSource salt, UserDAO userDao)
    {
        this.userDAO = userDao;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException
    {
        User user = userDAO.getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        logger.info("Loaded user: " + user.getUsername());
        return user;
    }
}
