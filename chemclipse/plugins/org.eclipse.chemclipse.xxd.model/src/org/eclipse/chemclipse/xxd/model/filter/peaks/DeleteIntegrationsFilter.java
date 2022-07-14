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
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.model.settings.peaks.DeleteIntegrationsFilterSettings;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class DeleteIntegrationsFilter extends AbstractPeakFilter<DeleteIntegrationsFilterSettings> {

	@Override
	public String getName() {

		return "Delete Integrations";
	}

	@Override
	public String getDescription() {

		return "Filter Peak Integrations";
	}

	@Override
	public Class<DeleteIntegrationsFilterSettings> getConfigClass() {

		return DeleteIntegrationsFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, DeleteIntegrationsFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		if(configuration.isDeleteIntegrations()) {
			SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
			for(IPeak peak : peaks) {
				peak.removeAllIntegrationEntries();
				subMonitor.worked(1);
			}
		}
	}
}