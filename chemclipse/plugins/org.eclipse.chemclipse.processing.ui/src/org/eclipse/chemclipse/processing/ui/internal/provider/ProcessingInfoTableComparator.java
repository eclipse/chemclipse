/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui.internal.provider;

import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.support.ui.swt.AbstractRecordTableComparator;
import org.eclipse.chemclipse.support.ui.swt.IRecordTableComparator;
import org.eclipse.jface.viewers.Viewer;

public class ProcessingInfoTableComparator extends AbstractRecordTableComparator implements IRecordTableComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		int sortOrder = 0;
		if(e1 instanceof IProcessingMessage && e2 instanceof IProcessingMessage) {
			IProcessingMessage message1 = (IProcessingMessage)e1;
			IProcessingMessage message2 = (IProcessingMessage)e2;
			switch(getPropertyIndex()) {
				case 0: // Message Type
					sortOrder = message2.getMessageType().compareTo(message1.getMessageType());
					break;
				case 1: // Description
					sortOrder = message2.getDescription().compareTo(message1.getDescription());
					break;
				case 2: // Message
					sortOrder = message2.getMessage().compareTo(message1.getMessage());
					break;
				case 3: // Date
					sortOrder = (int)(message2.getDate().getTime() - message1.getDate().getTime());
					break;
				case 4: // Proposed Solution
					sortOrder = message2.getProposedSolution().compareTo(message1.getProposedSolution());
					break;
				default:
					sortOrder = 0;
			}
		}
		if(getDirection() == ASCENDING) {
			sortOrder = -sortOrder;
		}
		return sortOrder;
	}
}
