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
 * Lorenz Gerber - more detailed error/exception handling
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupplier;
import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.ChromatogramSelectionProcessorSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramFilterMSDProcessSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Chromatogram Filter";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			IChromatogramFilterSupportMSD support = ChromatogramFilterMSD.getChromatogramFilterSupport();
			List<IProcessSupplier<?>> list = new ArrayList<>();
			for(String processorId : support.getAvailableFilterIds()) {
				list.add(new ChromatogramFilterMSDProcessorSupplier(support.getFilterSupplier(processorId), this));
			}
			return list;
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class ChromatogramFilterMSDProcessorSupplier extends ChromatogramSelectionProcessorSupplier<IChromatogramFilterSettings> {

		private IChromatogramFilterSupplier supplier;

		@SuppressWarnings("unchecked")
		public ChromatogramFilterMSDProcessorSupplier(IChromatogramFilterSupplier supplier, IProcessTypeSupplier parent) {

			super("ChromatogramFilterMSD." + supplier.getId(), supplier.getFilterName(), supplier.getDescription(), (Class<IChromatogramFilterSettings>)supplier.getSettingsClass(), parent, DataType.MSD);
			this.supplier = supplier;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(chromatogramSelection instanceof IChromatogramSelectionMSD chromatogramSelectionMSD) {
				if(processSettings instanceof IChromatogramFilterSettings) {
					messageConsumer.addMessages(ChromatogramFilterMSD.applyFilter(chromatogramSelectionMSD, processSettings, supplier.getId(), monitor));
				} else {
					messageConsumer.addMessages(ChromatogramFilterMSD.applyFilter(chromatogramSelectionMSD, supplier.getId(), monitor));
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
