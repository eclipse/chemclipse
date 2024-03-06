/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - enable ISD
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.IPeakIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = IProcessTypeSupplier.class)
public class PeakIntegratorProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return ICategories.PEAK_INTEGRATOR;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IPeakIntegratorSupport support = PeakIntegrator.getPeakIntegratorSupport();
			List<IProcessSupplier<?>> list = new ArrayList<IProcessSupplier<?>>();
			for(String processorId : support.getAvailableIntegratorIds()) {
				IPeakIntegratorSupplier supplier = support.getIntegratorSupplier(processorId);
				list.add(new PeakIntegratorProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoIntegratorAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class PeakIntegratorProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IPeakIntegrationSettings> {

		@SuppressWarnings("unchecked")
		public PeakIntegratorProcessorSupplier(IPeakIntegratorSupplier supplier, IProcessTypeSupplier parent) {

			super(supplier.getId(), supplier.getIntegratorName(), supplier.getDescription(), (Class<IPeakIntegrationSettings>)supplier.getSettingsClass(), parent, DataType.MSD, DataType.CSD, DataType.WSD, DataType.VSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IPeakIntegrationSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(processSettings instanceof IPeakIntegrationSettings) {
				messageConsumer.addMessages(PeakIntegrator.integrate(chromatogramSelection, processSettings, getId(), monitor));
			} else {
				messageConsumer.addMessages(PeakIntegrator.integrate(chromatogramSelection, getId(), monitor));
			}
			return chromatogramSelection;
		}
	}
}