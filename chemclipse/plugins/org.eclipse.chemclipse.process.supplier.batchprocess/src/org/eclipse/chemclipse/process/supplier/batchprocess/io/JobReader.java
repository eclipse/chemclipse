/*******************************************************************************
 * Copyright (c) 2010, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.process.supplier.batchprocess.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.process.supplier.batchprocess.core.BatchProcessJob;
import org.eclipse.chemclipse.process.supplier.batchprocess.internal.JobTags;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.ProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;

public class JobReader {

	private static final Logger logger = Logger.getLogger(JobReader.class);

	public BatchProcessJob read(File file) throws IOException {

		try {
			readHeader(file);
			/*
			 * The data type decides which processors the method may contain,
			 * hence it is read before the process method is created.
			 */
			DataType dataType = readDataType(file);
			ProcessMethod processMethod = new ProcessMethod(getDataCategories(dataType));
			BatchProcessJob batchProcessJob = new BatchProcessJob(processMethod);
			batchProcessJob.setDataType(dataType);
			readInputEntries(file, batchProcessJob);
			readProcessEntries(file, processMethod);
			return batchProcessJob;
		} catch(XMLStreamException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Returns the categories the process method of the given data type may contain.
	 *
	 * @param dataType
	 * @return the matching {@link DataCategory} set
	 */
	private static Set<DataCategory> getDataCategories(DataType dataType) {

		DataCategory dataCategory = dataType.toDataCategory();
		if(ProcessMethod.CHROMATOGRAPHY.contains(dataCategory)) {
			return ProcessMethod.CHROMATOGRAPHY;
		} else if(ProcessMethod.MS.contains(dataCategory)) {
			return ProcessMethod.MS;
		} else if(ProcessMethod.NMR.contains(dataCategory)) {
			return ProcessMethod.NMR;
		}

		return Set.of(dataCategory);
	}

	/**
	 * Reads the header information.
	 *
	 * @throws IOException
	 */
	private void readHeader(File file) throws XMLStreamException, IOException {

		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory.newInstance().setProperty(XMLInputFactory.IS_COALESCING, true);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
			eventReader = inputFactory.createXMLEventReader(bufferedInputStream, JobTags.UTF8);
			XMLEvent event;
			String elementName;
			exitloop:
			while(eventReader.hasNext()) {
				event = eventReader.nextEvent();
				if(event.isStartElement()) {
					elementName = event.asStartElement().getName().getLocalPart();
					/*
					 * Read the header elements. There are none to read actually.
					 */
				} else {
					/*
					 * Check if it is the end of the header. If yes, break to not
					 * run through all elements of the xml document.
					 */
					if(event.isEndElement()) {
						elementName = event.asEndElement().getName().getLocalPart();
						if(elementName.equals(JobTags.HEADER)) {
							break exitloop;
						}
					}
				}
			}
			/*
			 * Close the stream.
			 */
			eventReader.close();
		}
	}

	private DataType readDataType(File file) throws XMLStreamException, IOException {

		DataType dataType = BatchProcessJob.DATA_TYPE_DEFAULT;
		XMLEvent event;
		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, JobTags.UTF8);
		/*
		 * Use event filters.
		 */
		List<String> acceptedElements = new ArrayList<>();
		acceptedElements.add(JobTags.DATA_TYPE_ENTRY);
		EventFilter eventFilter = new BatchProcessJobEventFilter(acceptedElements);
		XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
		/*
		 * Read all entries.
		 */
		while(filteredEventReader.hasNext()) {
			filteredEventReader.nextEvent();
			event = eventReader.nextEvent();
			try {
				dataType = DataType.valueOf(event.asCharacters().getData(), DataType.AUTO_DETECT);
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();

		return dataType;
	}

	/**
	 * Read the input entries.
	 *
	 * @param file
	 * @param batchProcessJob
	 * @param monitor
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private void readInputEntries(File file, BatchProcessJob batchProcessJob) throws XMLStreamException, IOException {

		IBatchProcessInputEntry inputEntry;
		XMLEvent event;
		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, JobTags.UTF8);
		/*
		 * Use event filters.
		 */
		List<String> acceptedElements = new ArrayList<>();
		acceptedElements.add(JobTags.INPUT_ENTRY);
		EventFilter eventFilter = new BatchProcessJobEventFilter(acceptedElements);
		XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
		/*
		 * Read all entries.
		 */
		while(filteredEventReader.hasNext()) {
			filteredEventReader.nextEvent();
			event = eventReader.nextEvent();
			inputEntry = new BatchInputEntry(event.asCharacters().getData());
			batchProcessJob.getBatchProcessInputEntries().add(inputEntry);
		}
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();
	}

	private void readProcessEntries(File file, ProcessMethod method) throws XMLStreamException, IOException {

		XMLEvent event;
		Attribute attribute;
		String attributeName;

		String id = "";
		String name = "";
		String description = "";
		String jsonSettings = "{}";
		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, JobTags.UTF8);
		/*
		 * Use event filters.
		 */
		List<String> acceptedElements = new ArrayList<>();
		acceptedElements.add(JobTags.CHROMATOGRAM_PROCESS_ENTRY);
		EventFilter eventFilter = new BatchProcessJobEventFilter(acceptedElements);
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

				if(attributeName.equals(JobTags.PROCESSOR_ID)) {
					id = attribute.getValue();
				} else if(attributeName.equals(JobTags.PROCESSOR_NAME)) {
					name = attribute.getValue();
				} else if(attributeName.equals(JobTags.PROCESSOR_DESCRIPTION)) {
					description = attribute.getValue();
				} else if(attributeName.equals(JobTags.PROCESSOR_JSON_SETTINGS)) {
					jsonSettings = attribute.getValue();
				}
			}
			/*
			 * Create
			 */
			ProcessEntry processEntry = new ProcessEntry(method);
			processEntry.setProcessorId(id);
			processEntry.setName(name);
			processEntry.setDescription(description);
			processEntry.setSettings(jsonSettings);
			processEntry.setActiveProfile(method.getActiveProfile());
			method.getEntries().add(processEntry);
		}
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();
	}
}
