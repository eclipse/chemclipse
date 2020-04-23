/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.xxd.model.settings.peaks.ClassifierFilterSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class ClassifierFilter implements IPeakFilter<ClassifierFilterSettings> {

	@Override
	public String getName() {

		return "Peak Classifier";
	}

	@Override
	public String getDescription() {

		return "Filter peaks and add peak classifier (e.g. for the whole name or parts of the name).";
	}

	@Override
	public Class<ClassifierFilterSettings> getConfigClass() {

		return ClassifierFilterSettings.class;
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, ClassifierFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> read = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(read);
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());
		LinkedHashMap<String, String> parsedUserDefinedValues = parseUserDefinedValuesAsMap(configuration);
		for(X peak : read) {
			setPeakClassifier(configuration, parsedUserDefinedValues, peak);
			subMonitor.worked(1);
		}
	}

	private static LinkedHashMap<String, String> parseUserDefinedValuesAsMap(ClassifierFilterSettings configuration) {

		String userDefinedMatchExpression = configuration.getUserDefinedMatchExpression();
		List<String> expressions = new ArrayList<String>();
		if(!userDefinedMatchExpression.isEmpty()) {
			expressions.addAll(parseExpressions(userDefinedMatchExpression));
		} else {
			throw new IllegalArgumentException("No match expressions defined!");
		}
		List<String> classifications = new ArrayList<String>();
		String matchClassification = configuration.getMatchClassification();
		if(!matchClassification.isEmpty()) {
			classifications.addAll(parseExpressions(matchClassification));
		} else {
			throw new IllegalArgumentException("No match classifications defined!");
		}
		if(expressions.size() != classifications.size()) {
			throw new IllegalArgumentException("The number of Expressions is not the same as the number of Classifications.");
		}
		LinkedHashMap<String, String> parsedUserDefinedValues = new LinkedHashMap<String, String>();
		for(int i = 0; i < expressions.size(); i++) {
			parsedUserDefinedValues.put(expressions.get(i), classifications.get(i));
		}
		return parsedUserDefinedValues;
	}

	private static List<String> parseExpressions(String userExpressions) {

		List<String> expressionsList = new ArrayList<String>();
		String[] tempExpressions = userExpressions.split(System.getProperty("line.separator"));
		for(int i = 0; i < tempExpressions.length; i++) {
			if(!tempExpressions[i].isEmpty()) {
				expressionsList.add(tempExpressions[i]);
			}
		}
		return expressionsList;
	}

	private static <X extends IPeak> void setPeakClassifier(ClassifierFilterSettings configuration, LinkedHashMap<String, String> parsedUserDefinedValues, X peak) {

		peak.removeClassifier("");
		//
		for(IIdentificationTarget target : peak.getTargets()) {
			String substanceName = extractSubstanceName(configuration, target);
			if(substanceName != null) {
				for(Map.Entry<String, String> entry : parsedUserDefinedValues.entrySet()) {
					Pattern pattern = createPattern(configuration, entry);
					Matcher matcher = pattern.matcher(substanceName);
					/*
					 * UserDefinedMatchClassification for the cases when
					 * 1) the occurrence of the regular expression is found in identified name or
					 * 2) the whole text is found in identified name
					 */
					if(configuration.isWildcardSearch()) {
						if(matcher.find()) {
							peak.addClassifier(entry.getValue());
						}
					} else {
						if(matcher.matches()) {
							peak.addClassifier(entry.getValue());
						}
					}
				}
			}
		}
	}

	private static Pattern createPattern(ClassifierFilterSettings configuration, Map.Entry<String, String> entry) {

		Pattern pattern = null;
		/*
		 * UserDefinedMatchExpression
		 */
		if(configuration.isIgnoreUppercase()) {
			pattern = Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(entry.getKey());
		}
		return pattern;
	}

	/*
	 * Could return null.
	 */
	private static String extractSubstanceName(ClassifierFilterSettings configuration, IIdentificationTarget identificationTarget) {

		if(identificationTarget != null) {
			ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
			if(libraryInformation != null) {
				if(configuration.isIgnoreUppercase()) {
					return libraryInformation.getName().toLowerCase();
				} else {
					return libraryInformation.getName();
				}
			}
		}
		//
		return null;
	}
}