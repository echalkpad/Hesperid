package ch.astina.hesperid.model.internal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author fharter
 */
@Entity
public class SystemSettings
{
	private Long id;
	private Long dataDeletionBarrier;

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

	public Long getDataDeletionBarrier()
	{
		return dataDeletionBarrier;
	}

	public void setDataDeletionBarrier(Long dataDeletionBarrier)
	{
		this.dataDeletionBarrier = dataDeletionBarrier;
	}
}
