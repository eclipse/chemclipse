/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 * Philip Wenig - the exact name option has been added
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.xxd.classification.model.ClassificationRule;
import org.eclipse.chemclipse.xxd.classification.model.Reference;
import org.eclipse.chemclipse.xxd.classification.settings.ClassifierAssignFilterSettings;

public class ClassificationAssigner {

	public static void apply(IPeak peak, ClassifierAssignFilterSettings settings) {

		peak.removeClassifier("");
		//
		boolean isCaseSensitive = settings.isCaseSensitive();
		boolean useRegularExpression = settings.isUseRegularExpression();
		boolean isMatchPartly = settings.isMatchPartly();
		//
		for(IIdentificationTarget identificationTarget : peak.getTargets()) {
			for(ClassificationRule classificationRule : settings.getClassificationDictionary()) {
				String searchExpression = classificationRule.getSearchExpression();
				if(searchExpression != null && !searchExpression.isEmpty()) {
					String value = getTargetValue(identificationTarget, classificationRule.getReference(), isCaseSensitive);
					if(value != null && !value.isEmpty()) {
						/*
						 * Try to match the target value.
						 */
						boolean isMatch = false;
						if(useRegularExpression) {
							isMatch = matchByRegularExpression(value, searchExpression, isCaseSensitive, isMatchPartly);
						} else {
							isMatch = matchByString(value, getStringByCaseOption(searchExpression, isCaseSensitive), isMatchPartly);
						}
						//
						if(isMatch) {
							peak.addClassifier(classificationRule.getClassification());
						}
					}
				}
			}
		}
	}

	private static boolean matchByString(String value, String searchExpression, boolean isMatchPartly) {

		if(isMatchPartly) {
			return value.contains(searchExpression);
		} else {
			return value.equals(searchExpression);
		}
	}

	private static boolean matchByRegularExpression(String value, String searchExpression, boolean isCaseSensitive, boolean isMatchPartly) {

		Pattern pattern = createPattern(searchExpression, isCaseSensitive);
		Matcher matcher = pattern.matcher(value);
		//
		if(isMatchPartly) {
			return matcher.find();
		} else {
			return matcher.matches();
		}
	}

	private static Pattern createPattern(String searchExpression, boolean isCaseSensitive) {

		Pattern pattern = null;
		if(isCaseSensitive) {
			pattern = Pattern.compile(searchExpression);
		} else {
			pattern = Pattern.compile(searchExpression, Pattern.CASE_INSENSITIVE);
		}
		//
		return pattern;
	}

	private static String getTargetValue(IIdentificationTarget identificationTarget, Reference classificationReference, boolean isCaseSensitive) {

		String value = null;
		if(identificationTarget != null) {
			ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
			if(libraryInformation != null) {
				switch(classificationReference) {
					case NAME:
						value = getStringByCaseOption(libraryInformation.getName(), isCaseSensitive);
						break;
					case CAS:
						value = getStringByCaseOption(libraryInformation.getCasNumber(), isCaseSensitive);
						break;
					case REFERENCE_ID:
						value = getStringByCaseOption(libraryInformation.getReferenceIdentifier(), isCaseSensitive);
						break;
					default:
						break;
				}
			}
		}
		//
		return value;
	}

	private static String getStringByCaseOption(String value, boolean isCaseSensitive) {

		return isCaseSensitive ? value : value.toLowerCase();
	}
}