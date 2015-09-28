/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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
import org.eclipse.chemclipse.database.documents.IDocument;
import org.eclipse.chemclipse.database.model.AbstractDatabase;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationPeakMSD;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class QuantDatabase extends AbstractDatabase implements IQuantDatabase {

	public QuantDatabase(String database, String user, String password) throws org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException {

		super(database, user, password);
	}

	// ---------------------------------------------------------------------------------
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
			return quantitationCompoundDocument.save().getIdentity().getClusterPosition();
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
		long quantitationPeakDocumentId = quantitationPeakDocument.save().getIdentity().getClusterPosition();
		/*
		 * Add reference to quantitation compound.
		 */
		IQuantitationCompoundDocument quantitationCompoundDocument = getQuantitationCompoundDocument(quantitationCompoundId);
		Set<Long> quantitationPeakIds = quantitationCompoundDocument.getQuantitationPeakIds();
		quantitationPeakIds.add(quantitationPeakDocumentId);
		quantitationCompoundDocument.setQuantitationPeakIds(quantitationPeakIds);
		quantitationCompoundDocument.save();
		//
		return quantitationPeakDocumentId;
	}

	// ---------------------------------------------------------------------------------
	@Override
	public IQuantitationCompoundDocument getQuantitationCompoundDocument(long id) {

		ODocument document = queryDocumentById(IQuantitationCompoundDocument.CLASS_NAME, id);
		if(document == null) {
			return null;
		} else {
			return new QuantitationCompoundDocument(document);
		}
	}

	@Override
	public IQuantitationCompoundDocument getQuantitationCompoundDocument(String name) {

		IQuantitationCompoundDocument quantitationCompoundDocument = null;
		/*
		 * Create a new entry or return the id if the entry still exists.
		 */
		String escapedName = name.replaceAll(IDocument.SINGLE_QUOTE, IDocument.ESCAPED_SINGLE_QUOTE);
		List<ODocument> results = queryDocumentsByClassName(IQuantitationCompoundDocument.CLASS_NAME, " WHERE ", IQuantitationCompoundDocument.FIELD_NAME, " = '", escapedName, "'");
		if(results.size() > 0) {
			ODocument document = results.get(0);
			quantitationCompoundDocument = new QuantitationCompoundDocument(document);
		}
		return quantitationCompoundDocument;
	}

	@Override
	public IQuantitationPeakDocument getQuantitationPeakDocument(long id) {

		ODocument document = queryDocumentById(IQuantitationPeakDocument.CLASS_NAME, id);
		if(document == null) {
			return null;
		} else {
			return new QuantitationPeakDocument(document);
		}
	}

	@Override
	public List<IQuantitationCompoundDocument> getQuantitationCompoundDocuments() {

		IQuantitationCompoundDocument quantitationCompoundDocument;
		List<IQuantitationCompoundDocument> quantitationCompoundDocuments = new ArrayList<IQuantitationCompoundDocument>();
		/*
		 * Query the database
		 */
		List<ODocument> documents = queryDocumentsByClassName(IQuantitationCompoundDocument.CLASS_NAME);
		for(ODocument document : documents) {
			/*
			 * Create a new LibraryRecordDocument.
			 */
			quantitationCompoundDocument = new QuantitationCompoundDocument(document);
			quantitationCompoundDocuments.add(quantitationCompoundDocument);
		}
		return quantitationCompoundDocuments;
	}

	@Override
	public List<String> getQuantitationCompoundDocumentNames() {

		List<String> documentNames = new ArrayList<String>();
		List<ODocument> documents = queryDocumentsByClassName(IQuantitationCompoundDocument.CLASS_NAME, " FROM ", IQuantitationCompoundDocument.CLASS_NAME);
		for(ODocument document : documents) {
			/*
			 * Get the name.
			 */
			String name = document.field(IQuantitationCompoundDocument.FIELD_NAME);
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
		List<ODocument> documents = queryDocumentsByClassName(IQuantitationCompoundDocument.CLASS_NAME);
		if(documents.size() > 0) {
			/*
			 * Get the concentration unit.
			 */
			concentrationUnit = documents.get(0).field(IQuantitationCompoundDocument.FIELD_CONCENTRATION_UNIT);
		}
		return concentrationUnit;
	}

	// ---------------------------------------------------------------------------------
	@Override
	public long countQuantitationCompoundDocuments() {

		return countCluster(IQuantitationCompoundDocument.CLASS_NAME.toLowerCase());
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
		quantitationCompoundDocument.save();
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
		quantitationCompoundDocument.save();
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

		ODocument document = queryDocumentById(className, id);
		if(document != null) {
			document.delete();
		}
	}
}
