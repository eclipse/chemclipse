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
 * Philip Wenig - improvement update process
 * Matthias Mailänder - undoable
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.model.settings.peaks.DeletePeaksFilterSettings;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class DeletePeaksFilter extends AbstractPeakFilter<DeletePeaksFilterSettings> {

	@Override
	public String getName() {

		return "Delete Peaks";
	}

	@Override
	public Class<DeletePeaksFilterSettings> getConfigClass() {

		return DeletePeaksFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, DeletePeaksFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
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
			SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
			List<IPeak> peaksToDelete = new ArrayList<>();
			for(IPeak peak : peaks) {
				if(configuration.isDeleteUnidentifiedOnly()) {
					if(peak.getTargets().isEmpty()) {
						peaksToDelete.add(peak);
					}
				} else {
					peaksToDelete.add(peak);
				}
				subMonitor.worked(1);
			}
			deletePeaks(peaksToDelete, chromatogramSelection);
			resetPeakSelection(chromatogramSelection);
		}
	}
}
