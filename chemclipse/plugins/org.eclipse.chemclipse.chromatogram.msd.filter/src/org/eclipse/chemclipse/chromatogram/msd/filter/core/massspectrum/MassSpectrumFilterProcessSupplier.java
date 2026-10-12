/*******************************************************************************
 * Copyright (c) 2025, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.exceptions.NoMassSpectrumFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.filter.settings.IMassSpectrumFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.supplier.ScanProcessSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = IProcessTypeSupplier.class)
public class MassSpectrumFilterProcessSupplier implements IProcessTypeSupplier {

	private static final Logger logger = Logger.getLogger(MassSpectrumFilterProcessSupplier.class);

	@Override
	public String getCategory() {

		return ICategories.MASS_SPECTRUM_FILTER;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IMassSpectrumFilterSupport support = MassSpectrumFilter.getMassSpectrumFilterSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableFilterIds()) {
				IMassSpectrumFilterSupplier supplier = support.getFilterSupplier(processorId);
				list.add(new MassSpectrumFilterProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoMassSpectrumFilterSupplierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class MassSpectrumFilterProcessorSupplier extends ScanProcessSupplier<IMassSpectrumFilterSettings> {

		@SuppressWarnings("unchecked")
		public MassSpectrumFilterProcessorSupplier(IMassSpectrumFilterSupplier supplier, IProcessTypeSupplier parent) {

			super(supplier.getId(), supplier.getFilterName(), supplier.getDescription(), (Class<IMassSpectrumFilterSettings>)supplier.getSettingsClass(), parent, DataType.MALDI);
			getLiteratureReferences().addAll(supplier.getLiteratureReferences());
			IMassSpectrumFilterSettings massSpectrumFilterSettings = createSettings();
			if(massSpectrumFilterSettings != null) {
				setCategory(massSpectrumFilterSettings.getCategory());
			}
		}

		@Override
		public IScan apply(IScan scan, IMassSpectrumFilterSettings massSpectrumFilterSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(scan instanceof IScanMSD scanMSD) {
				messageConsumer.addMessages(MassSpectrumFilter.applyFilter(scanMSD, massSpectrumFilterSettings, getId(), monitor));
			}
			return scan;
		}

		@Override
		public boolean isValidFor(IScan scan) {

			if(!(scan instanceof IScanMSD)) {
				return false;
			}
			if(scan instanceof IRegularMassSpectrum regularMassSpectrum) {
				IMassSpectrumFilterSettings massSpectrumFilterSettings = createSettings();
				if(massSpectrumFilterSettings != null) {
					return massSpectrumFilterSettings.appliesToMassSpectrumTypes().contains(regularMassSpectrum.getMassSpectrumType());
				}
			}
			return true;
		}

		private IMassSpectrumFilterSettings createSettings() {

			Class<IMassSpectrumFilterSettings> settingsClass = getSettingsClass();
			if(settingsClass != null) {
				try {
					return settingsClass.getDeclaredConstructor().newInstance();
				} catch(InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					logger.error(e);
				}
			}
			return null;
		}
	}
}
