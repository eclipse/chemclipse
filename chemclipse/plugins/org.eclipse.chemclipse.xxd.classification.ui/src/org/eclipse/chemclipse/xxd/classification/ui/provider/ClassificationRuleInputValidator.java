/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.ui.provider;

import org.eclipse.chemclipse.xxd.classification.model.ClassificationDictionary;
import org.eclipse.chemclipse.xxd.classification.model.ClassificationRule;
import org.eclipse.chemclipse.xxd.classification.validators.ClassificationRuleValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IInputValidator;

public class ClassificationRuleInputValidator implements IInputValidator {

	private ClassificationRuleValidator validator = new ClassificationRuleValidator();
	private ClassificationDictionary dictionary = new ClassificationDictionary();

	public ClassificationRuleInputValidator(ClassificationDictionary dictionary) {

		if(dictionary != null) {
			this.dictionary = dictionary;
		}
	}

	@Override
	public String isValid(String target) {

		IStatus status = validator.validate(target);
		if(status.isOK()) {
			ClassificationRule classificationRule = validator.getSetting();
			if(dictionary.contains(classificationRule)) {
				return "The rule already exists.";
			}
		} else {
			return status.getMessage();
		}
		return null;
	}
}