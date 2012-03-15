package ch.astina.hesperid.web.services.jobs.impl;

import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import ch.astina.hesperid.model.base.ObserverResultType;

import java.util.regex.Pattern;

/**
 * @author fharter
 */
public class ObserverParameterValidator
{
	
	private String validationMessage = "";

	public boolean isValid(ObserverParameter parameter)
	{
		Observer observer = parameter.getObserver();
		ObserverResultType resultType = observer.getObserverStrategy().getResultType();

		if (resultType.isNumeric()) {
			validateNumeric(observer, parameter);
		} else if (resultType.equals(ObserverResultType.BOOLEAN)) {
			validateBoolean(observer, parameter);
		} else if (resultType.equals(ObserverResultType.STRING)) {
			validateString(observer, parameter);
		}

		if (hasValidationErrors()) {
			return false;
		}

		return true;
	}

	private void validateNumeric(Observer observer, ObserverParameter parameter)
	{
		if (observer.getExpectedValueMin() != null || observer.getExpectedValueMax() != null) {
			float value = Float.parseFloat(parameter.getValue());

			if (observer.getExpectedValueMin() != null && value < observer.getExpectedValueMin()) {
				validationMessage = "Parameter value is below expected minimum";
			}
			if (observer.getExpectedValueMax() != null && value > observer.getExpectedValueMax()) {
				validationMessage = "Parameter value is above expected maximum";
			}
		} else {
			validationMessage = "Expected min and/or max not set.";
		}
	}

	private void validateBoolean(Observer observer, ObserverParameter parameter)
	{
		if (!parameter.getValue().equals(observer.getExpectedValue())) {
			validationMessage = "Parameter value does not match expected value";
		}
	}

	private void validateString(Observer observer, ObserverParameter parameter)
	{
		Pattern pattern = Pattern.compile(observer.getExpectedValue().toString(), Pattern.DOTALL | Pattern.MULTILINE);

		if (!pattern.matcher(parameter.getValue()).matches()) {
			validationMessage = "Parameter value does not match expected value";
		}
	}

	public boolean hasValidationErrors()
	{
		return !validationMessage.isEmpty();
	}

	public String getValidationMessage()
	{
		return validationMessage;
	}
}
