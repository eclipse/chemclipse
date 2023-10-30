/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.DeleteStandardsFilterSettings;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class DeleteStandardsFilter extends AbstractPeakFilter<DeleteStandardsFilterSettings> {

	@Override
	public String getName() {

		return "Delete Standards";
	}

	@Override
	public String getDescription() {

		return "Filter Peak Standards";
	}

	@Override
	public Class<DeleteStandardsFilterSettings> getConfigClass() {

		return DeleteStandardsFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, DeleteStandardsFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		if(configuration.isDeleteStandards()) {
			SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
			for(IPeak peak : peaks) {
				peak.removeInternalStandards();
				subMonitor.worked(1);
			}
		}
	}
}