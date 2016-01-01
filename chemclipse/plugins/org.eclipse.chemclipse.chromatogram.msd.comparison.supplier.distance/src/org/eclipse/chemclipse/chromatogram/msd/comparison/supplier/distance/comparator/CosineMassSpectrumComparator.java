/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.distance.comparator;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.AbstractMassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.IMassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.MassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.distance.results.DistanceMassSpectrumComparisonResult;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumComparisonResult;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public class CosineMassSpectrumComparator extends AbstractMassSpectrumComparator implements IMassSpectrumComparator {

	@Override
	public IMassSpectrumComparatorProcessingInfo compare(IScanMSD unknown, IScanMSD reference) {

		IMassSpectrumComparatorProcessingInfo processingInfo = new MassSpectrumComparatorProcessingInfo();
		IProcessingInfo processingInfoValidate = super.validate(unknown, reference);
		if(processingInfoValidate.hasErrorMessages()) {
			processingInfo.addMessages(processingInfoValidate);
		} else {
			/*
			 * Get the match and reverse match factor.
			 * Internally it's normalized to 1, but a percentage value is used by the MS methods.
			 */
			float matchFactor = calculateCosinePhi(unknown.getExtractedIonSignal(), reference.getExtractedIonSignal()) * 100;
			float reverseMatchFactor = calculateCosinePhi(reference.getExtractedIonSignal(), unknown.getExtractedIonSignal()) * 100;
			//
			IMassSpectrumComparisonResult massSpectrumComparisonResult = new DistanceMassSpectrumComparisonResult(matchFactor, reverseMatchFactor);
			processingInfo.setMassSpectrumComparisonResult(massSpectrumComparisonResult);
		}
		return processingInfo;
	}

	/**
	 * Calculates the distance of both mass spectra.
	 * 1 : best match
	 * 0 : no match
	 * 
	 * @param unknownSignal
	 * @param referenceSignal
	 * @param distanceMeasure
	 * @return float
	 */
	public float calculateCosinePhi(IExtractedIonSignal unknownSignal, IExtractedIonSignal referenceSignal) {

		int size = unknownSignal.getNumberOfIonValues();
		double unknown[] = new double[size];
		double reference[] = new double[size];
		for(int i = unknownSignal.getStartIon(), j = 0; i <= unknownSignal.getStopIon(); i++, j++) {
			unknown[j] = unknownSignal.getAbundance(i);
			reference[j] = referenceSignal.getAbundance(i);
		}
		/*
		 * Calculate the cosine phi.
		 */
		ArrayRealVector unknownVector = new ArrayRealVector(unknown);
		ArrayRealVector referenceVector = new ArrayRealVector(reference);
		//
		float match;
		try {
			match = (float)unknownVector.cosine(referenceVector);
		} catch(MathArithmeticException | DimensionMismatchException e) {
			match = 0.0f;
		}
		return match;
	}
};