/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.peak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class PeakConverterMSDProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return "Peak Export";
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> list = new ArrayList<>();
		List<ISupplier> exportSupplier = PeakConverterMSD.getPeakConverterSupport().getExportSupplier();
		for(ISupplier supplier : exportSupplier) {
			IPeakExportConverter converter = PeakConverterMSD.getPeakExportConverter(supplier.getId());
			if(converter != null) {
				list.add(new PeakConverterMSDProcessSupplier(supplier, converter, this));
			}
		}
		return list;
	}

	private static final class PeakConverterMSDProcessSupplier extends AbstractProcessSupplier<PeakExportSettings> implements IChromatogramSelectionProcessSupplier<PeakExportSettings> {

		private final IPeakExportConverter converter;
		private final ISupplier supplier;

		public PeakConverterMSDProcessSupplier(ISupplier supplier, IPeakExportConverter converter, IProcessTypeSupplier parent) {
			super("PeakConverterMSD." + supplier.getId(), supplier.getFilterName(), supplier.getDescription(), PeakExportSettings.class, parent, DataCategory.MSD);
			this.supplier = supplier;
			this.converter = converter;
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, PeakExportSettings processSettings, ProcessExecutionContext context) {

			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD) {
				IChromatogramMSD msd = (IChromatogramMSD)chromatogram;
				IProcessingInfo<?> info = converter.convert(processSettings.getExportFileName(supplier.getFileExtension(), chromatogram), msd.toPeaks(msd.getName(), chromatogramSelection), false, context.getProgressMonitor());
				context.addMessages(info);
			} else {
				context.addWarnMessage(getName(), "Can only export MSD Data, skipping...");
			}
			return chromatogramSelection;
		}
	}
}
