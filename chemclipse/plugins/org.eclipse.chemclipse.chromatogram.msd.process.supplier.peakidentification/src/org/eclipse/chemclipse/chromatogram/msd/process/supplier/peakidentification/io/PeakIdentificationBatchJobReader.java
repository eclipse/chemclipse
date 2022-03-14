/*******************************************************************************
 * Copyright (c) 2011, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.internal.filter.PeakIdentificationBatchJobEventFilter;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.internal.support.IPeakIdentificationBatchJobTags;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakInputEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIntegrationEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakOutputEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.PeakIdentificationBatchJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.PeakIdentificationEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.PeakInputEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.PeakIntegrationEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.PeakOutputEntry;
import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakIdentificationBatchJobReader implements IPeakIdentificationBatchJobReader {

	@Override
	public IPeakIdentificationBatchJob read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IPeakIdentificationBatchJob batchJob = new PeakIdentificationBatchJob(file.getName());
		try {
			readHeader(file, batchJob);
			readPeakInputEntries(file, batchJob, monitor);
			readPeakIntegrationEntry(file, batchJob, monitor);
			readPeakIdentificationEntry(file, batchJob, monitor);
			readPeakOutputEntries(file, batchJob, monitor);
		} catch(XMLStreamException e) {
			throw new IOException(e);
		}
		return batchJob;
	}

	/**
	 * Reads the header information.
	 * 
	 * @throws IOException
	 */
	private void readHeader(File file, IPeakIdentificationBatchJob batchJob) throws XMLStreamException, IOException {

		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory.newInstance().setProperty(XMLInputFactory.IS_COALESCING, true);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, IPeakIdentificationBatchJobTags.UTF8);
		XMLEvent event;
		String elementName;
		exitloop:
		while(eventReader.hasNext()) {
			event = eventReader.nextEvent();
			if(event.isStartElement()) {
				elementName = event.asStartElement().getName().getLocalPart();
				if(elementName.equals(IPeakIdentificationBatchJobTags.REPORT_FOLDER)) {
					setReportFolder(batchJob, event, eventReader);
					continue;
				}
				if(elementName.equals(IPeakIdentificationBatchJobTags.OVERRIDE_REPORT)) {
					setOverrideReport(batchJob, event, eventReader);
					continue;
				}
			} else {
				/*
				 * Check if it is the end of the header. If yes, break to not
				 * run through all elements of the xml document.
				 */
				if(event.isEndElement()) {
					elementName = event.asEndElement().getName().getLocalPart();
					if(elementName.equals(IPeakIdentificationBatchJobTags.HEADER)) {
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
	 * Adds the report folder to the batch job.
	 * 
	 * @param batchProcessJob
	 * @param event
	 * @param eventReader
	 * @throws XMLStreamException
	 */
	private void setReportFolder(IPeakIdentificationBatchJob batchJob, XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {

		event = eventReader.nextEvent();
		batchJob.setReportFolder(event.asCharacters().getData());
	}

	/**
	 * Override the reports.
	 * 
	 * @param batchProcessJob
	 * @param event
	 * @param eventReader
	 * @throws XMLStreamException
	 */
	private void setOverrideReport(IPeakIdentificationBatchJob batchJob, XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {

		event = eventReader.nextEvent();
		batchJob.setOverrideReport(Boolean.parseBoolean(event.asCharacters().getData()));
	}

	/**
	 * Read the peak input entries.
	 * 
	 * @param file
	 * @param batchProcessJob
	 * @param monitor
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private void readPeakInputEntries(File file, IPeakIdentificationBatchJob batchJob, IProgressMonitor monitor) throws XMLStreamException, IOException {

		IPeakInputEntry inputEntry;
		XMLEvent event;
		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, IPeakIdentificationBatchJobTags.UTF8);
		/*
		 * Use event filters.
		 */
		List<String> acceptedElements = new ArrayList<String>();
		acceptedElements.add(IPeakIdentificationBatchJobTags.PEAK_INPUT_ENTRY);
		EventFilter eventFilter = new PeakIdentificationBatchJobEventFilter(acceptedElements);
		XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
		/*
		 * Read all entries.
		 */
		while(filteredEventReader.hasNext()) {
			event = filteredEventReader.nextEvent();
			event = eventReader.nextEvent();
			inputEntry = new PeakInputEntry(event.asCharacters().getData());
			batchJob.getPeakInputEntries().add(inputEntry);
		}
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();
	}

	private void readPeakIntegrationEntry(File file, IPeakIdentificationBatchJob batchJob, IProgressMonitor monitor) throws XMLStreamException, IOException {

		String processorId = "";
		boolean processReport = false;
		XMLEvent event;
		Attribute attribute;
		String attributeName;
		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, IPeakIdentificationBatchJobTags.UTF8);
		/*
		 * Use event filters.
		 */
		List<String> acceptedElements = new ArrayList<String>();
		acceptedElements.add(IPeakIdentificationBatchJobTags.PEAK_INTEGRATION_ENTRY);
		EventFilter eventFilter = new PeakIdentificationBatchJobEventFilter(acceptedElements);
		XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
		/*
		 * Read the entry.
		 */
		event = filteredEventReader.nextEvent();
		Iterator<? extends Attribute> attributes = event.asStartElement().getAttributes();
		while(attributes.hasNext()) {
			attribute = attributes.next();
			attributeName = attribute.getName().getLocalPart();
			// Processor id
			if(attributeName.equals(IPeakIdentificationBatchJobTags.PROCESSOR_ID)) {
				processorId = attribute.getValue();
			}
			// Report
			if(attributeName.equals(IPeakIdentificationBatchJobTags.PROCESS_REPORT)) {
				processReport = Boolean.parseBoolean(attribute.getValue());
			}
		}
		IPeakIntegrationEntry peakIntegrationEntry = new PeakIntegrationEntry(processorId, processReport);
		batchJob.setPeakIntegrationEntry(peakIntegrationEntry);
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();
	}

	private void readPeakIdentificationEntry(File file, IPeakIdentificationBatchJob batchJob, IProgressMonitor monitor) throws XMLStreamException, IOException {

		String processorId = "";
		boolean processReport = false;
		XMLEvent event;
		Attribute attribute;
		String attributeName;
		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, IPeakIdentificationBatchJobTags.UTF8);
		/*
		 * Use event filters.
		 */
		List<String> acceptedElements = new ArrayList<String>();
		acceptedElements.add(IPeakIdentificationBatchJobTags.PEAK_IDENTIFICATION_ENTRY);
		EventFilter eventFilter = new PeakIdentificationBatchJobEventFilter(acceptedElements);
		XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
		/*
		 * Read the entry.
		 */
		event = filteredEventReader.nextEvent();
		Iterator<? extends Attribute> attributes = event.asStartElement().getAttributes();
		while(attributes.hasNext()) {
			attribute = attributes.next();
			attributeName = attribute.getName().getLocalPart();
			// Processor id
			if(attributeName.equals(IPeakIdentificationBatchJobTags.PROCESSOR_ID)) {
				processorId = attribute.getValue();
			}
			// Report
			if(attributeName.equals(IPeakIdentificationBatchJobTags.PROCESS_REPORT)) {
				processReport = Boolean.parseBoolean(attribute.getValue());
			}
		}
		IPeakIdentificationEntry peakIdentificationEntry = new PeakIdentificationEntry(processorId, processReport);
		batchJob.setPeakIdentificationEntry(peakIdentificationEntry);
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();
	}

	/**
	 * Read the peak output entries.
	 * 
	 * @param file
	 * @param batchProcessJob
	 * @param monitor
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private void readPeakOutputEntries(File file, IPeakIdentificationBatchJob batchJob, IProgressMonitor monitor) throws XMLStreamException, IOException {

		IPeakOutputEntry outputEntry;
		String outputFile;
		String converterId = "";
		XMLEvent event;
		Attribute attribute;
		String attributeName;
		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, IPeakIdentificationBatchJobTags.UTF8);
		/*
		 * Use event filters.
		 */
		List<String> acceptedElements = new ArrayList<String>();
		acceptedElements.add(IPeakIdentificationBatchJobTags.PEAK_OUTPUT_ENTRY);
		EventFilter eventFilter = new PeakIdentificationBatchJobEventFilter(acceptedElements);
		XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
		/*
		 * Read all entries.
		 */
		while(filteredEventReader.hasNext()) {
			event = filteredEventReader.nextEvent();
			Iterator<? extends Attribute> attributes = event.asStartElement().getAttributes();
			while(attributes.hasNext()) {
				attribute = attributes.next();
				attributeName = attribute.getName().getLocalPart();
				// Get the converter id.
				if(attributeName.equals(IPeakIdentificationBatchJobTags.PEAK_CONVERTER_ID)) {
					converterId = attribute.getValue();
				}
			}
			// Get the output file.
			event = eventReader.nextEvent();
			outputFile = event.asCharacters().getData();
			outputEntry = new PeakOutputEntry(outputFile, converterId);
			batchJob.getPeakOutputEntries().add(outputEntry);
		}
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();
	}
}
