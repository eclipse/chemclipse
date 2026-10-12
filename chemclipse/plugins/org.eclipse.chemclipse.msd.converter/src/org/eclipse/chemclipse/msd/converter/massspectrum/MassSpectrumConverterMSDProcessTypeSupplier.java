/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
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
package org.eclipse.chemclipse.msd.converter.massspectrum;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.converter.core.IConverterSupport;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.supplier.ScanProcessSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class MassSpectrumConverterMSDProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return ICategories.MASS_SPECTRUM_EXPORT;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> list = new ArrayList<>();
		List<ISupplier> suppliers = new ArrayList<>(MassSpectrumConverter.getMassSpectrumConverterSupport().getSupplier(IConverterSupport.EXPORT_SUPPLIER));
		for(ISupplier supplier : suppliers) {
			list.add(new MassSpectrumConverterMSDProcessorSupplier(supplier, this));
		}
		return list;
	}

	private static final class MassSpectrumConverterMSDProcessorSupplier extends ScanProcessSupplier<MassSpectrumExportSettings> {

		private ISupplier supplier;

		public MassSpectrumConverterMSDProcessorSupplier(ISupplier supplier, IProcessTypeSupplier parent) {

			super("msd.massspectrum.export." + supplier.getId(), supplier.getFilterName(), supplier.getDescription(), MassSpectrumExportSettings.class, parent, DataType.MALDI);
			this.supplier = supplier;
		}

		@Override
		public boolean isValidFor(IScan scan) {

			return scan instanceof IScanMSD;
		}

		@Override
		public IScan apply(IScan scan, MassSpectrumExportSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(processSettings == null) {
				processSettings = new MassSpectrumExportSettings();
			}
			File exportFolder = processSettings.getExportFolder();
			if(exportFolder == null) {
				messageConsumer.addErrorMessage(getName(), "No output folder specified and no default configured.");
				return scan;
			}
			if(scan instanceof IScanMSD massSpectrum) {
				File file = processSettings.getExportFile(supplier.getFileExtension(), massSpectrum);
				if(file == null) {
					messageConsumer.addErrorMessage(getName(), "The current directory can't be determined for the given mass spectrum.");
					return scan;
				}
				File folder = file.getParentFile();
				if(folder.exists() || folder.mkdirs()) {
					IProcessingInfo<File> info = MassSpectrumConverter.convert(file, massSpectrum, processSettings.isAppend(), supplier.getId(), monitor);
					messageConsumer.addMessages(info);
					if(info != null && info.getProcessingResult() != null) {
						File result = info.getProcessingResult();
						messageConsumer.addInfoMessage(getName(), "Exported data to " + result.getAbsolutePath());
					}
				} else {
					messageConsumer.addErrorMessage(getName(), "The specified output folder (" + folder.getAbsolutePath() + ") does not exist and can't be created");
				}
			} else {
				messageConsumer.addWarnMessage(getName(), "Only mass spectra supported, skip processing.");
			}
			return scan;
		}
	}
}
