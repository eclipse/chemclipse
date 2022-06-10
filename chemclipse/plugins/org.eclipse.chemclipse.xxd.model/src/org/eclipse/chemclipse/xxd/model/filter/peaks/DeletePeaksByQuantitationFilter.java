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
 * Philip Wenig - implementation of a peak/target filter
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.xxd.model.settings.peaks.DeletePeaksByQuantitationFilterSettings;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class DeletePeaksByQuantitationFilter extends AbstractPeakFilter<DeletePeaksByQuantitationFilterSettings> {

	@Override
	public String getName() {

		return "Delete Peaks by Quantitation";
	}

	@Override
	public Class<DeletePeaksByQuantitationFilterSettings> getConfigClass() {

		return DeletePeaksByQuantitationFilterSettings.class;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, DeletePeaksByQuantitationFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> peaks = listener.read();
		//
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		//
		if(isConfigurationValid(configuration)) {
			SubMonitor subMonitor = SubMonitor.convert(monitor, peaks.size());
			for(X peak : peaks) {
				if(isDeletePeak(peak, configuration)) {
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

	public static boolean isDeletePeak(IPeak peak, DeletePeaksByQuantitationFilterSettings configuration) {

		if(peak != null) {
			if(isConfigurationValid(configuration)) {
				/*
				 * Setting
				 */
				String name = configuration.getName();
				double concentration = configuration.getConcentration();
				String unit = configuration.getUnit();
				//
				for(IQuantitationEntry quantitationEntry : peak.getQuantitationEntries()) {
					if(name.isEmpty() || quantitationEntry.getName().equals(name)) {
						if(quantitationEntry.getConcentrationUnit().equals(unit)) {
							if(quantitationEntry.getConcentration() < concentration) {
								return true;
							}
						}
					}
				}
			}
		}
		//
		return false;
	}

	private static boolean isConfigurationValid(DeletePeaksByQuantitationFilterSettings configuration) {

		if(configuration != null) {
			String name = configuration.getName();
			if(name != null) { // Name could be empty
				double concentration = configuration.getConcentration();
				if(concentration >= 0.0d) {
					String unit = configuration.getUnit();
					if(unit != null && !unit.isEmpty()) { // Unit must be not empty
						return true;
					}
				}
			}
		}
		//
		return false;
	}
}