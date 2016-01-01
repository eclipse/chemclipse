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

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.AbstractMassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.IMassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.comparison.processing.MassSpectrumComparatorProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.distance.results.DistanceMassSpectrumComparisonResult;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumComparisonResult;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public class EuclideanMassSpectrumComparator extends AbstractMassSpectrumComparator implements IMassSpectrumComparator {

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
			DistanceMeasure distanceMeasure = new EuclideanDistance();
			float matchFactor = (1 - calculateMatch(unknown.getExtractedIonSignal(), reference.getExtractedIonSignal(), distanceMeasure)) * 100;
			float reverseMatchFactor = (1 - calculateMatch(reference.getExtractedIonSignal(), unknown.getExtractedIonSignal(), distanceMeasure)) * 100;
			//
			IMassSpectrumComparisonResult massSpectrumComparisonResult = new DistanceMassSpectrumComparisonResult(matchFactor, reverseMatchFactor);
			processingInfo.setMassSpectrumComparisonResult(massSpectrumComparisonResult);
		}
		return processingInfo;
	}

	/**
	 * Calculates the distance of both mass spectra.
	 * 0 : best match
	 * 1 : no match
	 * 
	 * @param unknownSignal
	 * @param referenceSignal
	 * @param distanceMeasure
	 * @return float
	 */
	private float calculateMatch(IExtractedIonSignal unknownSignal, IExtractedIonSignal referenceSignal, DistanceMeasure distanceMeasure) {

		int size = unknownSignal.getNumberOfIonValues();
		double unknown[] = new double[size];
		double reference[] = new double[size];
		for(int i = unknownSignal.getStartIon(), j = 0; i <= unknownSignal.getStopIon(); i++, j++) {
			unknown[j] = unknownSignal.getAbundance(i);
			reference[j] = referenceSignal.getAbundance(i);
		}
		/*
		 * Calculate the euclidean distance on the unit vector.
		 * An exception will be thrown if one or both vectors have only 0 elements.
		 */
		float match;
		try {
			/*
			 * No distance is the best match.
			 * Only positive values are used, hence both vectors are located in quadrant I only.
			 * As the max distance of both point could be max 2: (a^2 + b^2 = c^2) -> (1^2 + 1^2 = 2), the match is calculated as follows:
			 */
			float distance = (float)distanceMeasure.compute(new ArrayRealVector(unknown).unitVector().toArray(), new ArrayRealVector(reference).unitVector().toArray());
			match = 0.5f * distance;
		} catch(MathArithmeticException e) {
			match = 1; // No match.
		}
		return match;
	}
};