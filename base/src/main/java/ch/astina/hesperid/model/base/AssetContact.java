/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.astina.hesperid.model.base;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author kstarosta
 */
@Entity
public class AssetContact 
{
    private Long id;
    private BusinessRole businessRole;
    private Contact contact;
    private Asset asset;

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
    
    @ManyToOne
    public BusinessRole getBusinessRole() 
    {
        return businessRole;
    }

    public void setBusinessRole(BusinessRole businessRole) 
    {
        this.businessRole = businessRole;
    }

    @ManyToOne
    public Contact getContact() 
    {
        return contact;
    }

    public void setContact(Contact contact) 
    {
        this.contact = contact;
    }

    @ManyToOne
    public Asset getAsset() 
    {
        return asset;
    }

    public void setAsset(Asset asset) 
    {
        this.asset = asset;
    }
}
