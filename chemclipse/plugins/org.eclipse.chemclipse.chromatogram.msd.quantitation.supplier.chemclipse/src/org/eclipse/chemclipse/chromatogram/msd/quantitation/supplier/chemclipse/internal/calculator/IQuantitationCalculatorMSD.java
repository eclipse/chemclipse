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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.internal.calculator;

import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;
import org.eclipse.chemclipse.msd.model.exceptions.EvaluationException;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

public interface IQuantitationCalculatorMSD {

	/**
	 * Returns a list of quantitation entries.
	 * 
	 * @param peak
	 * @param quantitationCompound
	 * @return List<IQuantitationEntryMSD>
	 * @throws EvaluationException
	 */
	List<IQuantitationEntryMSD> calculateQuantitationResults(IPeak peak, IQuantitationCompound quantitationCompound) throws EvaluationException;

	/**
	 * Returns a list of quantitation entries.
	 * 
	 * @param peak
	 * @param quantitationCompounds
	 * @return List<IQuantitationEntryMSD>
	 */
	List<IQuantitationEntryMSD> calculateQuantitationResults(IPeak peak, List<IQuantitationCompound> quantitationCompounds, IProcessingInfo processingInfo);
}
