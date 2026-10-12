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
package org.eclipse.chemclipse.msd.report.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.settings.AbstractProcessSettings;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.supplier.ScanProcessSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IStandaloneMassSpectrum;
import org.eclipse.chemclipse.msd.report.exceptions.NoReportSupplierAvailableException;
import org.eclipse.chemclipse.msd.report.settings.DefaultMassSpectraReportSettings;
import org.eclipse.chemclipse.msd.report.settings.IMassSpectraReportSettings;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class MassSpectraReportsProcessSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return ICategories.MASS_SPECTRUM_REPORTS;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		try {
			List<IProcessSupplier<?>> list = new ArrayList<>();
			IMassSpectraReportSupport support = MassSpectraReports.getMassSpectraReportSupplierSupport();
			for(String processorId : support.getAvailableProcessorIds()) {
				IMassSpectraReportSupplier supplier = support.getReportSupplier(processorId);
				list.add(new MassSpectraReportProcessorSupplier(supplier, this));
			}
			return list;
		} catch(NoReportSupplierAvailableException e) {
			return Collections.emptyList();
		}
	}

	private static final class MassSpectraReportProcessorSupplier extends ScanProcessSupplier<IMassSpectraReportSettings> {

		private static final String ILLEGAL_CHARACTERS = "[\\\\/:*?\"<>|]";
		private IMassSpectraReportSupplier supplier;

		@SuppressWarnings("unchecked")
		public MassSpectraReportProcessorSupplier(IMassSpectraReportSupplier supplier, IProcessTypeSupplier parent) {

			super(supplier.getId(), supplier.getReportName(), supplier.getDescription(), (Class<IMassSpectraReportSettings>)supplier.getSettingsClass(), parent, DataType.MSD, DataType.MALDI);
			this.supplier = supplier;
		}

		@Override
		public boolean isValidFor(IScan scan) {

			return scan instanceof IScanMSD;
		}

		@Override
		public IScan apply(IScan scan, IMassSpectraReportSettings processSettings, IMessageConsumer messageConsumer, IProgressMonitor monitor) {

			if(!(scan instanceof IScanMSD massSpectrum)) {
				return scan;
			}
			/*
			 * Settings
			 */
			IMassSpectraReportSettings settings = processSettings != null ? processSettings : new DefaultMassSpectraReportSettings();
			/*
			 * Validate the export folder.
			 */
			File exportFolder = settings.getExportFolder();
			if(exportFolder == null) {
				messageConsumer.addErrorMessage(getName(), "No output folder specified and no default configured.");
				return scan;
			}
			/*
			 * Validate the default directory.
			 */
			if(exportFolder.getAbsolutePath().contains(IProcessSettings.VARIABLE_CURRENT_DIRECTORY)) {
				File currentDirectory = getCurrentDirectory(massSpectrum);
				if(currentDirectory == null) {
					messageConsumer.addErrorMessage(getName(), "The current directory can't be determined for the given mass spectrum.");
					return scan;
				}
				String exportPath = AbstractProcessSettings.getCleanedFileValue(exportFolder.getAbsolutePath());
				exportFolder = new File(exportPath.replace(IProcessSettings.VARIABLE_CURRENT_DIRECTORY, currentDirectory.getAbsolutePath()));
			}
			/*
			 * Create the report file and run the report.
			 */
			if(exportFolder.exists() || exportFolder.mkdirs()) {
				File file = new File(exportFolder, getFileName(massSpectrum, settings));
				IProcessingInfo<?> info = MassSpectraReports.generate(file, settings.isAppend(), massSpectrum, settings, getId(), monitor);
				messageConsumer.addMessages(info);
				messageConsumer.addInfoMessage(getName(), "Report written to " + file.getAbsolutePath());
			} else {
				messageConsumer.addErrorMessage(getName(), "The specified output folder does not exist and can't be created.");
			}
			return scan;
		}

		private String getFileName(IScanMSD massSpectrum, IMassSpectraReportSettings settings) {

			String name = null;
			if(massSpectrum instanceof IStandaloneMassSpectrum standaloneMassSpectrum) {
				name = standaloneMassSpectrum.getName();
			}
			if(name == null || name.isEmpty()) {
				name = massSpectrum.getIdentifier();
			}
			if(name == null || name.isEmpty()) {
				name = supplier.getFileName();
			}
			String fileName = settings.getFileNamePattern();
			fileName = fileName.replace(IMassSpectraReportSettings.VARIABLE_MASSSPECTRUM_NAME, name);
			fileName = fileName.replace(IProcessSettings.VARIABLE_EXTENSION, supplier.getFileExtension());
			return fileName.replaceAll(ILLEGAL_CHARACTERS, "_");
		}

		private File getCurrentDirectory(IScanMSD massSpectrum) {

			if(massSpectrum instanceof IStandaloneMassSpectrum standaloneMassSpectrum && standaloneMassSpectrum.getFile() != null) {
				return standaloneMassSpectrum.getFile().getParentFile();
			}
			IChromatogram chromatogram = massSpectrum.getParentChromatogram();
			if(chromatogram != null && chromatogram.getFile() != null) {
				return chromatogram.getFile().getParentFile();
			}
			return null;
		}
	}
}
