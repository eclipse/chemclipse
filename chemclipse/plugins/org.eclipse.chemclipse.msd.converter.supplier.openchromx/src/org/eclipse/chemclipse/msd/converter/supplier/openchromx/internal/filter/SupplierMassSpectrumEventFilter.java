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
package org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.filter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.EventFilter;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.chemclipse.msd.converter.supplier.openchromx.internal.support.IChromatogramTags;

/**
 * Accepts only events of type RetentionTime, RetentionIndex and TotalIonSignal.
 * 
 * @author eselmeister
 */
public class SupplierMassSpectrumEventFilter implements EventFilter {

	private List<String> acceptedElements;

	public SupplierMassSpectrumEventFilter() {
		acceptedElements = new ArrayList<String>();
		acceptedElements.add(IChromatogramTags.SUPPLIER_MASS_SPECTRUM);
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
