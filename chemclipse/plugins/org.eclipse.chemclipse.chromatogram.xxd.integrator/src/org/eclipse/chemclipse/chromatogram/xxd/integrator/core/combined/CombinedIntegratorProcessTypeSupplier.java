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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.ICombinedIntegrationSettings;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = IProcessTypeSupplier.class)
public class CombinedIntegratorProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Combined Chromatogram and Peak Integrator";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			ICombinedIntegratorSupport support = CombinedIntegrator.getCombinedIntegratorSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableIntegratorIds()) {
				ICombinedIntegratorSupplier supplier = support.getIntegratorSupplier(processorId);
				list.add(new CombinedIntegratorProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoIntegratorAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class CombinedIntegratorProcessorSupplier extends ChromatogramSelectionProcessorSupplier<ICombinedIntegrationSettings> {

		@SuppressWarnings("unchecked")
		public CombinedIntegratorProcessorSupplier(ICombinedIntegratorSupplier supplier, IProcessTypeSupplier parent) {
			super(supplier.getId(), supplier.getIntegratorName(), supplier.getDescription(), (Class<ICombinedIntegrationSettings>)supplier.getSettingsClass(), parent, DataType.MSD, DataType.WSD, DataType.CSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, ICombinedIntegrationSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(processSettings instanceof ICombinedIntegrationSettings) {
				messageConsumer.addMessages(CombinedIntegrator.integrate(chromatogramSelection, processSettings, getId(), monitor));
			} else {
				messageConsumer.addMessages(CombinedIntegrator.integrate(chromatogramSelection, getId(), monitor));
			}
			return chromatogramSelection;
		}
	}
}
