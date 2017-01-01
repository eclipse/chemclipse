/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.calculator;

import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;
import org.eclipse.chemclipse.msd.model.exceptions.EvaluationException;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.processing.IPeakQuantifierProcessingInfo;

public interface IQuantitationCalculatorMSD {

	/**
	 * Returns a list of quantitation entries.
	 * 
	 * @param peak
	 * @param quantitationCompound
	 * @return List<IQuantitationEntryMSD>
	 * @throws EvaluationException
	 */
	List<IQuantitationEntryMSD> calculateQuantitationResults(IPeakMSD peak, IQuantitationCompoundMSD quantitationCompound) throws EvaluationException;

	/**
	 * Returns a list of quantitation entries.
	 * 
	 * @param peak
	 * @param quantitationCompounds
	 * @return List<IQuantitationEntryMSD>
	 */
	List<IQuantitationEntryMSD> calculateQuantitationResults(IPeakMSD peak, List<IQuantitationCompoundMSD> quantitationCompounds, IPeakQuantifierProcessingInfo processingInfo);
}
