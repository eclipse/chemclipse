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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.process.supplier.batchprocess.core.BatchProcessJob;
import org.eclipse.chemclipse.process.supplier.batchprocess.internal.JobTags;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;

public class JobWriter {

	public void writeBatchProcessJob(File file, BatchProcessJob batchProcessJob) throws IOException, XMLStreamException {

		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
			/*
			 * Writer
			 */
			XMLEventWriter eventWriter = xmlOutputFactory.createXMLEventWriter(bufferedOutputStream, JobTags.UTF8);
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			/*
			 * Document
			 */
			eventWriter.add(eventFactory.createStartDocument());
			StartElement batchJobStart = eventFactory.createStartElement("", "", JobTags.BATCH_PROCESS_JOB);
			eventWriter.add(batchJobStart);
			/*
			 * Write the header and the list informations.
			 */
			writeBatchProcessJobHeader(eventWriter, eventFactory);
			writeComment(eventWriter, eventFactory, "Data Types");
			writeDataTypes(eventWriter, eventFactory, List.of(batchProcessJob.getDataType()));
			writeComment(eventWriter, eventFactory, "Input Files");
			writeInputEntries(eventWriter, eventFactory, batchProcessJob.getBatchProcessInputEntries());
			writeComment(eventWriter, eventFactory, "Process Entries");
			writeProcessEntries(eventWriter, eventFactory, batchProcessJob.getProcessMethod());
			/*
			 * Close the document
			 */
			EndElement batchJobEnd = eventFactory.createEndElement("", "", JobTags.BATCH_PROCESS_JOB);
			eventWriter.add(batchJobEnd);
			eventWriter.add(eventFactory.createEndDocument());

			bufferedOutputStream.flush();
			eventWriter.flush();
			eventWriter.close();
		}
	}

	/**
	 * Writes the comment.
	 *
	 * @param eventWriter
	 * @param eventFactory
	 * @throws XMLStreamException
	 */
	private void writeComment(XMLEventWriter eventWriter, XMLEventFactory eventFactory, String comment) throws XMLStreamException {

		/*
		 * Comment
		 */
		Comment batchJobInfo = eventFactory.createComment(comment);
		eventWriter.add(batchJobInfo);
	}

	/**
	 * Writes the header.
	 *
	 * @param eventWriter
	 * @param eventFactory
	 * @param batchProcessJob
	 * @throws XMLStreamException
	 */
	private void writeBatchProcessJobHeader(XMLEventWriter eventWriter, XMLEventFactory eventFactory) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement headerStart = eventFactory.createStartElement("", "", JobTags.HEADER);
		EndElement headerEnd = eventFactory.createEndElement("", "", JobTags.HEADER);
		/*
		 * Write the elements.
		 */
		eventWriter.add(headerStart);
		eventWriter.add(headerEnd);
	}

	private void writeDataTypes(XMLEventWriter eventWriter, XMLEventFactory eventFactory, List<DataType> dataTypes) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entriesStart = eventFactory.createStartElement("", "", JobTags.DATA_TYPE_ENTRIES);
		EndElement entriesEnd = eventFactory.createEndElement("", "", JobTags.DATA_TYPE_ENTRIES);
		/*
		 * Write the elements.
		 */
		eventWriter.add(entriesStart);
		for(DataType dataType : dataTypes) {
			writeDataType(eventWriter, eventFactory, dataType);
		}
		eventWriter.add(entriesEnd);
	}

	/**
	 * Writes the input entries.
	 *
	 * @param eventWriter
	 * @param eventFactory
	 * @param inputEntries
	 * @throws XMLStreamException
	 */
	private void writeInputEntries(XMLEventWriter eventWriter, XMLEventFactory eventFactory, List<IBatchProcessInputEntry> inputEntries) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entriesStart = eventFactory.createStartElement("", "", JobTags.INPUT_ENTRIES);
		EndElement entriesEnd = eventFactory.createEndElement("", "", JobTags.INPUT_ENTRIES);
		/*
		 * Write the elements.
		 */
		eventWriter.add(entriesStart);
		for(IBatchProcessInputEntry inputEntry : inputEntries) {
			writeInputEntry(eventWriter, eventFactory, inputEntry);
		}
		eventWriter.add(entriesEnd);
	}

	private void writeDataType(XMLEventWriter eventWriter, XMLEventFactory eventFactory, DataType dataType) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entryStart = eventFactory.createStartElement("", "", JobTags.DATA_TYPE_ENTRY);
		EndElement entryEnd = eventFactory.createEndElement("", "", JobTags.DATA_TYPE_ENTRY);
		/*
		 * Values.
		 */
		Characters inputFile = eventFactory.createCData(dataType.name());
		/*
		 * Write the elements.
		 */
		eventWriter.add(entryStart);
		eventWriter.add(inputFile);
		eventWriter.add(entryEnd);
	}

	/**
	 * Writes the input entry.
	 *
	 * @param eventWriter
	 * @param eventFactory
	 * @param inputEntry
	 * @throws XMLStreamException
	 */
	private void writeInputEntry(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IBatchProcessInputEntry inputEntry) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entryStart = eventFactory.createStartElement("", "", JobTags.INPUT_ENTRY);
		EndElement entryEnd = eventFactory.createEndElement("", "", JobTags.INPUT_ENTRY);
		/*
		 * Values.
		 */
		Characters inputFile = eventFactory.createCData(inputEntry.getInputFile());
		/*
		 * Write the elements.
		 */
		eventWriter.add(entryStart);
		eventWriter.add(inputFile);
		eventWriter.add(entryEnd);
	}

	/**
	 * Writes the process entries.
	 *
	 * @param eventWriter
	 * @param eventFactory
	 * @param processEntries
	 * @throws XMLStreamException
	 */
	private void writeProcessEntries(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IProcessMethod processMethod) throws XMLStreamException {

		StartElement entriesStart = eventFactory.createStartElement("", "", JobTags.CHROMATOGRAM_PROCESS_ENTRIES);
		EndElement entriesEnd = eventFactory.createEndElement("", "", JobTags.CHROMATOGRAM_PROCESS_ENTRIES);

		eventWriter.add(entriesStart);
		for(IProcessEntry processEntry : processMethod) {
			writeProcessEntry(eventWriter, eventFactory, processEntry);
		}
		eventWriter.add(entriesEnd);
	}

	/**
	 * Writes the process entry.
	 *
	 * @param eventWriter
	 * @param eventFactory
	 * @param processEntry
	 * @throws XMLStreamException
	 */
	private void writeProcessEntry(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IProcessEntry processEntry) throws XMLStreamException {

		StartElement entryStart = eventFactory.createStartElement("", "", JobTags.CHROMATOGRAM_PROCESS_ENTRY);
		EndElement entryEnd = eventFactory.createEndElement("", "", JobTags.CHROMATOGRAM_PROCESS_ENTRY);

		eventWriter.add(entryStart);
		eventWriter.add(eventFactory.createAttribute(JobTags.PROCESSOR_ID, processEntry.getProcessorId()));
		eventWriter.add(eventFactory.createAttribute(JobTags.PROCESSOR_NAME, processEntry.getName()));
		eventWriter.add(eventFactory.createAttribute(JobTags.PROCESSOR_DESCRIPTION, processEntry.getDescription()));
		if(processEntry.getSettings() != null) {
			eventWriter.add(eventFactory.createAttribute(JobTags.PROCESSOR_JSON_SETTINGS, processEntry.getSettings()));
		}
		// not used, only for backward compat
		eventWriter.add(eventFactory.createAttribute(JobTags.PROCESSOR_SYMBOLIC_NAME, ""));
		eventWriter.add(eventFactory.createAttribute(JobTags.PROCESSOR_CLASS_NAME, ""));
		eventWriter.add(eventFactory.createAttribute(JobTags.PROCESSOR_DATA_TYPES, ""));
		eventWriter.add(entryEnd);
	}
}
