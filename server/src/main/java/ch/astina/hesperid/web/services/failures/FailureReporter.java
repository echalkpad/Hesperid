package ch.astina.hesperid.web.services.failures;

import ch.astina.hesperid.model.base.Failure;
import ch.astina.hesperid.model.base.FailureEscalation;

public interface FailureReporter
{
    public void reportFailure(FailureEscalation escalation);

    public void reportResolution(FailureEscalation escalation);
}
