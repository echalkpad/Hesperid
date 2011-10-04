/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.astina.hesperid.model.base;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author kstarosta
 */
@Entity
public class AssetSoftwareLicense 
{
    private Long id;
    private SoftwareLicense softwareLicense;
    private Date expirationDate;
    private String licenseKey;
    private String version;
    private String remark;
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
    
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getExpirationDate() 
    {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) 
    {
        this.expirationDate = expirationDate;
    }

    @Lob
    public String getLicenseKey() 
    {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) 
    {
        this.licenseKey = licenseKey;
    }

    @Lob
    public String getRemark() 
    {
        return remark;
    }

    public void setRemark(String remark) 
    {
        this.remark = remark;
    }

    @ManyToOne
    public SoftwareLicense getSoftwareLicense() 
    {
        return softwareLicense;
    }

    public void setSoftwareLicense(SoftwareLicense softwareLicense) 
    {
        this.softwareLicense = softwareLicense;
    }

    public String getVersion() 
    {
        return version;
    }

    public void setVersion(String version) 
    {
        this.version = version;
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
