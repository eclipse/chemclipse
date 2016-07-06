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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents.IQuantitationCompoundDocument;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents.IQuantitationPeakDocument;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents.QuantitationCompoundDocument;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents.QuantitationPeakDocument;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.exceptions.QuantitationCompoundAlreadyExistsException;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationPeakMSD;

public class QuantDatabase implements IQuantDatabase {

	@Override
	public long createQuantitationCompoundDocument(IQuantitationCompoundMSD quantitationCompound) throws QuantitationCompoundAlreadyExistsException {

		String name = quantitationCompound.getName();
		/*
		 * Only create the compound if it's not available.
		 */
		if(isQuantitationCompoundAlreadyAvailable(name)) {
			throw new QuantitationCompoundAlreadyExistsException("The compund already exists: " + name);
		} else {
			IQuantitationCompoundDocument quantitationCompoundDocument = new QuantitationCompoundDocument();
			quantitationCompoundDocument.setQuantitationCompound(quantitationCompound);
			System.out.println("save");
			return 1;
		}
	}

	@Override
	public long createQuantitationPeakDocument(IQuantitationPeakMSD quantitationPeakMSD, long quantitationCompoundId) {

		/*
		 * Quantitation Peak
		 */
		IQuantitationPeakDocument quantitationPeakDocument = new QuantitationPeakDocument();
		quantitationPeakDocument.setConcentration(quantitationPeakMSD.getConcentration());
		quantitationPeakDocument.setConcentrationUnit(quantitationPeakMSD.getConcentrationUnit());
		quantitationPeakDocument.setPeakMSD(quantitationPeakMSD.getReferencePeakMSD());
		System.out.println("save");
		long quantitationPeakDocumentId = 1;
		/*
		 * Add reference to quantitation compound.
		 */
		IQuantitationCompoundDocument quantitationCompoundDocument = getQuantitationCompoundDocument(quantitationCompoundId);
		Set<Long> quantitationPeakIds = quantitationCompoundDocument.getQuantitationPeakIds();
		quantitationPeakIds.add(quantitationPeakDocumentId);
		quantitationCompoundDocument.setQuantitationPeakIds(quantitationPeakIds);
		System.out.println("save");
		//
		return quantitationPeakDocumentId;
	}

	// ---------------------------------------------------------------------------------
	@Override
	public IQuantitationCompoundDocument getQuantitationCompoundDocument(long id) {

		System.out.println("query");
		Object document = null; // queryDocumentById(IQuantitationCompoundDocument.CLASS_NAME, id);
		if(document == null) {
			return null;
		} else {
			return new QuantitationCompoundDocument();
		}
	}

	@Override
	public IQuantitationCompoundDocument getQuantitationCompoundDocument(String name) {

		IQuantitationCompoundDocument quantitationCompoundDocument = null;
		/*
		 * Create a new entry or return the id if the entry still exists.
		 */
		System.out.println("query");
		String escapedName = name; // .replaceAll(IDocument.SINGLE_QUOTE, IDocument.ESCAPED_SINGLE_QUOTE);
		List<Object> results = new ArrayList<Object>();// queryDocumentsByClassName(IQuantitationCompoundDocument.CLASS_NAME, " WHERE ", IQuantitationCompoundDocument.FIELD_NAME, " = '", escapedName, "'");
		if(results.size() > 0) {
			quantitationCompoundDocument = new QuantitationCompoundDocument();
		}
		return quantitationCompoundDocument;
	}

	@Override
	public IQuantitationPeakDocument getQuantitationPeakDocument(long id) {

		System.out.println("query");
		Object document = null; // queryDocumentById(IQuantitationPeakDocument.CLASS_NAME, id);
		if(document == null) {
			return null;
		} else {
			return new QuantitationPeakDocument();
		}
	}

	@Override
	public List<IQuantitationCompoundDocument> getQuantitationCompoundDocuments() {

		IQuantitationCompoundDocument quantitationCompoundDocument;
		List<IQuantitationCompoundDocument> quantitationCompoundDocuments = new ArrayList<IQuantitationCompoundDocument>();
		/*
		 * Query the database
		 */
		System.out.println("query");
		List<Object> documents = new ArrayList<Object>(); // queryDocumentsByClassName(IQuantitationCompoundDocument.CLASS_NAME);
		for(Object document : documents) {
			/*
			 * Create a new LibraryRecordDocument.
			 */
			quantitationCompoundDocument = new QuantitationCompoundDocument();
			quantitationCompoundDocuments.add(quantitationCompoundDocument);
		}
		return quantitationCompoundDocuments;
	}

	@Override
	public List<String> getQuantitationCompoundDocumentNames() {

		List<String> documentNames = new ArrayList<String>();
		List<Object> documents = new ArrayList<Object>(); // queryDocumentsByClassName(IQuantitationCompoundDocument.CLASS_NAME, " FROM ", IQuantitationCompoundDocument.CLASS_NAME);
		System.out.println("query");
		for(Object document : documents) {
			/*
			 * Get the name.
			 */
			String name = "todo"; // document.field(IQuantitationCompoundDocument.FIELD_NAME);
			documentNames.add(name);
		}
		return documentNames;
	}

	@Override
	public List<IQuantitationPeakDocument> getQuantitationPeakDocuments(IQuantitationCompoundDocument quantitationCompoundDocument) {

		List<IQuantitationPeakDocument> quantitationPeakDocuments = new ArrayList<IQuantitationPeakDocument>();
		if(quantitationCompoundDocument != null) {
			Set<Long> quantitationPeakIds = quantitationCompoundDocument.getQuantitationPeakIds();
			for(long quantitationPeakId : quantitationPeakIds) {
				IQuantitationPeakDocument quantitationPeakDocument = getQuantitationPeakDocument(quantitationPeakId);
				if(quantitationPeakDocument != null) {
					quantitationPeakDocuments.add(quantitationPeakDocument);
				}
			}
		}
		return quantitationPeakDocuments;
	}

	@Override
	public String getQuantitationCompoundDocumentConcentrationUnit(String name) {

		String concentrationUnit = "";
		/*
		 * Query the database
		 */
		// builder.append("SELECT ");
		// builder.append(IQuantitationCompoundDocument.FIELD_CONCENTRATION_UNIT);
		// builder.append(" FROM ");
		// builder.append(IQuantitationCompoundDocument.CLASS_NAME);
		//
		System.out.println("query");
		System.out.println("unit");
		concentrationUnit = "mg/ml"; // documents.get(0).field(IQuantitationCompoundDocument.FIELD_CONCENTRATION_UNIT);
		return concentrationUnit;
	}

	// ---------------------------------------------------------------------------------
	@Override
	public long countQuantitationCompoundDocuments() {

		System.out.println("count");
		return 1;// countCluster(IQuantitationCompoundDocument.CLASS_NAME.toLowerCase());
	}

	@Override
	public boolean isQuantitationCompoundAlreadyAvailable(String name) {

		boolean result;
		if(countQuantitationCompoundDocuments() == 0) {
			result = false;
		} else {
			IQuantitationCompoundDocument document = getQuantitationCompoundDocument(name);
			if(document == null) {
				result = false;
			} else {
				result = true;
			}
		}
		return result;
	}

	// ---------------------------------------------------------------------------------
	@Override
	public void deleteQuantitationCompoundDocument(IQuantitationCompoundDocument quantitationCompoundDocument) {

		deleteQuantitationCompoundDocument(quantitationCompoundDocument.getDocumentId());
		Set<Long> quantitationPeakIds = quantitationCompoundDocument.getQuantitationPeakIds();
		for(long quantitationPeakId : quantitationPeakIds) {
			deleteQuantitationPeakDocument(quantitationPeakId);
		}
	}

	@Override
	public void deleteQuantitationCompoundDocuments(List<IQuantitationCompoundDocument> quantitationCompoundDocuments) {

		if(quantitationCompoundDocuments != null) {
			for(IQuantitationCompoundDocument quantitationCompoundDocument : quantitationCompoundDocuments) {
				deleteQuantitationCompoundDocument(quantitationCompoundDocument);
			}
		}
	}

	@Override
	public void deleteAllQuantitationCompoundDocuments() {

		List<IQuantitationCompoundDocument> quantitationCompoundDocuments = getQuantitationCompoundDocuments();
		deleteQuantitationCompoundDocuments(quantitationCompoundDocuments);
	}

	private void deleteQuantitationPeakDocument(long id) {

		deleteDocument(IQuantitationPeakDocument.CLASS_NAME, id);
	}

	@Override
	public void deleteQuantitationPeakDocument(IQuantitationCompoundDocument quantitationCompoundDocument, long id) {

		/*
		 * Remove the reference from the quantitation document.
		 */
		Set<Long> quantitationPeakIds = quantitationCompoundDocument.getQuantitationPeakIds();
		quantitationPeakIds.remove(id);
		quantitationCompoundDocument.setQuantitationPeakIds(quantitationPeakIds);
		System.out.println("save");
		/*
		 * Delete the peak document.
		 */
		deleteQuantitationCompoundDocument(id);
	}

	@Override
	public void deleteQuantitationPeakDocuments(IQuantitationCompoundDocument quantitationCompoundDocument, Set<Long> ids) {

		/*
		 * Remove the references from the quantitation document.
		 */
		Set<Long> quantitationPeakIds = quantitationCompoundDocument.getQuantitationPeakIds();
		quantitationPeakIds.remove(ids);
		quantitationCompoundDocument.setQuantitationPeakIds(quantitationPeakIds);
		System.out.println("save");
		/*
		 * Delete the peak documents.
		 */
		for(long id : ids) {
			deleteQuantitationCompoundDocument(id);
		}
	}

	/**
	 * Deletes the quantitation compound document only.
	 * 
	 * @param id
	 */
	private void deleteQuantitationCompoundDocument(long id) {

		deleteDocument(IQuantitationCompoundDocument.CLASS_NAME, id);
	}

	/**
	 * Take care. This method deletes the document by the class name and given id.
	 * 
	 * @param className
	 * @param id
	 */
	protected void deleteDocument(String className, long id) {

		System.out.println("delete");
	}
}
