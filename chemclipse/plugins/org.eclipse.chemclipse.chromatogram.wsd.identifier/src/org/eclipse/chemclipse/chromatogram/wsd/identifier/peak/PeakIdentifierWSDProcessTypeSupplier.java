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
 * Philip Wenig - added support for WSD
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.identifier.peak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.wsd.identifier.settings.IPeakIdentifierSettingsWSD;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = IProcessTypeSupplier.class)
public class PeakIdentifierWSDProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Peak Identifier";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IPeakIdentifierSupportWSD support = PeakIdentifierWSD.getPeakIdentifierSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableIdentifierIds()) {
				IPeakIdentifierSupplierWSD supplier = support.getIdentifierSupplier(processorId);
				list.add(new PeakIdentifierProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoIdentifierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class PeakIdentifierProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IPeakIdentifierSettingsWSD> {

		private IPeakIdentifierSupplierWSD supplier;

		@SuppressWarnings("unchecked")
		public PeakIdentifierProcessorSupplier(IPeakIdentifierSupplierWSD supplier, IProcessTypeSupplier parent) {

			super("PeakIdentifierWSD." + supplier.getId(), supplier.getIdentifierName(), supplier.getDescription(), (Class<IPeakIdentifierSettingsWSD>)supplier.getSettingsClass(), parent, DataType.WSD);
			this.supplier = supplier;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IPeakIdentifierSettingsWSD processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(chromatogramSelection instanceof IChromatogramSelectionWSD chromatogramSelectionWSD) {
				if(processSettings instanceof IPeakIdentifierSettingsWSD) {
					messageConsumer.addMessages(PeakIdentifierWSD.identify(chromatogramSelectionWSD, processSettings, supplier.getId(), monitor));
				} else {
					messageConsumer.addMessages(PeakIdentifierWSD.identify(chromatogramSelectionWSD, supplier.getId(), monitor));
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
