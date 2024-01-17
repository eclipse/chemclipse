/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.RetentionIndexRecalculatorSettings;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.model.core.IChromatogram;
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
public class ChromatogramRetentionIndexRecalculator implements IProcessTypeSupplier {

	private static final String ID = "org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.retentionindexrecalculator";
	private static final String NAME = "Retention Index (Recalculator)";
	private static final String DESCRIPTION = "Calculates the retention indices for scans and peaks based on the stored RIs in the chromatogram.";

	@Override
	public String getCategory() {

		return ICategories.CHROMATOGRAM_CALCULATOR;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new ProcessSupplier(this));
	}

	private static final class ProcessSupplier extends AbstractProcessSupplier<RetentionIndexRecalculatorSettings> implements IChromatogramSelectionProcessSupplier<RetentionIndexRecalculatorSettings> {

		public ProcessSupplier(IProcessTypeSupplier parent) {

			super(ID, NAME, DESCRIPTION, RetentionIndexRecalculatorSettings.class, parent, DataCategory.MSD, DataCategory.CSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, RetentionIndexRecalculatorSettings processSettings, ProcessExecutionContext context) throws InterruptedException {

			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			boolean processReferenceChromatograms = processSettings.isProcessReferenceChromatograms();
			/*
			 * Recalculate
			 */
			processChromatogram(chromatogram);
			if(processReferenceChromatograms) {
				for(IChromatogram<?> chromatogramReference : chromatogram.getReferencedChromatograms()) {
					processChromatogram(chromatogramReference);
				}
			}
			//
			return chromatogramSelection;
		}

		private void processChromatogram(IChromatogram<?> chromatogram) {

			ISeparationColumnIndices separationColumnIndices = chromatogram.getSeparationColumnIndices();
			RetentionIndexCalculator.calculateIndex(chromatogram, separationColumnIndices);
		}
	}
}