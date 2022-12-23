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
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.quantitation.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.quantitation.exceptions.NoPeakQuantifierAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.quantitation.settings.IPeakQuantifierSettings;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = IProcessTypeSupplier.class)
public class PeakQuantifierProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Peak Quantifier";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IPeakQuantifierSupport support = PeakQuantifier.getPeakQuantifierSupport();
			List<IProcessSupplier<?>> list = new ArrayList<IProcessSupplier<?>>();
			for(String processorId : support.getAvailablePeakQuantifierIds()) {
				IPeakQuantifierSupplier supplier = support.getPeakQuantifierSupplier(processorId);
				list.add(new PeakQuantifierProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoPeakQuantifierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class PeakQuantifierProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IPeakQuantifierSettings> {

		@SuppressWarnings("unchecked")
		public PeakQuantifierProcessorSupplier(IPeakQuantifierSupplier supplier, IProcessTypeSupplier parent) {
			super(supplier.getId(), supplier.getPeakQuantifierName(), supplier.getDescription(), (Class<IPeakQuantifierSettings>)supplier.getSettingsClass(), parent, DataType.MSD, DataType.CSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IPeakQuantifierSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			@SuppressWarnings("unchecked")
			List<IPeak> peaks = (List<IPeak>)chromatogramSelection.getChromatogram().getPeaks();
			if(processSettings instanceof IPeakQuantifierSettings) {
				messageConsumer.addMessages(PeakQuantifier.quantify(peaks, processSettings, getId(), monitor));
			} else {
				messageConsumer.addMessages(PeakQuantifier.quantify(peaks, getId(), monitor));
			}
			return chromatogramSelection;
		}
	}
}
