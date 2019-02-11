/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.openchromx.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.support.DateSupport;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.support.IChromatogramTags;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.support.IConstants;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.support.IonSupport;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriter extends AbstractChromatogramWriter implements IChromatogramMSDWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
		try {
			XMLEventWriter eventWriter = xmlOutputFactory.createXMLEventWriter(bufferedOutputStream, "UTF-8");
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			/*
			 * Document
			 */
			eventWriter.add(eventFactory.createStartDocument());
			StartElement chromatogramStart = eventFactory.createStartElement("", "", IChromatogramTags.CHROMATOGRAM);
			eventWriter.add(chromatogramStart);
			writeChromatogramIdentifier(eventWriter, eventFactory);
			writeChromatogramHeader(eventWriter, eventFactory, chromatogram);
			/*
			 * Comment
			 */
			Comment massSpectraInfo = eventFactory.createComment("The retention time is given in milliseconds. All ions are encoded with the Base64 algorithm. It's structure is ion-abundance ion-abundance ...");
			eventWriter.add(massSpectraInfo);
			monitor.subTask(IConstants.EXPORT_CHROMATOGRAM);
			writeChromatogramMassSpectra(eventWriter, eventFactory, chromatogram, monitor);
			EndElement chromatogramEnd = eventFactory.createEndElement("", "", IChromatogramTags.CHROMATOGRAM);
			eventWriter.add(chromatogramEnd);
			eventWriter.add(eventFactory.createEndDocument());
			/*
			 * Close the streams
			 */
			bufferedOutputStream.flush();
			eventWriter.flush();
			bufferedOutputStream.close();
			eventWriter.close();
		} catch(XMLStreamException e) {
			throw new IOException(e);
		}
	}

	// ------------------------------------------private methods
	/**
	 * Writes the chromatogram identifier. It makes it possible to check if the
	 * converter can convert the file if no special id is given. See
	 * ChromatogramConverter.
	 * 
	 * @throws XMLStreamException
	 */
	private void writeChromatogramIdentifier(XMLEventWriter eventWriter, XMLEventFactory eventFactory) throws XMLStreamException {

		StartElement chromatogramIdentifierStart = eventFactory.createStartElement("", "", IChromatogramTags.IDENTIFIER);
		EndElement chromatogramIdentifierEnd = eventFactory.createEndElement("", "", IChromatogramTags.IDENTIFIER);
		Characters chromatogramIdentifierId = eventFactory.createCharacters(IChromatogramTags.IDENTIFIER_ID);
		eventWriter.add(chromatogramIdentifierStart);
		eventWriter.add(chromatogramIdentifierId);
		eventWriter.add(chromatogramIdentifierEnd);
	}

	/**
	 * Writes the chromatogram header.
	 * 
	 * @throws XMLStreamException
	 */
	private void writeChromatogramHeader(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IChromatogramMSD chromatogram) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement headerStart = eventFactory.createStartElement("", "", IChromatogramTags.HEADER);
		EndElement headerEnd = eventFactory.createEndElement("", "", IChromatogramTags.HEADER);
		/*
		 * Write the elements.
		 */
		eventWriter.add(headerStart);
		writeOperator(eventWriter, eventFactory, chromatogram);
		writeDate(eventWriter, eventFactory, chromatogram);
		writeMiscInfo(eventWriter, eventFactory, chromatogram);
		IEditHistory editHistory = chromatogram.getEditHistory();
		writeEditHistory(eventWriter, eventFactory, editHistory);
		eventWriter.add(headerEnd);
	}

	private void writeOperator(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IChromatogramMSD chromatogram) throws XMLStreamException {

		StartElement operatorStart = eventFactory.createStartElement("", "", IChromatogramTags.OPERATOR);
		EndElement operatorEnd = eventFactory.createEndElement("", "", IChromatogramTags.OPERATOR);
		Characters operator = eventFactory.createCData(chromatogram.getOperator());
		eventWriter.add(operatorStart);
		eventWriter.add(operator);
		eventWriter.add(operatorEnd);
	}

	/**
	 * Writes the date.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param chromatogram
	 * @throws XMLStreamException
	 */
	private void writeDate(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IChromatogramMSD chromatogram) throws XMLStreamException {

		StartElement dateStart = eventFactory.createStartElement("", "", IChromatogramTags.DATE);
		EndElement dateEnd = eventFactory.createEndElement("", "", IChromatogramTags.DATE);
		Characters date = eventFactory.createCharacters(DateSupport.getDate(chromatogram.getDate()));
		eventWriter.add(dateStart);
		eventWriter.add(date);
		eventWriter.add(dateEnd);
	}

	private void writeMiscInfo(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IChromatogramMSD chromatogram) throws XMLStreamException {

		StartElement miscInfoStart = eventFactory.createStartElement("", "", IChromatogramTags.MISC_INFO);
		EndElement miscInfoEnd = eventFactory.createEndElement("", "", IChromatogramTags.MISC_INFO);
		Characters miscInfo = eventFactory.createCData(chromatogram.getMiscInfo());
		eventWriter.add(miscInfoStart);
		eventWriter.add(miscInfo);
		eventWriter.add(miscInfoEnd);
	}

	private void writeEditHistory(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IEditHistory editHistory) throws XMLStreamException {

		StartElement editHistoryStart = eventFactory.createStartElement("", "", IChromatogramTags.EDIT_HISTORY);
		EndElement editHistoryEnd = eventFactory.createEndElement("", "", IChromatogramTags.EDIT_HISTORY);
		StartElement editInformationStart = eventFactory.createStartElement("", "", IChromatogramTags.EDIT_INFORMATION);
		EndElement editInformationEnd = eventFactory.createEndElement("", "", IChromatogramTags.EDIT_INFORMATION);
		Attribute editDate;
		Characters description;
		/*
		 * Write all elements from the edit history.
		 */
		eventWriter.add(editHistoryStart);
		for(IEditInformation editInformation : editHistory) {
			editDate = eventFactory.createAttribute(IChromatogramTags.EDIT_DATE, editInformation.getDate().toString());
			description = eventFactory.createCData(editInformation.getDescription());
			eventWriter.add(editInformationStart);
			eventWriter.add(editDate);
			eventWriter.add(description);
			eventWriter.add(editInformationEnd);
		}
		eventWriter.add(editHistoryEnd);
	}

	/**
	 * Writes the scans.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param chromatogram
	 * @throws XMLStreamException
	 */
	private void writeChromatogramMassSpectra(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws XMLStreamException {

		IVendorMassSpectrum massSpectrum;
		monitor.subTask(IConstants.EXPORT_SCANS);
		/*
		 * scan element
		 */
		StartElement scansStart = eventFactory.createStartElement("", "", IChromatogramTags.SCANS);
		EndElement scansEnd = eventFactory.createEndElement("", "", IChromatogramTags.SCANS);
		eventWriter.add(scansStart);
		/*
		 * Write all scans.
		 */
		for(int scan = 1; scan <= chromatogram.getNumberOfScans(); scan++) {
			monitor.subTask(IConstants.SCAN + " " + scan);
			massSpectrum = chromatogram.getSupplierScan(scan);
			writeChromatogramMassSpectrum(eventWriter, eventFactory, massSpectrum);
		}
		eventWriter.add(scansEnd);
	}

	private void writeChromatogramMassSpectrum(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IVendorMassSpectrum massSpectrum) throws XMLStreamException {

		// mass spectrum element
		StartElement supplierMassSpectrumStart = eventFactory.createStartElement("", "", IChromatogramTags.SUPPLIER_MASS_SPECTRUM);
		EndElement supplierMassSpectrumEnd = eventFactory.createEndElement("", "", IChromatogramTags.SUPPLIER_MASS_SPECTRUM);
		// mass spectrum attributes
		Attribute retentionTime = eventFactory.createAttribute(IChromatogramTags.RETENTION_TIME, Integer.valueOf(massSpectrum.getRetentionTime()).toString());
		Attribute retentionIndex = eventFactory.createAttribute(IChromatogramTags.RETENTION_INDEX, Float.valueOf(massSpectrum.getRetentionIndex()).toString());
		Attribute totalIonSignal = eventFactory.createAttribute(IChromatogramTags.TOTAL_SIGNAL, Float.valueOf(massSpectrum.getTotalSignal()).toString());
		// ions element
		StartElement supplierIonsStart = eventFactory.createStartElement("", "", IChromatogramTags.SUPPLIER_IONS);
		EndElement supplierIonsEnd = eventFactory.createEndElement("", "", IChromatogramTags.SUPPLIER_IONS);
		// ions base64 encoded
		Characters ions;
		/*
		 * Write the structure.
		 */
		eventWriter.add(supplierMassSpectrumStart);
		eventWriter.add(retentionTime);
		eventWriter.add(retentionIndex);
		eventWriter.add(totalIonSignal);
		/*
		 * Write the ions.
		 */
		eventWriter.add(supplierIonsStart);
		ions = eventFactory.createCharacters(IonSupport.encodeIons(massSpectrum));
		eventWriter.add(ions);
		eventWriter.add(supplierIonsEnd);
		eventWriter.add(supplierMassSpectrumEnd);
	}
	// ------------------------------------------private methods
}
