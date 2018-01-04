/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
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
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.internal.support.IPeakIdentificationBatchJobTags;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakInputEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakOutputEntry;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakProcessEntry;

public class PeakIdentificationBatchJobWriter implements IPeakIdentificationBatchJobWriter {

	@Override
	public void writeBatchProcessJob(File file, IPeakIdentificationBatchJob peakIdentificationBatchJob, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException, XMLStreamException {

		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
		XMLEventWriter eventWriter = xmlOutputFactory.createXMLEventWriter(bufferedOutputStream, IPeakIdentificationBatchJobTags.UTF8);
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		/*
		 * Document
		 */
		eventWriter.add(eventFactory.createStartDocument());
		StartElement chromatogramStart = eventFactory.createStartElement("", "", IPeakIdentificationBatchJobTags.PEAK_IDENTIFICATION_BATCH_JOB);
		eventWriter.add(chromatogramStart);
		/*
		 * Write the header and the list informations.
		 */
		writeBatchProcessJobHeader(eventWriter, eventFactory, peakIdentificationBatchJob);
		writeComment(eventWriter, eventFactory, "Load the following peaks.");
		writePeakInputEntries(eventWriter, eventFactory, peakIdentificationBatchJob.getPeakInputEntries());
		writeComment(eventWriter, eventFactory, "Use the following peak integrator.");
		writePeakIntegratorEntry(eventWriter, eventFactory, peakIdentificationBatchJob.getPeakIntegrationEntry());
		writeComment(eventWriter, eventFactory, "Use the following peak identifier.");
		writePeakIdentificationEntry(eventWriter, eventFactory, peakIdentificationBatchJob.getPeakIdentificationEntry());
		writeComment(eventWriter, eventFactory, "Export the peaks to the following formats.");
		writePeakOutputEntries(eventWriter, eventFactory, peakIdentificationBatchJob.getPeakOutputEntries());
		EndElement chromatogramEnd = eventFactory.createEndElement("", "", IPeakIdentificationBatchJobTags.PEAK_IDENTIFICATION_BATCH_JOB);
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
	private void writeBatchProcessJobHeader(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IPeakIdentificationBatchJob peakIdentificationBatchJob) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement headerStart = eventFactory.createStartElement("", "", IPeakIdentificationBatchJobTags.HEADER);
		EndElement headerEnd = eventFactory.createEndElement("", "", IPeakIdentificationBatchJobTags.HEADER);
		/*
		 * Write the elements.
		 */
		eventWriter.add(headerStart);
		writeReportFolder(eventWriter, eventFactory, peakIdentificationBatchJob.getReportFolder());
		writeOverrideReport(eventWriter, eventFactory, peakIdentificationBatchJob.isOverrideReport());
		eventWriter.add(headerEnd);
	}

	/**
	 * Writes the report folder.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param batchProcessJob
	 * @throws XMLStreamException
	 */
	private void writeReportFolder(XMLEventWriter eventWriter, XMLEventFactory eventFactory, String reportFolder) throws XMLStreamException {

		StartElement operatorStart = eventFactory.createStartElement("", "", IPeakIdentificationBatchJobTags.REPORT_FOLDER);
		EndElement operatorEnd = eventFactory.createEndElement("", "", IPeakIdentificationBatchJobTags.REPORT_FOLDER);
		/*
		 * CData needs a valid element.
		 */
		if(reportFolder == null || reportFolder.equals("")) {
			reportFolder = "";
		}
		Characters operator = eventFactory.createCData(reportFolder);
		eventWriter.add(operatorStart);
		eventWriter.add(operator);
		eventWriter.add(operatorEnd);
	}

	/**
	 * Override the reports?
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param overrideReports
	 * @throws XMLStreamException
	 */
	private void writeOverrideReport(XMLEventWriter eventWriter, XMLEventFactory eventFactory, boolean overrideReports) throws XMLStreamException {

		StartElement operatorStart = eventFactory.createStartElement("", "", IPeakIdentificationBatchJobTags.OVERRIDE_REPORT);
		EndElement operatorEnd = eventFactory.createEndElement("", "", IPeakIdentificationBatchJobTags.OVERRIDE_REPORT);
		Characters operator = eventFactory.createCData(Boolean.toString(overrideReports));
		eventWriter.add(operatorStart);
		eventWriter.add(operator);
		eventWriter.add(operatorEnd);
	}

	/**
	 * Writes the peak input entries.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param inputEntries
	 * @throws XMLStreamException
	 */
	private void writePeakInputEntries(XMLEventWriter eventWriter, XMLEventFactory eventFactory, List<IPeakInputEntry> inputEntries) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entriesStart = eventFactory.createStartElement("", "", IPeakIdentificationBatchJobTags.PEAK_INPUT_ENTRIES);
		EndElement entriesEnd = eventFactory.createEndElement("", "", IPeakIdentificationBatchJobTags.PEAK_INPUT_ENTRIES);
		/*
		 * Write the elements.
		 */
		eventWriter.add(entriesStart);
		for(IPeakInputEntry inputEntry : inputEntries) {
			writePeakInputEntry(eventWriter, eventFactory, inputEntry);
		}
		eventWriter.add(entriesEnd);
	}

	/**
	 * Writes the peak input entry.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param inputEntry
	 * @throws XMLStreamException
	 */
	private void writePeakInputEntry(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IPeakInputEntry inputEntry) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entryStart = eventFactory.createStartElement("", "", IPeakIdentificationBatchJobTags.PEAK_INPUT_ENTRY);
		EndElement entryEnd = eventFactory.createEndElement("", "", IPeakIdentificationBatchJobTags.PEAK_INPUT_ENTRY);
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
	 * Writes the peak output entries.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param inputEntries
	 * @throws XMLStreamException
	 */
	private void writePeakOutputEntries(XMLEventWriter eventWriter, XMLEventFactory eventFactory, List<IPeakOutputEntry> outputEntries) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entriesStart = eventFactory.createStartElement("", "", IPeakIdentificationBatchJobTags.PEAK_OUTPUT_ENTRIES);
		EndElement entriesEnd = eventFactory.createEndElement("", "", IPeakIdentificationBatchJobTags.PEAK_OUTPUT_ENTRIES);
		/*
		 * Write the elements.
		 */
		eventWriter.add(entriesStart);
		for(IPeakOutputEntry inputEntry : outputEntries) {
			writePeakOutputEntry(eventWriter, eventFactory, inputEntry);
		}
		eventWriter.add(entriesEnd);
	}

	/**
	 * Writes the peak output entry.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param inputEntry
	 * @throws XMLStreamException
	 */
	private void writePeakOutputEntry(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IPeakOutputEntry outputEntry) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entryStart = eventFactory.createStartElement("", "", IPeakIdentificationBatchJobTags.PEAK_OUTPUT_ENTRY);
		EndElement entryEnd = eventFactory.createEndElement("", "", IPeakIdentificationBatchJobTags.PEAK_OUTPUT_ENTRY);
		/*
		 * Attributes and Values.
		 */
		Attribute converterId = eventFactory.createAttribute(IPeakIdentificationBatchJobTags.PEAK_CONVERTER_ID, outputEntry.getConverterId());
		Characters outputFolder = eventFactory.createCData(outputEntry.getOutputFolder());
		/*
		 * Write the elements.
		 */
		eventWriter.add(entryStart);
		eventWriter.add(converterId);
		eventWriter.add(outputFolder);
		eventWriter.add(entryEnd);
	}

	/**
	 * Writes the peak integration entry.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param peakProcessEntry
	 * @throws XMLStreamException
	 */
	private void writePeakIntegratorEntry(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IPeakProcessEntry peakProcessEntry) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entryStart = eventFactory.createStartElement("", "", IPeakIdentificationBatchJobTags.PEAK_INTEGRATION_ENTRY);
		EndElement entryEnd = eventFactory.createEndElement("", "", IPeakIdentificationBatchJobTags.PEAK_INTEGRATION_ENTRY);
		eventWriter.add(entryStart);
		/*
		 * Attributes and values.
		 */
		if(peakProcessEntry != null) {
			Attribute processorType = eventFactory.createAttribute(IPeakIdentificationBatchJobTags.PEAK_INTEGRATOR, IPeakIdentificationBatchJobTags.PEAK_INTEGRATOR_ATTRIBUTE);
			Attribute report = eventFactory.createAttribute(IPeakIdentificationBatchJobTags.PROCESS_REPORT, Boolean.toString(peakProcessEntry.isReport()));
			Attribute processorId = eventFactory.createAttribute(IPeakIdentificationBatchJobTags.PROCESSOR_ID, peakProcessEntry.getProcessorId());
			/*
			 * Write the elements.
			 */
			eventWriter.add(processorType);
			eventWriter.add(report);
			eventWriter.add(processorId);
		}
		eventWriter.add(entryEnd);
	}

	/**
	 * Writes the peak identification entry.
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @param peakProcessEntry
	 * @throws XMLStreamException
	 */
	private void writePeakIdentificationEntry(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IPeakProcessEntry peakProcessEntry) throws XMLStreamException {

		/*
		 * Element and content definition.
		 */
		StartElement entryStart = eventFactory.createStartElement("", "", IPeakIdentificationBatchJobTags.PEAK_IDENTIFICATION_ENTRY);
		EndElement entryEnd = eventFactory.createEndElement("", "", IPeakIdentificationBatchJobTags.PEAK_IDENTIFICATION_ENTRY);
		eventWriter.add(entryStart);
		/*
		 * Attributes and values.
		 */
		if(peakProcessEntry != null) {
			Attribute processorType = eventFactory.createAttribute(IPeakIdentificationBatchJobTags.PEAK_IDENTIFIER, IPeakIdentificationBatchJobTags.PEAK_IDENTIFIER_ATTRIBUTE);
			Attribute report = eventFactory.createAttribute(IPeakIdentificationBatchJobTags.PROCESS_REPORT, Boolean.toString(peakProcessEntry.isReport()));
			Attribute processorId = eventFactory.createAttribute(IPeakIdentificationBatchJobTags.PROCESSOR_ID, peakProcessEntry.getProcessorId());
			/*
			 * Write the elements.
			 */
			eventWriter.add(processorType);
			eventWriter.add(report);
			eventWriter.add(processorId);
		}
		eventWriter.add(entryEnd);
	}
}
