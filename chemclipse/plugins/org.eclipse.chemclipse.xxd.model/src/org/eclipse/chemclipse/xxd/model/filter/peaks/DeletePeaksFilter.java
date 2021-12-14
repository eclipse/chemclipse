/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - improvement update process
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
import org.eclipse.chemclipse.xxd.model.settings.peaks.DeletePeaksFilterSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class DeletePeaksFilter extends AbstractPeakFilter<DeletePeaksFilterSettings> {

	@Override
	public String getName() {

		return "Delete Peak(s)";
	}

	@Override
	public Class<DeletePeaksFilterSettings> getConfigClass() {

		return DeletePeaksFilterSettings.class;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, DeletePeaksFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> peaks = listener.read();
		/*
		 * Settings
		 */
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		/*
		 * Delete the peaks.
		 */
		if(configuration.isDeletePeaks()) {
			SubMonitor subMonitor = SubMonitor.convert(monitor, peaks.size());
			for(X peak : peaks) {
				if(configuration.isDeleteUnidentifiedOnly()) {
					if(peak.getTargets().isEmpty()) {
						listener.delete(peak);
					}
				} else {
					listener.delete(peak);
				}
				subMonitor.worked(1);
			}
		}
		//
		resetPeakSelection(listener.getDataContainer());
	}

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}
}
