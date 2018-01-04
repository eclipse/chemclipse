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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.internal.filter;

import java.util.List;

import javax.xml.stream.EventFilter;
import javax.xml.stream.events.XMLEvent;

/**
 * @author Dr. Philip Wenig
 * 
 */
public class PeakIdentificationBatchJobEventFilter implements EventFilter {

	private List<String> acceptedElements;

	/**
	 * Use e.g.:
	 * IPeakIdentificationBatchJobTags.REPORT_FOLDER, ... stored in the list
	 * 
	 * @param acceptedElements
	 */
	public PeakIdentificationBatchJobEventFilter(List<String> acceptedElements) {
		if(acceptedElements != null) {
			this.acceptedElements = acceptedElements;
		}
	}

	@Override
	public boolean accept(XMLEvent xmlEvent) {

		boolean result = false;
		String element;
		if(xmlEvent.isStartElement()) {
			element = xmlEvent.asStartElement().getName().getLocalPart();
			exitloop:
			for(String acceptedElement : acceptedElements) {
				if(element.equals(acceptedElement)) {
					result = true;
					break exitloop;
				}
			}
		}
		return result;
	}
}
