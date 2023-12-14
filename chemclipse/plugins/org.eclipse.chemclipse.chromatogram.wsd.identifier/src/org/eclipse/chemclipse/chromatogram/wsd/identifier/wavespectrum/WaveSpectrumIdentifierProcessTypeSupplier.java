/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.identifier.wavespectrum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.wsd.identifier.settings.IWaveSpectrumIdentifierSettings;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = IProcessTypeSupplier.class)
public class WaveSpectrumIdentifierProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return ICategories.SCAN_IDENTIFIER;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IWaveSpectrumIdentifierSupport support = WaveSpectrumIdentifier.getWaveSpectrumIdentifierSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableIdentifierIds()) {
				IWaveSpectrumIdentifierSupplier supplier = support.getIdentifierSupplier(processorId);
				list.add(new WaveSpectrumIdentifierProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoIdentifierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class WaveSpectrumIdentifierProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IWaveSpectrumIdentifierSettings> {

		private IWaveSpectrumIdentifierSupplier supplier;

		@SuppressWarnings("unchecked")
		public WaveSpectrumIdentifierProcessorSupplier(IWaveSpectrumIdentifierSupplier supplier, IProcessTypeSupplier parent) {

			super("WaveSpectrumIdentifier." + supplier.getId(), supplier.getIdentifierName(), supplier.getDescription(), (Class<IWaveSpectrumIdentifierSettings>)supplier.getSettingsClass(), parent, DataType.WSD);
			this.supplier = supplier;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IWaveSpectrumIdentifierSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(chromatogramSelection instanceof IChromatogramSelectionWSD chromatogramSelectionWSD) {
				if(processSettings instanceof IWaveSpectrumIdentifierSettings) {
					messageConsumer.addMessages(WaveSpectrumIdentifier.identify(chromatogramSelectionWSD.getSelectedScan(), processSettings, supplier.getId(), monitor));
				} else {
					messageConsumer.addMessages(WaveSpectrumIdentifier.identify(chromatogramSelectionWSD.getSelectedScan(), supplier.getId(), monitor));
				}
			} else {
				messageConsumer.addWarnMessage(getName(), "Only WSD chromatogram supported, skipp processing");
			}
			return chromatogramSelection;
		}

		@Override
		public boolean matchesId(String id) {

			return super.matchesId(id) || supplier.getId().equals(id);
		}
	}
}
