/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.io;

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

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.internal.support.IBatchProcessJobTags;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.core.runtime.IProgressMonitor;

public class BatchProcessJobWriter {

	public void writeBatchProcessJob(File file, IBatchProcessJob batchProcessJob, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException, XMLStreamException {

		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
		XMLEventWriter eventWriter = xmlOutputFactory.createXMLEventWriter(bufferedOutputStream, IBatchProcessJobTags.UTF8);
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		/*
		 * Document
		 */
		eventWriter.add(eventFactory.createStartDocument());
		StartElement chromatogramStart = eventFactory.createStartElement("", "", IBatchProcessJobTags.BATCH_PROCESS_JOB);
		eventWriter.add(chromatogramStart);
		/*
		 * Write the header and the list informations.
		 */
		writeBatchProcessJobHeader(eventWriter, eventFactory, batchProcessJob);
		writeComment(eventWriter, eventFactory, "Load the following chromatograms.");
		writeChromatogramInputEntries(eventWriter, eventFactory, batchProcessJob.getChromatogramInputEntries());
		writeComment(eventWriter, eventFactory, "Process each chromatogram with the listed method entries.");
		writeChromatogramProcessEntries(eventWriter, eventFactory, batchProcessJob.getProcessMethod());
		/*
		 * Close the document
		 */
		EndElement chromatogramEnd = eventFactory.createEndElement("", "", IBatchProcessJobTags.BATCH_PROCESS_JOB);
		eventWriter.add(chromatogramEnd);
		eventWriter.add(eventFactory.createEndDocument());
		/*
		 * Close the streams
		 */
		bufferedOutputStream.flush();
		eventWriter.flush();
		bufferedOutputStream.close();
		eventWriter.close();
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
	private void writeBatchProcessJobHeader(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IBatchProcessJob batchProcessJob) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement headerStart = eventFactory.createStartElement("", "", IBatchProcessJobTags.HEADER);
		EndElement headerEnd = eventFactory.createEndElement("", "", IBatchProcessJobTags.HEADER);
		/*
		 * Write the elements.
		 */
		eventWriter.add(headerStart);
		eventWriter.add(headerEnd);
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
		StartElement entriesStart = eventFactory.createStartElement("", "", IBatchProcessJobTags.CHROMATOGRAM_INPUT_ENTRIES);
		EndElement entriesEnd = eventFactory.createEndElement("", "", IBatchProcessJobTags.CHROMATOGRAM_INPUT_ENTRIES);
		/*
		 * Write the elements.
		 */
		eventWriter.add(entriesStart);
		for(IChromatogramInputEntry inputEntry : inputEntries) {
			writeChromatogramInputEntry(eventWriter, eventFactory, inputEntry);
		}
		eventWriter.add(entriesEnd);
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
		StartElement entryStart = eventFactory.createStartElement("", "", IBatchProcessJobTags.CHROMATOGRAM_INPUT_ENTRY);
		EndElement entryEnd = eventFactory.createEndElement("", "", IBatchProcessJobTags.CHROMATOGRAM_INPUT_ENTRY);
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
	private void writeChromatogramProcessEntries(XMLEventWriter eventWriter, XMLEventFactory eventFactory, ProcessMethod processMethod) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entriesStart = eventFactory.createStartElement("", "", IBatchProcessJobTags.CHROMATOGRAM_PROCESS_ENTRIES);
		EndElement entriesEnd = eventFactory.createEndElement("", "", IBatchProcessJobTags.CHROMATOGRAM_PROCESS_ENTRIES);
		/*
		 * Write the elements.
		 */
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

		/*
		 * Element and content definition.
		 */
		StartElement entryStart = eventFactory.createStartElement("", "", IBatchProcessJobTags.CHROMATOGRAM_PROCESS_ENTRY);
		EndElement entryEnd = eventFactory.createEndElement("", "", IBatchProcessJobTags.CHROMATOGRAM_PROCESS_ENTRY);
		/*
		 * Attributes and values.
		 */
		// processEntry.getDescription();
		// processEntry.getJsonSettings();
		// processEntry.getName();
		// processEntry.getProcessorId();
		// processEntry.getProcessSettingsClass();
		// processEntry.getSupportedDataTypes();
		/*
		 * Write the elements.
		 */
		eventWriter.add(entryStart);
		eventWriter.add(eventFactory.createAttribute(IBatchProcessJobTags.PROCESSOR_ID, processEntry.getProcessorId()));
		eventWriter.add(entryEnd);
	}
}
