/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - don't extract ion signal more than once, use lazy result
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.distance.comparator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.AbstractMassSpectrumComparator;
import org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum.IMassSpectrumComparator;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.LazyComparisonResult;
import org.eclipse.chemclipse.model.identifier.MatchConstraints;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public abstract class AbstractCosineComparator extends AbstractMassSpectrumComparator implements IMassSpectrumComparator {

	@Override
	public IProcessingInfo<IComparisonResult> compare(IScanMSD unknown, IScanMSD reference, MatchConstraints matchConstraints) {

		IProcessingInfo<IComparisonResult> processingInfo = super.validate(unknown, reference);
		if(!processingInfo.hasErrorMessages()) {
			/*
			 * Get the match and reverse match factor.
			 * Internally it's normalized to 1, but a percentage value is used by the MS methods.
			 */
			IExtractedIonSignal unknownSignal = unknown.getExtractedIonSignal();
			IExtractedIonSignal referenceSignal = reference.getExtractedIonSignal();
			IComparisonResult massSpectrumComparisonResult = new LazyComparisonResult( //
					() -> calculateCosinePhi(unknownSignal, referenceSignal), //
					() -> calculateCosinePhi(referenceSignal, unknownSignal), //
					() -> calculateCosinePhiDirect(unknownSignal, referenceSignal), //
					() -> calculateCosinePhiDirect(referenceSignal, unknownSignal), //
					matchConstraints //
			);
			//
			processingInfo.setProcessingResult(massSpectrumComparisonResult);
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
	public double calculateCosinePhi(IExtractedIonSignal unknownSignal, IExtractedIonSignal referenceSignal) {

		int size = unknownSignal.getNumberOfIonValues();
		double unknown[] = new double[size];
		double reference[] = new double[size];
		for(int i = unknownSignal.getStartIon(), j = 0; i <= unknownSignal.getStopIon(); i++, j++) {
			unknown[j] = getVectorValue(unknownSignal, i);
			reference[j] = getVectorValue(referenceSignal, i);
		}
		/*
		 * Calculate the cosine phi.
		 */
		ArrayRealVector unknownVector = new ArrayRealVector(unknown);
		ArrayRealVector referenceVector = new ArrayRealVector(reference);
		//
		try {
			return unknownVector.cosine(referenceVector) * 100;
		} catch(MathArithmeticException | DimensionMismatchException e) {
			return 0;
		}
	}

	public double calculateCosinePhiDirect(IExtractedIonSignal unknownSignal, IExtractedIonSignal referenceSignal) {

		List<Integer> ionList = new ArrayList<Integer>();
		int startIon = unknownSignal.getStartIon();
		int stopIon = unknownSignal.getStopIon();
		for(int ion = startIon; ion <= stopIon; ion++) {
			if(unknownSignal.getAbundance(ion) > 0.0f) {
				ionList.add(ion);
			}
		}
		//
		double unknown[] = new double[ionList.size()];
		double reference[] = new double[ionList.size()];
		int j = 0;
		for(int ion : ionList) {
			unknown[j] = unknownSignal.getAbundance(ion);
			reference[j] = referenceSignal.getAbundance(ion);
			j++;
		}
		/*
		 * Calculate the cosine phi.
		 */
		ArrayRealVector unknownVector = new ArrayRealVector(unknown);
		ArrayRealVector referenceVector = new ArrayRealVector(reference);
		try {
			return unknownVector.cosine(referenceVector) * 100;
		} catch(MathArithmeticException | DimensionMismatchException e) {
			return 0;
		}
	}

	/**
	 * This method returns the intensity value.
	 * Override it in subclasses to enabled a specific behavior.
	 * 
	 * @param signal
	 * @param i
	 * @return double
	 */
	abstract double getVectorValue(IExtractedIonSignal signal, int i);
}
