/*******************************************************************************
 * Copyright (c) 2015, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io;

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
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.model.IVendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.model.IVendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.model.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.model.VendorScan;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractReaderVersion extends AbstractChromatogramMSDReader implements IChromatogramMSDReader {

	private static final Logger logger = Logger.getLogger(AbstractReaderVersion.class);

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
			EventFilter eventFilter = new EventFilterScan();
			XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
			/*
			 * Get the overview
			 */
			chromatogram = new VendorChromatogram();
			while(filteredEventReader.hasNext()) {
				//
				int retentionTime = 0;
				float totalSignal = 0.0f;
				//
				XMLEvent xmlEvent = filteredEventReader.nextEvent();
				Iterator<? extends Attribute> attributes = xmlEvent.asStartElement().getAttributes();
				while(attributes.hasNext()) {
					Attribute attribute = attributes.next();
					String attributeName = attribute.getName().getLocalPart();
					if(attributeName.equals("retentionTime")) {
						try {
							Duration duration = DatatypeFactory.newInstance().newDuration(attribute.getValue());
							retentionTime = duration.multiply(1000).getSeconds(); // milliseconds
						} catch(DatatypeConfigurationException e) {
							logger.warn(e);
						}
					} else if(attributeName.equals("totIonCurrent")) {
						totalSignal = Float.valueOf(attribute.getValue());
					}
				}
				//
				IVendorScan massSpectrum = new VendorScan();
				massSpectrum.setRetentionTime(retentionTime);
				try {
					massSpectrum.addIon(new VendorIon(AbstractIon.TIC_ION, totalSignal));
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				} catch(IonLimitExceededException e) {
					logger.warn(e);
				}
				chromatogram.addScan(massSpectrum);
			}
		} catch(XMLStreamException e) {
			logger.warn(e);
		}
		//
		return chromatogram;
	}
}
