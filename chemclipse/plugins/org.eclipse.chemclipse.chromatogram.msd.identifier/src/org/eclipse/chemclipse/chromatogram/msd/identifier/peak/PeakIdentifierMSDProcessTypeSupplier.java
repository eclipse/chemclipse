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
package org.eclipse.chemclipse.chromatogram.msd.identifier.peak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = IProcessTypeSupplier.class)
public class PeakIdentifierMSDProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Peak Identifier";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IPeakIdentifierSupportMSD support = PeakIdentifierMSD.getPeakIdentifierSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableIdentifierIds()) {
				IPeakIdentifierSupplierMSD supplier = support.getIdentifierSupplier(processorId);
				list.add(new PeakIdentifierProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoIdentifierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class PeakIdentifierProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IPeakIdentifierSettingsMSD> {

		private IPeakIdentifierSupplierMSD supplier;

		@SuppressWarnings("unchecked")
		public PeakIdentifierProcessorSupplier(IPeakIdentifierSupplierMSD supplier, IProcessTypeSupplier parent) {
			super("PeakIdentifierMSD." + supplier.getId(), supplier.getIdentifierName(), supplier.getDescription(), (Class<IPeakIdentifierSettingsMSD>)supplier.getSettingsClass(), parent, DataType.MSD);
			this.supplier = supplier;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IPeakIdentifierSettingsMSD processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
				if(processSettings instanceof IPeakIdentifierSettingsMSD) {
					messageConsumer.addMessages(PeakIdentifierMSD.identify(chromatogramSelectionMSD, processSettings, supplier.getId(), monitor));
				} else {
					messageConsumer.addMessages(PeakIdentifierMSD.identify(chromatogramSelectionMSD, supplier.getId(), monitor));
				}
			} else {
				messageConsumer.addWarnMessage(getName(), "Only MSD chromatogram supported, skipp processing");
			}
			return chromatogramSelection;
		}

		@Override
		public boolean matchesId(String id) {

			return super.matchesId(id) || supplier.getId().equals(id);
		}
	}
}
