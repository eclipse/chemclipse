/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
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
import org.eclipse.chemclipse.xxd.filter.peaks.settings.DeleteQuantitationReferencesFilterSettings;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class DeleteQuantitationReferencesFilter extends AbstractPeakFilter<DeleteQuantitationReferencesFilterSettings> {

	@Override
	public String getName() {

		return "Delete Quantitation References";
	}

	@Override
	public String getDescription() {

		return "Delete the peak quantitation references.";
	}

	@Override
	public Class<DeleteQuantitationReferencesFilterSettings> getConfigClass() {

		return DeleteQuantitationReferencesFilterSettings.class;
	}

	@Override
	public void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, DeleteQuantitationReferencesFilterSettings configuration, ProcessExecutionContext context) throws IllegalArgumentException {

		Collection<IPeak> peaks = getReadOnlyPeaks(chromatogramSelection);
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		String quantitationReference = configuration.getQuantitationReference();
		boolean deleteReferencesCompletely = quantitationReference == null || quantitationReference.isEmpty() || quantitationReference.isBlank();
		//
		SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
		for(IPeak peak : peaks) {
			if(deleteReferencesCompletely) {
				peak.removeQuantitationReferences();
			} else {
				peak.removeQuantitationReference(quantitationReference);
			}
			subMonitor.worked(1);
		}
	}
}