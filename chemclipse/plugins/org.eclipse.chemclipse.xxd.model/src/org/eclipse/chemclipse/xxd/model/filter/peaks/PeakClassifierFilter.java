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
import java.util.Set;
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = { IPeakFilter.class, Filter.class, Processor.class })
public class PeakClassifierFilter implements IPeakFilter<PeakClassifierFilterSettings> {

	@Override
	public String getName() {

		return "Peak Classifier Filter";
	}

	@Override
	public String getDescription() {

		return "Filter peaks and add peak classifier (e.g. for whole name or parts of the name.)";
	}

	@Override
	public Class<PeakClassifierFilterSettings> getConfigClass() {

		return PeakClassifierFilterSettings.class;
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, PeakClassifierFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

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

	private static LinkedHashMap<String, String> parseUserDefinedValuesAsMap(PeakClassifierFilterSettings configuration) {

		String userDefinedMatchExpression = configuration.getUserDefinedMatchExpression();
		String matchClassification = configuration.getMatchClassification();
		List<List<String>> matchList = new ArrayList<List<String>>();
		if(!userDefinedMatchExpression.isEmpty()) {
			matchList.add(parseExpressions(userDefinedMatchExpression));
		} else {
			throw new IllegalArgumentException("No match expressions defined!");
		}
		if(!matchClassification.isEmpty()) {
			matchList.add(parseExpressions(matchClassification));
		} else {
			throw new IllegalArgumentException("No match classifications defined!");
		}
		if(matchList.get(0).size() != matchList.get(1).size()) {
			throw new IllegalArgumentException("The number of Expressions is not the same as the number of Classifications.");
		}
		LinkedHashMap<String, String> parsedUserDefinedValues = new LinkedHashMap<String, String>();
		for(int i = 0; i < matchList.get(0).size(); i++) {
			parsedUserDefinedValues.put(matchList.get(0).get(i),matchList.get(1).get(i));
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

	private static <X extends IPeak> void setPeakClassifier(PeakClassifierFilterSettings configuration, LinkedHashMap<String, String> parsedUserDefinedValues, X peak) {

		peak.removeClassifier("");
		String substanceName = extractSubstanceName(configuration, peak);
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

	private static Pattern createPattern(PeakClassifierFilterSettings configuration, Map.Entry<String, String> entry) {

		Pattern pattern = null;
		if(configuration.isIgnoreUppercase()) { // UserDefinedMatchExpression
			pattern = Pattern.compile(entry.getKey(), Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(entry.getKey());
		}
		return pattern;
	}

	private static <X extends IPeak> String extractSubstanceName(PeakClassifierFilterSettings configuration, X peak) {

		Set<IIdentificationTarget> targets = peak.getTargets();
		IIdentificationTarget target = IIdentificationTarget.getBestIdentificationTarget(targets);
		ILibraryInformation information = null;
		if(target != null) {
			information = target.getLibraryInformation();
		}
		if(information != null) {
			if(configuration.isIgnoreUppercase()) {
				return information.getName().toLowerCase();
			} else {
				return information.getName();
			}
		} else {
			return "Name not found!";
		}
	}
}