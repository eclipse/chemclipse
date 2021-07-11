/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
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
import org.eclipse.chemclipse.xxd.model.settings.peaks.ClassifierAssignFilterSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class ClassifierAssignFilter implements IPeakFilter<ClassifierAssignFilterSettings> {

	@Override
	public String getName() {

		return "Peak Classifier (Assign)";
	}

	@Override
	public String getDescription() {

		return "Filter peaks and add peak classifier (e.g. for the whole name or parts of the name).";
	}

	@Override
	public Class<ClassifierAssignFilterSettings> getConfigClass() {

		return ClassifierAssignFilterSettings.class;
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, ClassifierAssignFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> peaks = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		SubMonitor subMonitor = SubMonitor.convert(monitor, peaks.size());
		LinkedHashMap<String, String> parsedUserDefinedValues = parseClassificationsMap(configuration);
		for(X peak : peaks) {
			setPeakClassifier(configuration, parsedUserDefinedValues, peak);
			subMonitor.worked(1);
		}
	}

	private static LinkedHashMap<String, String> parseClassificationsMap(ClassifierAssignFilterSettings configuration) {

		List<String> matchExpressions = getMatchExpressions(configuration);
		List<String> matchClassifications = getMatchClassifications(configuration);
		validateSettings(matchExpressions, matchClassifications);
		/*
		 * Create the map.
		 */
		LinkedHashMap<String, String> classificationsMap = new LinkedHashMap<String, String>();
		for(int i = 0; i < matchExpressions.size(); i++) {
			classificationsMap.put(matchExpressions.get(i), matchClassifications.get(i));
		}
		//
		return classificationsMap;
	}

	private static void validateSettings(List<String> matchExpressions, List<String> matchClassifications) {

		if(matchExpressions.size() == 0) {
			throw new IllegalArgumentException("The match expressions list is empty.");
		} else if(matchClassifications.size() == 0) {
			throw new IllegalArgumentException("The match classifications list is empty.");
		} else if(matchExpressions.size() != matchClassifications.size()) {
			throw new IllegalArgumentException("The match expressions and classifications list do not have the same size.");
		}
	}

	private static List<String> getMatchExpressions(ClassifierAssignFilterSettings configuration) {

		String matchExpressions = configuration.getMatchExpressions();
		List<String> expressions = new ArrayList<String>();
		if(!matchExpressions.isEmpty()) {
			/*
			 * For a better performance, convert the expression
			 * by case option here.
			 */
			for(String matchExpression : parseExpressions(matchExpressions)) {
				expressions.add(getStringByCaseOption(matchExpression, configuration));
			}
		}
		//
		return expressions;
	}

	private static List<String> getMatchClassifications(ClassifierAssignFilterSettings configuration) {

		String matchClassifications = configuration.getMatchClassifications();
		List<String> classifications = new ArrayList<String>();
		if(!matchClassifications.isEmpty()) {
			/*
			 * No case option needed here, as the classification
			 * shall be set as is.
			 */
			classifications.addAll(parseExpressions(matchClassifications));
		}
		//
		return classifications;
	}

	private static List<String> parseExpressions(String expressions) {

		/*
		 * Windows, macOS and Linux/Unix
		 * line separators must be matched.
		 */
		List<String> expressionsList = new ArrayList<String>();
		String[] expressionsArray = expressions.split("\\r?\\n");
		//
		for(int i = 0; i < expressionsArray.length; i++) {
			if(!expressionsArray[i].isEmpty()) {
				expressionsList.add(expressionsArray[i]);
			}
		}
		//
		return expressionsList;
	}

	private static <X extends IPeak> void setPeakClassifier(ClassifierAssignFilterSettings configuration, LinkedHashMap<String, String> classificationsMap, X peak) {

		peak.removeClassifier("");
		boolean useRegularExpression = configuration.isUseRegularExpression();
		//
		for(IIdentificationTarget identificationTarget : peak.getTargets()) {
			String substanceName = extractSubstanceName(configuration, identificationTarget);
			if(substanceName != null) {
				for(Map.Entry<String, String> entry : classificationsMap.entrySet()) {
					if(useRegularExpression) {
						matchByRegularExpression(peak, substanceName, configuration, entry);
					} else {
						matchByString(peak, substanceName, configuration, entry);
					}
				}
			}
		}
	}

	private static <X extends IPeak> void matchByString(X peak, String substanceName, ClassifierAssignFilterSettings configuration, Map.Entry<String, String> entry) {

		if(configuration.isMatchPartly()) {
			if(substanceName.contains(entry.getKey())) {
				peak.addClassifier(entry.getValue());
			}
		} else {
			if(substanceName.equals(entry.getKey())) {
				peak.addClassifier(entry.getValue());
			}
		}
	}

	private static <X extends IPeak> void matchByRegularExpression(X peak, String substanceName, ClassifierAssignFilterSettings configuration, Map.Entry<String, String> entry) {

		Pattern pattern = createPattern(configuration, entry);
		Matcher matcher = pattern.matcher(substanceName);
		if(configuration.isMatchPartly()) {
			if(matcher.find()) {
				peak.addClassifier(entry.getValue());
			}
		} else {
			if(matcher.matches()) {
				peak.addClassifier(entry.getValue());
			}
		}
	}

	private static Pattern createPattern(ClassifierAssignFilterSettings configuration, Map.Entry<String, String> entry) {

		Pattern pattern = null;
		if(configuration.isCaseSensitive()) {
			pattern = Pattern.compile(entry.getKey());
		} else {
			pattern = Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE);
		}
		//
		return pattern;
	}

	private static String extractSubstanceName(ClassifierAssignFilterSettings configuration, IIdentificationTarget identificationTarget) {

		if(identificationTarget != null) {
			ILibraryInformation libraryInformation = identificationTarget.getLibraryInformation();
			if(libraryInformation != null) {
				return getStringByCaseOption(libraryInformation.getName(), configuration);
			}
		}
		//
		return null;
	}

	private static String getStringByCaseOption(String value, ClassifierAssignFilterSettings configuration) {

		return configuration.isCaseSensitive() ? value : value.toLowerCase();
	}
}