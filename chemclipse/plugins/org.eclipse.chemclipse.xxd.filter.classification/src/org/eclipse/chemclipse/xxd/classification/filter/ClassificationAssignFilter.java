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
package org.eclipse.chemclipse.xxd.classification.filter;

import java.util.Collection;
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
import org.eclipse.chemclipse.xxd.classification.model.ClassificationRule;
import org.eclipse.chemclipse.xxd.classification.settings.ClassifierAssignFilterSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class ClassificationAssignFilter implements IPeakFilter<ClassifierAssignFilterSettings> {

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
		for(X peak : peaks) {
			setPeakClassifier(configuration, peak);
			subMonitor.worked(1);
		}
	}

	private static <X extends IPeak> void setPeakClassifier(ClassifierAssignFilterSettings configuration, X peak) {

		peak.removeClassifier("");
		boolean useRegularExpression = configuration.isUseRegularExpression();
		//
		for(IIdentificationTarget identificationTarget : peak.getTargets()) {
			String substanceName = extractSubstanceName(configuration, identificationTarget);
			if(substanceName != null) {
				for(ClassificationRule rule : configuration.getClassificationDictionary()) {
					if(useRegularExpression) {
						matchByRegularExpression(peak, substanceName, configuration, rule);
					} else {
						matchByString(peak, substanceName, configuration, rule);
					}
				}
			}
		}
	}

	private static <X extends IPeak> void matchByString(X peak, String substanceName, ClassifierAssignFilterSettings configuration, ClassificationRule rule) {

		if(configuration.isMatchPartly()) {
			if(substanceName.contains(rule.getSearchExpression())) {
				peak.addClassifier(rule.getClassification());
			}
		} else {
			if(substanceName.equals(rule.getSearchExpression())) {
				peak.addClassifier(rule.getClassification());
			}
		}
	}

	private static <X extends IPeak> void matchByRegularExpression(X peak, String substanceName, ClassifierAssignFilterSettings configuration, ClassificationRule rule) {

		Pattern pattern = createPattern(configuration, rule);
		Matcher matcher = pattern.matcher(substanceName);
		if(configuration.isMatchPartly()) {
			if(matcher.find()) {
				peak.addClassifier(rule.getClassification());
			}
		} else {
			if(matcher.matches()) {
				peak.addClassifier(rule.getClassification());
			}
		}
	}

	private static Pattern createPattern(ClassifierAssignFilterSettings configuration, ClassificationRule rule) {

		Pattern pattern = null;
		if(configuration.isCaseSensitive()) {
			pattern = Pattern.compile(rule.getSearchExpression());
		} else {
			pattern = Pattern.compile(rule.getSearchExpression(), Pattern.CASE_INSENSITIVE);
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