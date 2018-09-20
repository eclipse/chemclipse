/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.wizard.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.ui.wizards.ChromatogramWizardElements;
import org.eclipse.chemclipse.xxd.process.model.ChromatogramProcessEntry;
import org.eclipse.chemclipse.xxd.process.model.IChromatogramProcessEntry;
import org.eclipse.chemclipse.xxd.process.supplier.BaselineDetectorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramCalculatorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakDetectorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIdentifierTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIntegratorTypeSupplier;

public class EvaluationWizardElements extends ChromatogramWizardElements implements IEvaluationWizardElements {

	private String description = ""; // Could be ""
	private List<IChromatogramProcessEntry> processingEntries;
	private String notes = ""; // Could be ""

	public EvaluationWizardElements() {
		initalizeProcessingEntries();
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public IChromatogramProcessEntry getProcessEntry(String category) {

		for(IChromatogramProcessEntry processEntry : processingEntries) {
			if(processEntry.getProcessCategory().equals(category)) {
				return processEntry;
			}
		}
		return null;
	}

	@Override
	public List<IChromatogramProcessEntry> getProcessingEntries() {

		return processingEntries;
	}

	@Override
	public String getNotes() {

		return notes;
	}

	@Override
	public void setNotes(String notes) {

		this.notes = notes;
	}

	private void initalizeProcessingEntries() {

		processingEntries = new ArrayList<IChromatogramProcessEntry>();
		processingEntries.add(new ChromatogramProcessEntry(ChromatogramFilterTypeSupplierMSD.CATEGORY, ""));
		processingEntries.add(new ChromatogramProcessEntry(ChromatogramFilterTypeSupplier.CATEGORY, ""));
		processingEntries.add(new ChromatogramProcessEntry(BaselineDetectorTypeSupplier.CATEGORY, ""));
		processingEntries.add(new ChromatogramProcessEntry(PeakDetectorTypeSupplier.CATEGORY, ""));
		processingEntries.add(new ChromatogramProcessEntry(PeakIntegratorTypeSupplier.CATEGORY, ""));
		processingEntries.add(new ChromatogramProcessEntry(ChromatogramCalculatorTypeSupplier.CATEGORY, ""));
		processingEntries.add(new ChromatogramProcessEntry(PeakIdentifierTypeSupplier.CATEGORY, ""));
	}
}
