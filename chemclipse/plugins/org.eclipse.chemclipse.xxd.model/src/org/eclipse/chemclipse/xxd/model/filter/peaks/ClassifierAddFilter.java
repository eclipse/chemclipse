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
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.xxd.model.settings.peaks.ClassifierAddFilterSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class ClassifierAddFilter implements IPeakFilter<ClassifierAddFilterSettings> {

	@Override
	public String getName() {

		return "Peak Classifier (Add)";
	}

	@Override
	public String getDescription() {

		return "Adds the given classifier.";
	}

	@Override
	public Class<ClassifierAddFilterSettings> getConfigClass() {

		return ClassifierAddFilterSettings.class;
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, ClassifierAddFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> peaks = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		String classification = configuration.getClassification();
		boolean skipClassifiedPeak = configuration.isSkipClassifiedPeak();
		SubMonitor subMonitor = SubMonitor.convert(monitor, peaks.size());
		for(X peak : peaks) {
			/*
			 * Peak Classifier
			 */
			boolean classify = true;
			if(!peak.getClassifier().isEmpty()) {
				if(skipClassifiedPeak) {
					classify = false;
				}
			}
			//
			if(classify) {
				peak.addClassifier(classification);
			}
			//
			subMonitor.worked(1);
		}
	}
}
