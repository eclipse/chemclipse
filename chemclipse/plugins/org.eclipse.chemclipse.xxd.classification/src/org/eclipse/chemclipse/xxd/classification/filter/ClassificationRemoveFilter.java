/*******************************************************************************
 * Copyright (c) 2021, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.classification.settings.ClassifierRemoveFilterSettings;
import org.eclipse.chemclipse.xxd.model.filter.peaks.AbstractPeakFilter;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class ClassificationRemoveFilter extends AbstractPeakFilter<ClassifierRemoveFilterSettings> {

	@Override
	public String getName() {

		return "Peak Classifier (Remove)";
	}

	@Override
	public String getDescription() {

		return "Removes the given classifier(s) from the peaks.";
	}

	@Override
	public Class<ClassifierRemoveFilterSettings> getConfigClass() {

		return ClassifierRemoveFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, ClassifierRemoveFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		String classification = getClassification(configuration);
		boolean removeCompletely = classification.isEmpty();
		SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
		for(IPeak peak : peaks) {
			/*
			 * Peak Classifier
			 */
			if(removeCompletely) {
				peak.removeClassifier();
			} else {
				List<String> classifiers = new ArrayList<>(peak.getClassifier());
				for(String classifier : classifiers) {
					if(getStringByCaseOption(classifier, configuration).equals(classification)) {
						peak.removeClassifier(classifier);
					}
				}
			}
			subMonitor.worked(1);
		}
	}

	private String getClassification(ClassifierRemoveFilterSettings configuration) {

		return getStringByCaseOption(configuration.getClassification(), configuration);
	}

	private String getStringByCaseOption(String value, ClassifierRemoveFilterSettings configuration) {

		return configuration.isCaseSensitive() ? value : value.toLowerCase();
	}
}