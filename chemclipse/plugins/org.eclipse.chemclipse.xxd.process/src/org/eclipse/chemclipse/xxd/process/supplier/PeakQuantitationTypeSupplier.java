/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.IPeakQuantifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.PeakQuantifier;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakQuantitationTypeSupplier extends AbstractProcessTypeSupplier implements IProcessTypeSupplier {

	public static final String CATEGORY = "Peak Quantifier";

	public PeakQuantitationTypeSupplier() {
		super(CATEGORY, new DataType[]{DataType.MSD, DataType.CSD});
	}

	@Override
	public String getProcessorName(String processorId) throws Exception {

		IPeakQuantifierSupplier quantifierSupplier = PeakQuantifier.getPeakQuantifierSupport().getPeakQuantifierSupplier(processorId);
		return quantifierSupplier.getPeakQuantifierName();
	}

	@Override
	public String getProcessorDescription(String processorId) throws Exception {

		IPeakQuantifierSupplier quantifierSupplier = PeakQuantifier.getPeakQuantifierSupport().getPeakQuantifierSupplier(processorId);
		return quantifierSupplier.getDescription();
	}

	@Override
	public List<String> getProcessorIds() throws Exception {

		return PeakQuantifier.getPeakQuantifierSupport().getAvailablePeakQuantifierIds();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public IProcessingInfo applyProcessor(IChromatogramSelection chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		List<IPeak> peaks = chromatogramSelection.getChromatogram().getPeaks();
		return PeakQuantifier.quantify(peaks, processorId, monitor);
	}
}
