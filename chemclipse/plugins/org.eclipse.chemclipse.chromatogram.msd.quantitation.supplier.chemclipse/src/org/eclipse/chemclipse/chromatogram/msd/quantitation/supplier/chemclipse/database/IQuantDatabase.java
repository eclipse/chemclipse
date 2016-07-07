/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database;

import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.QuantitationCompoundAlreadyExistsException;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationPeakMSD;

public interface IQuantDatabase {

	IQuantitationCompoundMSD getQuantitationCompound(String name);

	void addQuantitationCompound(IQuantitationCompoundMSD quantitationCompound) throws QuantitationCompoundAlreadyExistsException;

	List<IQuantitationCompoundMSD> getQuantitationCompounds();

	List<String> getQuantitationCompoundNames();

	String getQuantitationCompoundConcentrationUnit(String name);

	long countQuantitationCompounds();

	boolean isQuantitationCompoundAlreadyAvailable(String name);

	void deleteQuantitationCompound(IQuantitationCompoundMSD quantitationCompound);

	void deleteQuantitationCompound(List<IQuantitationCompoundMSD> quantitationCompounds);

	void deleteAllQuantitationCompounds();

	List<IQuantitationPeakMSD> getQuantitationPeaks(IQuantitationCompoundMSD quantitationCompound);

	void deleteQuantitationPeakDocument(IQuantitationCompoundMSD quantitationCompound, IQuantitationPeakMSD quantitationPeak);

	void deleteQuantitationPeakDocuments(IQuantitationCompoundMSD quantitationCompound, Set<IQuantitationPeakMSD> quantitationPeaks);
}
