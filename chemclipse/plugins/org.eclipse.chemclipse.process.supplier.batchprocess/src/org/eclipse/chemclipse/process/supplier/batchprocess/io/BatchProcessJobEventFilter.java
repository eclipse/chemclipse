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

import java.util.List;

import javax.xml.stream.EventFilter;
import javax.xml.stream.events.XMLEvent;

public class BatchProcessJobEventFilter implements EventFilter {

	private List<String> acceptedElements;

	/**
	 * Use e.g.:
	 * IBatchProcessJobTags.REPORT_FOLDER, ... stored in the list
	 * 
	 * @param acceptedElements
	 */
	public BatchProcessJobEventFilter(List<String> acceptedElements) {

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