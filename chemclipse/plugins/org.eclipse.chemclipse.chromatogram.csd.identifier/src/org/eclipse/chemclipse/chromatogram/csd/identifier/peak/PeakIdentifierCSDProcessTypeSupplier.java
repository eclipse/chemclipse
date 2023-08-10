/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactor menu categories
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.identifier.peak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.csd.identifier.l10n.Messages;
import org.eclipse.chemclipse.chromatogram.csd.identifier.settings.IPeakIdentifierSettingsCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
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
public class PeakIdentifierCSDProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return ICategories.PEAK_IDENTIFIER;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IPeakIdentifierSupportCSD support = PeakIdentifierCSD.getPeakIdentifierSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableIdentifierIds()) {
				IPeakIdentifierSupplierCSD supplier = support.getIdentifierSupplier(processorId);
				list.add(new PeakIdentifierProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoIdentifierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class PeakIdentifierProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IPeakIdentifierSettingsCSD> {

		private IPeakIdentifierSupplierCSD supplier;

		@SuppressWarnings("unchecked")
		public PeakIdentifierProcessorSupplier(IPeakIdentifierSupplierCSD supplier, IProcessTypeSupplier parent) {

			super("PeakIdentifierCSD." + supplier.getId(), supplier.getIdentifierName(), supplier.getDescription(), (Class<IPeakIdentifierSettingsCSD>)supplier.getSettingsClass(), parent, DataType.CSD); //$NON-NLS-1$
			this.supplier = supplier;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IPeakIdentifierSettingsCSD processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(chromatogramSelection instanceof IChromatogramSelectionCSD chromatogramSelectionCSD) {
				if(processSettings instanceof IPeakIdentifierSettingsCSD) {
					messageConsumer.addMessages(PeakIdentifierCSD.identify(chromatogramSelectionCSD, processSettings, supplier.getId(), monitor));
				} else {
					messageConsumer.addMessages(PeakIdentifierCSD.identify(chromatogramSelectionCSD, supplier.getId(), monitor));
				}
			} else {
				messageConsumer.addWarnMessage(getName(), Messages.onlyCSDchromatogramSupported);
			}
			return chromatogramSelection;
		}

		@Override
		public boolean matchesId(String id) {

			return super.matchesId(id) || supplier.getId().equals(id);
		}
	}
}
