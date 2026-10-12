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
package org.eclipse.chemclipse.msd.identifier;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.supplier.ScanProcessSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.MassSpectrumType;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = IProcessTypeSupplier.class)
public class StandaloneMassSpectrumIdentifierProcessTypeSupplier implements IProcessTypeSupplier {

	private static final Logger logger = Logger.getLogger(StandaloneMassSpectrumIdentifierProcessTypeSupplier.class);

	@Override
	public String getCategory() {

		return ICategories.MASS_SPECTRUM_IDENTIFIER;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IMassSpectrumIdentifierSupport support = MassSpectrumIdentifier.getMassSpectrumIdentifierSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableIdentifierIds()) {
				IMassSpectrumIdentifierSupplier supplier = support.getIdentifierSupplier(processorId);
				list.add(new StandaloneMassSpectrumIdentifierProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoIdentifierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class StandaloneMassSpectrumIdentifierProcessorSupplier extends ScanProcessSupplier<IMassSpectrumIdentifierSettings> {

		private IMassSpectrumIdentifierSupplier supplier;

		@SuppressWarnings("unchecked")
		public StandaloneMassSpectrumIdentifierProcessorSupplier(IMassSpectrumIdentifierSupplier supplier, IProcessTypeSupplier parent) {

			super("ScanMassSpectrumIdentifier." + supplier.getId(), supplier.getIdentifierName(), supplier.getDescription(), (Class<IMassSpectrumIdentifierSettings>)supplier.getSettingsClass(), parent, DataType.MALDI);
			this.supplier = supplier;
			getLiteratureReferences().addAll(supplier.getLiteratureReferences());
		}

		@Override
		public IScan apply(IScan scan, IMassSpectrumIdentifierSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(scan instanceof IScanMSD scanMSD) {
				if(processSettings instanceof IMassSpectrumIdentifierSettings) {
					messageConsumer.addMessages(MassSpectrumIdentifier.identify(scanMSD, processSettings, supplier.getId(), monitor));
				} else {
					messageConsumer.addMessages(MassSpectrumIdentifier.identify(scanMSD, supplier.getId(), monitor));
				}
				scan.setDirty(true);
			}
			return scan;
		}

		@Override
		public boolean matchesId(String id) {

			return super.matchesId(id) || supplier.getId().equals(id);
		}

		@Override
		public boolean isValidFor(IScan scan) {

			if(scan instanceof IRegularMassSpectrum regularMassSpectrum) {
				MassSpectrumType massSpectrumType = regularMassSpectrum.getMassSpectrumType();
				IMassSpectrumIdentifierSettings massSpectrumIdentifierSettings = createSettings();
				if(massSpectrumIdentifierSettings != null) {
					return massSpectrumIdentifierSettings.appliesToMassSpectrumTypes().contains(massSpectrumType);
				}
				return MassSpectrumType.CENTROID.equals(massSpectrumType);
			}
			return false;
		}

		private IMassSpectrumIdentifierSettings createSettings() {

			Class<IMassSpectrumIdentifierSettings> settingsClass = getSettingsClass();
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