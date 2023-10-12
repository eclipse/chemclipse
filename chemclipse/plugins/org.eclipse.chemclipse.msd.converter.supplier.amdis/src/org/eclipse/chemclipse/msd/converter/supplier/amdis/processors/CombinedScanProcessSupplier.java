/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.processors;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.support.CalculationType;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.io.MSLWriter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.settings.CombinedScanSettings;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.support.FilterSupport;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class CombinedScanProcessSupplier implements IProcessTypeSupplier {

	private static final Logger logger = Logger.getLogger(CombinedScanProcessSupplier.class);
	//
	private static final String ID = "org.eclipse.chemclipse.msd.converter.supplier.amdis.processors.combinedScanProcessSupplier";
	private static final String NAME = "Export Chromatogram Combined Scan (*.msl)";
	private static final String DESCRIPTION = "Export a combined scan chromatogram.";

	@Override
	public String getCategory() {

		return ICategories.PROCESS;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<CombinedScanSettings> implements IChromatogramSelectionProcessSupplier<CombinedScanSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, CombinedScanSettings.class, parent, DataCategory.MSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, CombinedScanSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			try {
				if(chromatogramSelection instanceof IChromatogramSelectionMSD chromatogramSelectionMSD) {
					/*
					 * Create the combined scan.
					 */
					CombinedScanSettings settings = (processSettings == null) ? new CombinedScanSettings() : processSettings;
					boolean useNormalize = settings.isUseNormalizedScan();
					CalculationType calculationType = settings.getCalculationType();
					File file = settings.getFileExport();
					boolean append = settings.isAppend();
					boolean usePeaksInsteadOfScans = settings.isUsePeaksInsteadOfScans();
					IScanMSD combinedMassSpectrum = FilterSupport.getCombinedMassSpectrum(chromatogramSelectionMSD, null, useNormalize, calculationType, usePeaksInsteadOfScans);
					/*
					 * Set the chromatogram name and processing comments.
					 */
					StringBuilder builder = new StringBuilder();
					builder.append("Normalize:");
					builder.append(" ");
					builder.append(useNormalize);
					builder.append(", ");
					builder.append("Calculation Type:");
					builder.append(" ");
					builder.append(calculationType.label());
					builder.append(", ");
					builder.append("Use Peaks instead of Scans:");
					builder.append(" ");
					builder.append(usePeaksInsteadOfScans);
					//
					ILibraryInformation libraryInformation = new LibraryInformation();
					libraryInformation.setName(chromatogramSelectionMSD.getChromatogram().getName());
					libraryInformation.setComments(builder.toString());
					IIdentificationTarget identificationTarget = new IdentificationTarget(libraryInformation,ComparisonResult.COMPARISON_RESULT_BEST_MATCH);
					combinedMassSpectrum.getTargets().add(identificationTarget);
					/*
					 * Export as *.msl file.
					 */
					IMassSpectraWriter massSpectraWriter = new MSLWriter();
					massSpectraWriter.write(file, combinedMassSpectrum, append, new NullProgressMonitor());
				}
			} catch(Exception e) {
				logger.warn(e);
				Thread.currentThread().interrupt();
			}
			//
			return chromatogramSelection;
		}
	}
}