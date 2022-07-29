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
package org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
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
public class MassSpectrumIdentifierProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Scan Identifier";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IMassSpectrumIdentifierSupport support = MassSpectrumIdentifier.getMassSpectrumIdentifierSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableIdentifierIds()) {
				IMassSpectrumIdentifierSupplier supplier = support.getIdentifierSupplier(processorId);
				list.add(new MassSpectrumIdentifierProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoIdentifierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class MassSpectrumIdentifierProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IMassSpectrumIdentifierSettings> {

		private IMassSpectrumIdentifierSupplier supplier;

		@SuppressWarnings("unchecked")
		public MassSpectrumIdentifierProcessorSupplier(IMassSpectrumIdentifierSupplier supplier, IProcessTypeSupplier parent) {

			super("MassSpectrumIdentifier." + supplier.getId(), supplier.getIdentifierName(), supplier.getDescription(), (Class<IMassSpectrumIdentifierSettings>)supplier.getSettingsClass(), parent, DataType.MSD);
			this.supplier = supplier;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IMassSpectrumIdentifierSettings processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(chromatogramSelection instanceof IChromatogramSelectionMSD chromatogramSelectionMSD) {
				if(processSettings instanceof IMassSpectrumIdentifierSettings) {
					messageConsumer.addMessages(MassSpectrumIdentifier.identify(chromatogramSelectionMSD.getSelectedScan(), processSettings, supplier.getId(), monitor));
				} else {
					messageConsumer.addMessages(MassSpectrumIdentifier.identify(chromatogramSelectionMSD.getSelectedScan(), supplier.getId(), monitor));
				}
			} else {
				messageConsumer.addWarnMessage(getName(), "Only MSD chromatogram supported, skipp processing");
			}
			chromatogramSelection.getChromatogram().setDirty(true);
			return chromatogramSelection;
		}

		@Override
		public boolean matchesId(String id) {

			return super.matchesId(id) || supplier.getId().equals(id);
		}
	}
}
