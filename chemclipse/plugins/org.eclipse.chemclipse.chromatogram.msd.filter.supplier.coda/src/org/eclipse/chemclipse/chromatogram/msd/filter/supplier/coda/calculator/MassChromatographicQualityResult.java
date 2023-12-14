/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.calculator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.coda.exceptions.CodaCalculatorException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;

public class MassChromatographicQualityResult implements IMassChromatographicQualityResult {

	private static final Logger logger = Logger.getLogger(MassChromatographicQualityResult.class);
	private float dataReductionValue;
	private IMarkedIons excludedIons;

	/**
	 * Create a new mass chromatographic quality result object.
	 * 
	 * @param chromatogramSelection
	 * @param codaThreshold
	 * @param windowSize
	 * @throws CodaCalculatorException
	 */
	public MassChromatographicQualityResult(IChromatogramSelectionMSD chromatogramSelection, float codaThreshold, int windowSize) throws CodaCalculatorException {

		validateChromatogramSelection(chromatogramSelection);
		/*
		 * Create a new excluded ions object.
		 */
		excludedIons = new MarkedIons(MarkedTraceModus.INCLUDE);
		calculateMassChromatographicQuality(chromatogramSelection, codaThreshold, windowSize, excludedIons);
	}

	@Override
	public IMarkedIons getExcludedIons() {

		return excludedIons;
	}

	@Override
	public float getDataReductionValue() {

		return dataReductionValue;
	}

	// ---------------------------------private methods
	/**
	 * Calculates all representative values.<br/>
	 * MCQValue<br/>
	 * DataReductionValue<br/>
	 * ExcludedIons
	 */
	private void calculateMassChromatographicQuality(IChromatogramSelectionMSD chromatogramSelection, float codaThreshold, int windowSize, IMarkedIons excludedIons) {

		assert (chromatogramSelection != null) : "The chromatogram selection must not be null.";
		assert (chromatogramSelection.getChromatogram() != null) : "The chromatogram must not be null.";
		assert (excludedIons != null) : "The excluded ions must not be null.";
		List<Float> mcqs = new ArrayList<>();
		float mcq;
		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
		try {
			IExtractedIonSignalExtractor extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
			IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
			int startIon = extractedIonSignals.getStartIon();
			int stopIon = extractedIonSignals.getStopIon();
			for(int ion = startIon; ion <= stopIon; ion++) {
				mcq = CodaCalculator.getMCQValue(extractedIonSignals, windowSize, ion);
				/*
				 * Add the ion value to the excluded ion list?
				 */
				if(mcq < codaThreshold) {
					excludedIons.add(new MarkedIon(ion));
				}
				/*
				 * Use mcqs to determine the data reduction value.
				 */
				mcqs.add(mcq);
			}
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		}
		dataReductionValue = calculateDataReductionValue(mcqs, codaThreshold);
	}

	private float calculateDataReductionValue(List<Float> mcqs, float codaThreshold) {

		float selected = 0.0f;
		/*
		 * Avoid a null pointer or a division by zero exception.
		 */
		if(mcqs == null || mcqs.isEmpty()) {
			return 0.0f;
		}
		/*
		 * How many values are higher or equal than the given threshold?
		 */
		for(Float f : mcqs) {
			if(f >= codaThreshold) {
				selected++;
			}
		}
		/*
		 * Calculate the data reduction value.
		 */
		return selected / mcqs.size();
	}

	/**
	 * Validates the chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 * @throws CodaCalculatorException
	 */
	private void validateChromatogramSelection(IChromatogramSelectionMSD chromatogramSelection) throws CodaCalculatorException {

		if(chromatogramSelection == null) {
			throw new CodaCalculatorException("The chromatogram selection must not be null.");
		}
		if(chromatogramSelection.getChromatogram() == null) {
			throw new CodaCalculatorException("The chromatogram must not be null.");
		}
	}
}
