/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.targets;

import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class TargetValidator implements IValidator<Object> {

	public static final String IDENTIFIER = "org.eclipse.chemclipse.model.targets.manual"; // $NON-NLS-N$
	//
	private static final String ERROR_TARGET = "Please enter target, e.g.: " + TargetListUtil.EXAMPLE;
	private static final String ERROR_TOKEN = "The target must not contain: " + TargetListUtil.SEPARATOR_TOKEN;
	//
	private String name = "";
	private String casNumber = "";
	private String comments = "";
	private String contributor = "";
	private String referenceId = "";

	@Override
	public IStatus validate(Object value) {

		/*
		 * Reset
		 */
		name = "";
		casNumber = "";
		comments = "";
		contributor = "";
		referenceId = "";
		//
		String message = null;
		if(value == null) {
			message = ERROR_TARGET;
		} else {
			if(value instanceof String text) {
				text = text.trim();
				if(text.contains(TargetListUtil.SEPARATOR_TOKEN)) {
					message = ERROR_TOKEN;
				} else if("".equals(text.trim())) {
					message = ERROR_TARGET;
				} else {
					/*
					 * Extract name, casNumber, ...
					 */
					String[] values = text.trim().split("\\" + TargetListUtil.SEPARATOR_ENTRY); // The pipe needs to be escaped.
					if(values.length > 0) {
						name = values[0].trim();
						//
						if(values.length > 1) {
							casNumber = values[1].trim();
						}
						//
						if(values.length > 2) {
							comments = values[2].trim();
						}
						//
						if(values.length > 3) {
							contributor = values[3].trim();
						}
						//
						if(values.length > 4) {
							referenceId = values[4].trim();
						}
					} else {
						message = ERROR_TARGET;
					}
				}
			} else {
				message = ERROR_TARGET;
			}
		}
		//
		if(message != null) {
			return ValidationStatus.error(message);
		} else {
			return ValidationStatus.ok();
		}
	}

	public String getName() {

		return name;
	}

	public String getCasNumber() {

		return casNumber;
	}

	public String getComments() {

		return comments;
	}

	public String getContributor() {

		return contributor;
	}

	public String getReferenceId() {

		return referenceId;
	}

	public IIdentificationTarget getIdentificationTarget() {

		ILibraryInformation libraryInformation = new LibraryInformation();
		libraryInformation.setName(name);
		libraryInformation.setCasNumber(casNumber);
		libraryInformation.setComments(comments);
		libraryInformation.setContributor(contributor);
		libraryInformation.setReferenceIdentifier(referenceId);
		IComparisonResult comparisonResult = ComparisonResult.COMPARISON_RESULT_BEST_MATCH;
		IIdentificationTarget identificationTarget = new IdentificationTarget(libraryInformation, comparisonResult);
		identificationTarget.setIdentifier(IDENTIFIER);
		//
		return identificationTarget;
	}
}
