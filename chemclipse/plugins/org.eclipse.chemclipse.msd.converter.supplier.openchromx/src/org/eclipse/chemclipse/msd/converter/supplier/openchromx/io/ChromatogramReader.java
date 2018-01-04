/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.filter.IdentifierEventFilter;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.filter.SupplierIonsEventFilter;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.filter.SupplierMassSpectrumEventFilter;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.support.DateSupport;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.support.IChromatogramTags;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.support.IConstants;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.support.IonSupport;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.model.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.openchromx.model.VendorScan;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.logging.core.Logger;

/**
 * @author eselmeister
 */
public class ChromatogramReader extends AbstractChromatogramMSDReader implements IChromatogramMSDReader {

	private static Logger logger = Logger.getLogger(ChromatogramReader.class);
	public static final String VALID_FILE_FORMAT_CHECK = "ChemClipseChromatogram";

	public ChromatogramReader() {
	}

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramMSD chromatogram = new VendorChromatogram();
		try {
			monitor.subTask(IConstants.IMPORT_CHROMATOGRAM);
			isValidFileFormat(file);
			readHeader(file, chromatogram);
			readMassSpectra(file, chromatogram, false, monitor);
		} catch(XMLStreamException e) {
			throw new IOException(e);
		}
		return chromatogram;
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramMSD chromatogram = new VendorChromatogram();
		try {
			monitor.subTask(IConstants.IMPORT_CHROMATOGRAM_OVERVIEW);
			isValidFileFormat(file);
			readHeader(file, chromatogram);
			readMassSpectra(file, chromatogram, true, monitor);
		} catch(XMLStreamException e) {
			throw new IOException(e);
		}
		return chromatogram;
	}

	// ------------------------------------------private methods
	/**
	 * Reads the header information.
	 * 
	 * @throws IOException
	 */
	private void readHeader(File file, IChromatogramMSD chromatogram) throws XMLStreamException, IOException {

		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory.newInstance().setProperty(XMLInputFactory.IS_COALESCING, true);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, IChromatogramTags.UTF8);
		XMLEvent event;
		String elementName;
		exitloop:
		while(eventReader.hasNext()) {
			event = eventReader.nextEvent();
			if(event.isStartElement()) {
				elementName = event.asStartElement().getName().getLocalPart();
				if(elementName.equals(IChromatogramTags.OPERATOR)) {
					addOperator(chromatogram, event, eventReader);
					continue;
				}
				if(elementName.equals(IChromatogramTags.DATE)) {
					addDate(chromatogram, event, eventReader);
					continue;
				}
				if(elementName.equals(IChromatogramTags.MISC_INFO)) {
					addMiscInfo(chromatogram, event, eventReader);
					continue;
				}
				if(elementName.equals(IChromatogramTags.EDIT_INFORMATION)) {
					addEditInformation(chromatogram, event, eventReader);
					continue;
				}
			} else {
				/*
				 * Check if it is the end of the header. If yes, break to not
				 * run through all elements of the xml document.
				 */
				if(event.isEndElement()) {
					elementName = event.asEndElement().getName().getLocalPart();
					if(elementName.equals(IChromatogramTags.HEADER)) {
						break exitloop;
					}
				}
			}
		}
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();
	}

	/**
	 * Adds the operator to the chromatogram.
	 * 
	 * @param chromatogram
	 * @param event
	 * @param eventReader
	 * @throws XMLStreamException
	 */
	private void addOperator(IChromatogramMSD chromatogram, XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {

		event = eventReader.nextEvent();
		chromatogram.setOperator(event.asCharacters().getData());
	}

	/**
	 * Adds the date to the chromatogram.
	 * 
	 * @param chromatogram
	 * @param event
	 * @param eventReader
	 * @throws XMLStreamException
	 */
	private void addDate(IChromatogramMSD chromatogram, XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {

		event = eventReader.nextEvent();
		Date date;
		try {
			date = DateSupport.getDate(event.asCharacters().getData());
		} catch(ParseException e) {
			date = new Date();
		}
		chromatogram.setDate(date);
	}

	/**
	 * Adds miscellaneous information to the chromatogram.
	 * 
	 * @param chromatogram
	 * @param event
	 * @param eventReader
	 * @throws XMLStreamException
	 */
	private void addMiscInfo(IChromatogramMSD chromatogram, XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {

		event = eventReader.nextEvent();
		chromatogram.setMiscInfo(event.asCharacters().getData());
	}

	/**
	 * Adds edit information to the chromatogram.
	 * 
	 * @param chromatogram
	 * @param event
	 * @param eventReader
	 * @throws XMLStreamException
	 */
	private void addEditInformation(IChromatogramMSD chromatogram, XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {

		Attribute dateAttribute = event.asStartElement().getAttributeByName(new QName(IChromatogramTags.EDIT_DATE));
		Date date;
		// TODO Date
		try {
			date = DateSupport.getDate(dateAttribute.getValue());
		} catch(ParseException e) {
			date = new Date();
		}
		event = eventReader.nextEvent();
		String description = event.asCharacters().getData();
		IEditInformation editInformation = new EditInformation(date, description);
		chromatogram.getEditHistory().add(editInformation);
	}

	@SuppressWarnings("unchecked")
	private void readMassSpectra(File file, IChromatogramMSD chromatogram, boolean overview, IProgressMonitor monitor) throws XMLStreamException, IOException {

		monitor.subTask(IConstants.PARSE_SCANS);
		// monitor.setTaskName("Import the chromatogram");
		int scan = 1;
		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, IChromatogramTags.UTF8);
		XMLEvent event;
		Attribute attribute;
		String attributeName;
		IVendorMassSpectrum supplierMassSpectrum;
		int retentionTime = 0;
		float retentionIndex = 0.0f;
		float totalSignal = 0.0f;
		EventFilter eventFilter = new SupplierMassSpectrumEventFilter();
		XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
		/*
		 * Read all mass spectra, create new supplier mass spectrum objects and
		 * store them in the chromatogram.
		 */
		while(filteredEventReader.hasNext()) {
			event = filteredEventReader.nextEvent();
			/*
			 * Monitor
			 */
			monitor.subTask(IConstants.SCAN + " " + scan++);
			supplierMassSpectrum = new VendorScan();
			Iterator<? extends Attribute> attributes = event.asStartElement().getAttributes();
			while(attributes.hasNext()) {
				attribute = attributes.next();
				attributeName = attribute.getName().getLocalPart();
				if(attributeName.equals(IChromatogramTags.RETENTION_TIME)) {
					retentionTime = Integer.valueOf(attribute.getValue());
				}
				if(attributeName.equals(IChromatogramTags.RETENTION_INDEX)) {
					retentionIndex = Float.valueOf(attribute.getValue());
				}
				if(attributeName.equals(IChromatogramTags.TOTAL_SIGNAL)) {
					totalSignal = Float.valueOf(attribute.getValue());
				}
			}
			supplierMassSpectrum.setRetentionTime(retentionTime);
			supplierMassSpectrum.setRetentionIndex(retentionIndex);
			if(overview) {
				addTotalSignal(supplierMassSpectrum, totalSignal);
			} else {
				readIons(inputFactory, eventReader, supplierMassSpectrum);
			}
			chromatogram.addScan(supplierMassSpectrum);
		}
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();
		/*
		 * Calculate the scanInterval and scanDelay.
		 */
		int scanInterval = chromatogram.getStopRetentionTime() / chromatogram.getNumberOfScans();
		/*
		 * Scan delay must be not negative.
		 */
		int scanDelay = chromatogram.getStartRetentionTime() - scanInterval;
		scanDelay = (scanDelay < 0) ? 0 : scanDelay;
		chromatogram.setScanInterval(scanInterval);
		chromatogram.setScanDelay(scanDelay);
		/*
		 * Extension Point
		 */
		chromatogram.setConverterId("org.eclipse.chemclipse.msd.converter.supplier.openchromx");
	}

	/**
	 * Adds a total signal mass spectrum to the chromatogram.<br/>
	 * Used for mass chromatogram overview.
	 * 
	 * @param supplierMassSpectrum
	 * @param totalSignal
	 */
	private void addTotalSignal(IVendorMassSpectrum supplierMassSpectrum, float totalSignal) {

		IScanIon ion;
		try {
			ion = new VendorIon(IIon.TIC_ION, totalSignal);
			supplierMassSpectrum.addIon(ion);
		} catch(AbundanceLimitExceededException e) {
			logger.warn(e);
		} catch(IonLimitExceededException e) {
			logger.warn(e);
		}
	}

	/**
	 * Reads the ions, builds a mass spectrum and adds it to the
	 * chromatogram.
	 * 
	 * @param inputFactory
	 * @param eventReader
	 * @param supplierMassSpectrum
	 * @throws XMLStreamException
	 */
	private void readIons(XMLInputFactory inputFactory, XMLEventReader eventReader, IVendorMassSpectrum supplierMassSpectrum) throws XMLStreamException {

		XMLEvent event;
		String encodedIons;
		EventFilter eventFilter = new SupplierIonsEventFilter();
		XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
		/*
		 * Do not use the while loop as there is only 1 element to retrieve.
		 */
		if(filteredEventReader.hasNext()) {
			event = filteredEventReader.nextEvent();
			event = eventReader.nextEvent();
			encodedIons = event.asCharacters().getData();
			/*
			 * Decode the ions and store them in supplier mass
			 * spectrum.
			 */
			IonSupport.decodeIons(supplierMassSpectrum, encodedIons);
		}
	}

	/**
	 * Check whether the chromatogram file can be parsed or not.<br/>
	 * Returns true if the chromatogram can be parsed and false if not.
	 * 
	 * @return boolean
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private boolean isValidFileFormat(File file) throws XMLStreamException, IOException {

		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, IChromatogramTags.UTF8);
		boolean result = false;
		XMLEvent event;
		String description;
		EventFilter eventFilter = new IdentifierEventFilter();
		XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
		if(filteredEventReader.hasNext()) {
			event = filteredEventReader.nextEvent();
			event = eventReader.nextEvent();
			description = event.asCharacters().getData();
			if(description.equals(IChromatogramTags.IDENTIFIER_ID)) {
				result = true;
			}
		}
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();
		return result;
	}
}
