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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.chromatogram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.chromatogram.IChromatogramIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramIntegratorProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Chromatogram Integrator";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			List<IProcessSupplier<?>> list = new ArrayList<>();
			IChromatogramIntegratorSupport support = ChromatogramIntegrator.getChromatogramIntegratorSupport();
			for(String processorId : support.getAvailableIntegratorIds()) {
				IChromatogramIntegratorSupplier supplier = support.getIntegratorSupplier(processorId);
				list.add(new ChromatogramIntegratorProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoIntegratorAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class ChromatogramIntegratorProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IChromatogramIntegrationSettings> {

		@SuppressWarnings("unchecked")
		public ChromatogramIntegratorProcessorSupplier(IChromatogramIntegratorSupplier supplier, IProcessTypeSupplier parent) {
			super(supplier.getId(), supplier.getIntegratorName(), supplier.getDescription(), (Class<IChromatogramIntegrationSettings>)supplier.getSettingsClass(), parent, DataType.MSD, DataType.CSD, DataType.WSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramIntegrationSettings processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(processSettings instanceof IChromatogramIntegrationSettings) {
				messageConsumer.addMessages(ChromatogramIntegrator.integrate(chromatogramSelection, processSettings, getId(), monitor));
			} else {
				messageConsumer.addMessages(ChromatogramIntegrator.integrate(chromatogramSelection, getId(), monitor));
			}
			return chromatogramSelection;
		}
	}
}
