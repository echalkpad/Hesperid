/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.astina.hesperid.web.services.users.impl;

import ch.astina.hesperid.dao.UserDAO;
import ch.astina.hesperid.model.user.User;
import ch.astina.hesperid.web.services.users.impl.UserServiceImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Value;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 *
 * @author kstarosta
 */
public class MockUserServiceImpl extends UserServiceImpl
{
    public MockUserServiceImpl(UserDAO userDao, PasswordEncoder encoder, SaltSource salt,
            @Inject @Value("${spring-security.anonymous.attribute}") String anonymousAttribute)
    {
        super(userDao, encoder, salt, anonymousAttribute);
    }
    
    @Override
    public User getCurrentUser()
    {
        return new User();
    }
    
    
}
