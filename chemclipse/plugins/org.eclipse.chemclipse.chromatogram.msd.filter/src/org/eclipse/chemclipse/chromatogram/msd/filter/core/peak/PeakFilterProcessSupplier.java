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
package org.eclipse.chemclipse.chromatogram.msd.filter.core.peak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoPeakFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = IProcessTypeSupplier.class)
public class PeakFilterProcessSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Peak Filter";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IPeakFilterSupport support = PeakFilter.getPeakFilterSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableFilterIds()) {
				IPeakFilterSupplier supplier = support.getFilterSupplier(processorId);
				list.add(new PeakFilterProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoPeakFilterSupplierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class PeakFilterProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IPeakFilterSettings> {

		@SuppressWarnings("unchecked")
		public PeakFilterProcessorSupplier(IPeakFilterSupplier supplier, IProcessTypeSupplier parent) {
			super(supplier.getId(), supplier.getFilterName(), supplier.getDescription(), (Class<IPeakFilterSettings>)supplier.getSettingsClass(), parent, DataType.MSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IPeakFilterSettings processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
				if(processSettings instanceof IPeakFilterSettings) {
					messageConsumer.addMessages(PeakFilter.applyFilter(chromatogramSelectionMSD, processSettings, getId(), monitor));
				} else {
					messageConsumer.addMessages(PeakFilter.applyFilter(chromatogramSelectionMSD, getId(), monitor));
				}
			} else {
				messageConsumer.addWarnMessage(getName(), "Only MSD chromatograms supported, processing skipped");
			}
			return chromatogramSelection;
		}
	}
}
