/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.Collection;
import java.util.Set;

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

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class DisableClassifiedPeaksFilter implements IPeakFilter<DisableClassifiedPeaksFilterConfig> {

	@Override
	public String getName() {

		return "Disable classified peaks";
	}

	@Override
	public String getDescription() {

		return "Disables peaks with a given classification";
	}

	@Override
	public Class<DisableClassifiedPeaksFilterConfig> getConfigClass() {

		return DisableClassifiedPeaksFilterConfig.class;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, DisableClassifiedPeaksFilterConfig configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> read = listener.read();
		if(configuration == null) {
			configuration = createConfiguration(read);
		}
		Set<String> classifications = configuration.getClassificationSet();
		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());
		for(X peak : read) {
			Set<IIdentificationTarget> targets = peak.getTargets();
			IIdentificationTarget target = IIdentificationTarget.getBestIdentificationTarget(targets);
			if(target != null) {
				ILibraryInformation information = target.getLibraryInformation();
				if(information != null) {
					boolean disable;
					if(classifications.isEmpty()) {
						disable = information.getClassifier().size() > 0;
					} else {
						disable = false;
						for(String classifier : information.getClassifier()) {
							if(classifications.contains(classifier)) {
								disable = true;
								break;
							}
						}
					}
					if(disable) {
						peak.setActiveForAnalysis(false);
						listener.updated(peak);
					}
				}
			}
			subMonitor.worked(1);
		}
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}
}
