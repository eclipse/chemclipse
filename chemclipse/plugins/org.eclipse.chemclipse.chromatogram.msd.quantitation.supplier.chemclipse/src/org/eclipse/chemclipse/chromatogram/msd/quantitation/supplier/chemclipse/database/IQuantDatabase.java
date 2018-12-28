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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.QuantitationCompoundAlreadyExistsException;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;

public interface IQuantDatabase extends Serializable {

	IQuantitationCompound getQuantitationCompound(String name);

	void addQuantitationCompound(IQuantitationCompound quantitationCompound) throws QuantitationCompoundAlreadyExistsException;

	List<IQuantitationCompound> getQuantitationCompounds();

	List<String> getQuantitationCompoundNames();

	String getQuantitationCompoundConcentrationUnit(String name);

	long countQuantitationCompounds();

	boolean isQuantitationCompoundAlreadyAvailable(String name);

	void deleteQuantitationCompound(IQuantitationCompound quantitationCompound);

	void deleteQuantitationCompound(List<IQuantitationCompound> quantitationCompounds);

	void deleteAllQuantitationCompounds();

	List<IQuantitationPeak> getQuantitationPeaks(IQuantitationCompound quantitationCompound);

	void deleteQuantitationPeakDocument(IQuantitationCompound quantitationCompound, IQuantitationPeak quantitationPeak);

	void deleteQuantitationPeakDocuments(IQuantitationCompound quantitationCompound, Set<IQuantitationPeak> quantitationPeaks);
}
