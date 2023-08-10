/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.core;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.RetentionIndexCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.RetentionIndexExtractor;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl.RetentionIndexSupport;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexMarker;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.IndexCuratorSettings;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.osgi.service.component.annotations.Component;

@Component(service = {IProcessTypeSupplier.class})
public class ChromatogramIndexCurator implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.retentionindexcurator";
	private static final String NAME = "Retention Index Curator";
	private static final String DESCRIPTION = "Curate retention indices by peaks";

	@Override
	public String getCategory() {

		return ICategories.CHROMATOGRAM_CALCULATOR;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<IndexCuratorSettings> implements IChromatogramSelectionProcessSupplier<IndexCuratorSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, IndexCuratorSettings.class, parent, DataCategory.MSD, DataCategory.CSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, IndexCuratorSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
			/*
			 * Curate
			 */
			RetentionIndexExtractor retentionIndexExtractor = new RetentionIndexExtractor();
			boolean useCuratedNames = processSettings.isUseCuratedNames();
			boolean deriveMissingIndices = processSettings.isDeriveMissingIndices();
			ISeparationColumnIndices separationColumnIndices = retentionIndexExtractor.extract(chromatogram, deriveMissingIndices, useCuratedNames);
			/*
			 * Assign
			 */
			boolean extrapolateLeft = processSettings.isExtrapolateLeft();
			boolean extrapolateRight = processSettings.isExtrapolateRight();
			RetentionIndexMarker retentionIndexMarker = RetentionIndexSupport.getRetentionIndexMarker(separationColumnIndices, chromatogram, extrapolateLeft, extrapolateRight);
			boolean processReferenceChromatograms = processSettings.isProcessReferenceChromatograms();
			RetentionIndexCalculator.calculateIndex(chromatogram, retentionIndexMarker, processReferenceChromatograms);
			/*
			 * Store the retention index marker in the chromatogram.
			 */
			if(processSettings.isStoreInChromatogram()) {
				ISeparationColumnIndices separationColumnIndicesChromatogram = chromatogram.getSeparationColumnIndices();
				RetentionIndexSupport.transferRetentionIndexMarker(retentionIndexMarker, separationColumnIndicesChromatogram);
			}
			//
			return chromatogramSelection;
		}
	}
}