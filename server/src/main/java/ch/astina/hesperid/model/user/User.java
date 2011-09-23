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
package ch.astina.hesperid.model.user;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
@SuppressWarnings("serial")
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = "username")})
public class User implements UserDetails
{
    private Long id;
    private String username;
    private String password;
    private boolean enabled;
    private Set<Role> roles = new HashSet<Role>();

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

    @Transient
    public GrantedAuthority[] getAuthorities()
    {
        GrantedAuthority[] authorities = new GrantedAuthority[getRoles().size()];
        int i = 0;
        for (Role role : getRoles()) {
            authorities[i++] = new GrantedAuthorityImpl(role.getName());
        }
        return authorities;
    }

    @NonVisual
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @ManyToMany
    public Set<Role> getRoles()
    {
        return roles;
    }

    public void setRoles(Set<Role> roles)
    {
        this.roles = roles;
    }

    @Transient
    @NonVisual
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Transient
    @NonVisual
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Transient
    @NonVisual
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public String toString()
    {
        return username;
    }
}
