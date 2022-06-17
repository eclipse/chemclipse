/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactorings
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.Collection;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.xxd.model.settings.peaks.ClassifiedPeaksFilterSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class ClassifiedPeaksFilter implements IPeakFilter<ClassifiedPeaksFilterSettings> {

	@Override
	public String getName() {

		return "Activate/Deactivate by Classification";
	}

	@Override
	public String getDescription() {

		return "Sets the active for analysis status for peaks with a given classification";
	}

	@Override
	public Class<ClassifiedPeaksFilterSettings> getConfigClass() {

		return ClassifiedPeaksFilterSettings.class;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, ClassifiedPeaksFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> peaks = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		Set<String> classifications = configuration.getClassificationSet();
		boolean activeForAnalysis = configuration.isActiveForAnalysis();
		//
		SubMonitor subMonitor = SubMonitor.convert(monitor, peaks.size());
		for(X peak : peaks) {
			Collection<String> classifier = peak.getClassifier();
			for(String classification : classifications) {
				if(classifier.contains(classification)) {
					peak.setActiveForAnalysis(activeForAnalysis);
					listener.updated(peak);
					break;
				}
			}
			subMonitor.worked(1);
		}
		subMonitor.done();
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}
}