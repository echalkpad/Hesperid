package ch.astina.hesperid.web.services.failures;

import ch.astina.hesperid.model.base.EscalationLevel;
import ch.astina.hesperid.model.base.EscalationScheme;
import ch.astina.hesperid.model.base.Failure;
import ch.astina.hesperid.model.base.FailureStatus;

import java.util.Date;
import java.util.List;

/**
 * @author fharter
 */
public class EscalationSpecification
{
	
	private String registeredMessage = "";

	public boolean isSatisfiedBy(Failure failure)
	{
		return failureHasDetectedStatus(failure)
				&& escalationSchemeExists(failure)
				&& hasApplicableEscalationLevel(failure)
				&& applicableEscalationLevelCanTrigger(failure);
	}
	
	private boolean failureHasDetectedStatus(Failure failure)
	{
		if (failure.hasStatus(FailureStatus.ACKNOWLEDGED) || failure.hasStatus(FailureStatus.RESOLVED)) {
			registeredMessage = "Failure already acknowledged or resolved";
			return false;
		}

		return true;
	}

	private boolean escalationSchemeExists(Failure failure)
	{
		if (failure.getAsset().getEscalationScheme() == null) {
			registeredMessage = "Asset " + failure.getAsset() + " has no escalation scheme";
			return false;
		}

		return true;
	}

	private boolean hasApplicableEscalationLevel(Failure failure)
	{
		EscalationLevel level = EscalationSpecification.nextEscalationLevelFor(failure);
		if (level == null) {
			registeredMessage = "Escalation scheme "
					+ failure.getAsset().getEscalationScheme().getName()
					+ " for asset " + failure.getAsset().getAssetName()
					+ " has no applicable escalation level.";
			return false;
		}

		return true;
	}

	private boolean applicableEscalationLevelCanTrigger(Failure failure)
	{
		Date lastEscalation = failure.getEscalated();
		boolean canTrigger = false;

		if (lastEscalation == null) {
			// Since we have an applicable level, we can trigger this as the first one
			canTrigger = true;
		} else {
			EscalationLevel currentLevel = failure.getEscalationLevel();

			Date now = new Date();
			Long diff = now.getTime() - lastEscalation.getTime();
			canTrigger = diff > (currentLevel.getTimeout() * 1000);
		}

		if (!canTrigger) {
			registeredMessage = "No escalation level needs to be escalated right now.";
		}

		return canTrigger;
	}


	public static EscalationLevel nextEscalationLevelFor(Failure failure)
	{
		EscalationLevel currentLevel = failure.getEscalationLevel();
		EscalationScheme scheme = failure.getAsset().getEscalationScheme();
		List<EscalationLevel> levels = scheme.getEscalationLevels();

		for (EscalationLevel level : levels) {

			// no level set? use first level
			if (currentLevel == null) {
				return level;
			}

			// next level (levels should be ordered at this point)
			// {@link ch.astina.hesperid.model.base.EscalationScheme#getEscalationLevels()}
			if (level.getLevel() > currentLevel.getLevel()) {
				return level;
			}
		}

		return null;
	}

	public String getRegisteredMessage()
	{
		return registeredMessage;
	}
}
