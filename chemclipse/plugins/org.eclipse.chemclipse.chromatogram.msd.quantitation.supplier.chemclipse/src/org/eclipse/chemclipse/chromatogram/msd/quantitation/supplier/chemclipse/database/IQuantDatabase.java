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

import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationPeakMSD;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents.IQuantitationCompoundDocument;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents.IQuantitationPeakDocument;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.QuantitationCompoundAlreadyExistsException;
import org.eclipse.chemclipse.database.model.IDatabase;

public interface IQuantDatabase extends IDatabase {

	/*
	 * Create
	 */
	long createQuantitationCompoundDocument(IQuantitationCompoundMSD quantitationCompoundMSD) throws QuantitationCompoundAlreadyExistsException;

	long createQuantitationPeakDocument(IQuantitationPeakMSD quantitationPeakMSD, long quantitationCompoundId);

	/*
	 * Get
	 */
	IQuantitationCompoundDocument getQuantitationCompoundDocument(long id);

	IQuantitationCompoundDocument getQuantitationCompoundDocument(String name);

	IQuantitationPeakDocument getQuantitationPeakDocument(long id);

	List<IQuantitationCompoundDocument> getQuantitationCompoundDocuments();

	List<String> getQuantitationCompoundDocumentNames();

	List<IQuantitationPeakDocument> getQuantitationPeakDocuments(IQuantitationCompoundDocument quantitationCompoundDocument);

	String getQuantitationCompoundDocumentConcentrationUnit(String name); // TODO

	/*
	 * Count
	 */
	long countQuantitationCompoundDocuments();

	/*
	 * Exists
	 */
	boolean isQuantitationCompoundAlreadyAvailable(String name);

	/*
	 * Delete
	 */
	void deleteQuantitationCompoundDocument(IQuantitationCompoundDocument quantitationCompoundDocument);

	void deleteQuantitationCompoundDocuments(List<IQuantitationCompoundDocument> quantitationCompoundDocuments);

	void deleteAllQuantitationCompoundDocuments();

	void deleteQuantitationPeakDocument(IQuantitationCompoundDocument quantitationCompoundDocument, long id);

	void deleteQuantitationPeakDocuments(IQuantitationCompoundDocument quantitationCompoundDocument, Set<Long> ids);
}
