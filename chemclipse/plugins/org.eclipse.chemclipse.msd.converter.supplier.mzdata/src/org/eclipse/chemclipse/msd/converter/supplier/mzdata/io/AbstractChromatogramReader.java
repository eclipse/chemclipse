/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.IVendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.IVendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.model.VendorScan;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractChromatogramReader extends AbstractChromatogramMSDReader implements IChromatogramMSDReader {

	private static final Logger logger = Logger.getLogger(AbstractChromatogramReader.class);

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IVendorChromatogram chromatogram = null;
		//
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
			inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
			XMLEventReader eventReader = inputFactory.createXMLEventReader(bufferedInputStream);
			EventFilter eventFilter = new EventFilterParam();
			XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
			/*
			 * Get the overview
			 */
			chromatogram = new VendorChromatogram();
			while(filteredEventReader.hasNext()) {
				//
				int retentionTime = 0;
				//
				XMLEvent xmlEvent = filteredEventReader.nextEvent();
				@SuppressWarnings("unchecked")
				Iterator<? extends Attribute> attributes = xmlEvent.asStartElement().getAttributes();
				while(attributes.hasNext()) {
					Attribute attribute = attributes.next();
					String attributeName = attribute.getName().getLocalPart();
					if(attributeName.equals("TimeInSeconds")) {
						try {
							Duration duration = DatatypeFactory.newInstance().newDuration(attribute.getValue());
							retentionTime = duration.multiply(1000).getSeconds(); // milliseconds
						} catch(DatatypeConfigurationException e) {
							logger.warn(e);
						}
					}
				}
				//
				IVendorScan massSpectrum = new VendorScan();
				massSpectrum.setRetentionTime(retentionTime);
				chromatogram.addScan(massSpectrum);
			}
		} catch(XMLStreamException e) {
			logger.warn(e);
		}
		//
		return chromatogram;
	}
}
