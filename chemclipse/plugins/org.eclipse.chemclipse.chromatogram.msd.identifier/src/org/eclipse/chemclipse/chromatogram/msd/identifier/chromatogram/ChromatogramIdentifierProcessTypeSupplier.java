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
package org.eclipse.chemclipse.chromatogram.msd.identifier.chromatogram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IChromatogramIdentifierSettings;
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
public class ChromatogramIdentifierProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Chromatogram Identifier";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			List<IProcessSupplier<?>> list = new ArrayList<>();
			IChromatogramIdentifierSupport support = ChromatogramIdentifier.getChromatogramIdentifierSupport();
			for(String processorId : support.getAvailableIdentifierIds()) {
				IChromatogramIdentifierSupplier supplier = support.getIdentifierSupplier(processorId);
				list.add(new ChromatogramIdentifierProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoIdentifierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class ChromatogramIdentifierProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IChromatogramIdentifierSettings> {

		@SuppressWarnings("unchecked")
		public ChromatogramIdentifierProcessorSupplier(IChromatogramIdentifierSupplier supplier, IProcessTypeSupplier parent) {
			super(supplier.getId(), supplier.getIdentifierName(), supplier.getDescription(), (Class<IChromatogramIdentifierSettings>)supplier.getSettingsClass(), parent, DataType.MSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramIdentifierSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
				if(processSettings == null) {
					messageConsumer.addMessages(ChromatogramIdentifier.identify(chromatogramSelectionMSD, getId(), monitor));
				} else {
					messageConsumer.addMessages(ChromatogramIdentifier.identify(chromatogramSelectionMSD, processSettings, getId(), monitor));
				}
			} else {
				messageConsumer.addWarnMessage(getName(), "Only MSD chromatograms are supported, processor was skipped");
			}
			return chromatogramSelection;
		}
	}
}
