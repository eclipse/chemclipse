/*******************************************************************************
 * Copyright (c) 2010, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.internal.support.JobTags;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.core.runtime.IProgressMonitor;

public class JobWriter {

	public void writeBatchProcessJob(File file, BatchProcessJob batchProcessJob, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException, XMLStreamException {

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
			StartElement chromatogramStart = eventFactory.createStartElement("", "", JobTags.BATCH_PROCESS_JOB);
			eventWriter.add(chromatogramStart);
			/*
			 * Write the header and the list informations.
			 */
			writeBatchProcessJobHeader(eventWriter, eventFactory, batchProcessJob);
			writeComment(eventWriter, eventFactory, "Data Types");
			writeDataTypes(eventWriter, eventFactory, batchProcessJob.getDataTypeEntries());
			writeComment(eventWriter, eventFactory, "Chromatograms");
			writeChromatogramInputEntries(eventWriter, eventFactory, batchProcessJob.getChromatogramInputEntries());
			writeComment(eventWriter, eventFactory, "Process Entries");
			writeChromatogramProcessEntries(eventWriter, eventFactory, batchProcessJob.getProcessMethod());
			/*
			 * Close the document
			 */
			EndElement chromatogramEnd = eventFactory.createEndElement("", "", JobTags.BATCH_PROCESS_JOB);
			eventWriter.add(chromatogramEnd);
			eventWriter.add(eventFactory.createEndDocument());
			//
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
	private void writeBatchProcessJobHeader(XMLEventWriter eventWriter, XMLEventFactory eventFactory, BatchProcessJob batchProcessJob) throws XMLStreamException {

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
	 * Writes the chromatogram input entries.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param inputEntries
	 * @throws XMLStreamException
	 */
	private void writeChromatogramInputEntries(XMLEventWriter eventWriter, XMLEventFactory eventFactory, List<IChromatogramInputEntry> inputEntries) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entriesStart = eventFactory.createStartElement("", "", JobTags.CHROMATOGRAM_INPUT_ENTRIES);
		EndElement entriesEnd = eventFactory.createEndElement("", "", JobTags.CHROMATOGRAM_INPUT_ENTRIES);
		/*
		 * Write the elements.
		 */
		eventWriter.add(entriesStart);
		for(IChromatogramInputEntry inputEntry : inputEntries) {
			writeChromatogramInputEntry(eventWriter, eventFactory, inputEntry);
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
	 * Writes the chromatogram input entry.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param inputEntry
	 * @throws XMLStreamException
	 */
	private void writeChromatogramInputEntry(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IChromatogramInputEntry inputEntry) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entryStart = eventFactory.createStartElement("", "", JobTags.CHROMATOGRAM_INPUT_ENTRY);
		EndElement entryEnd = eventFactory.createEndElement("", "", JobTags.CHROMATOGRAM_INPUT_ENTRY);
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
	 * Writes the chromatogram process entries.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param processEntries
	 * @throws XMLStreamException
	 */
	private void writeChromatogramProcessEntries(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IProcessMethod processMethod) throws XMLStreamException {

		StartElement entriesStart = eventFactory.createStartElement("", "", JobTags.CHROMATOGRAM_PROCESS_ENTRIES);
		EndElement entriesEnd = eventFactory.createEndElement("", "", JobTags.CHROMATOGRAM_PROCESS_ENTRIES);
		//
		eventWriter.add(entriesStart);
		for(IProcessEntry processEntry : processMethod) {
			writeChromatogramProcessEntry(eventWriter, eventFactory, processEntry);
		}
		eventWriter.add(entriesEnd);
	}

	/**
	 * Writes the chromatogram process entry.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param processEntry
	 * @throws XMLStreamException
	 */
	private void writeChromatogramProcessEntry(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IProcessEntry processEntry) throws XMLStreamException {

		StartElement entryStart = eventFactory.createStartElement("", "", JobTags.CHROMATOGRAM_PROCESS_ENTRY);
		EndElement entryEnd = eventFactory.createEndElement("", "", JobTags.CHROMATOGRAM_PROCESS_ENTRY);
		//
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