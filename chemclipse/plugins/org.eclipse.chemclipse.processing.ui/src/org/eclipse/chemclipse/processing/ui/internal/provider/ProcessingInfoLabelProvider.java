/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
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
import org.eclipse.chemclipse.processing.ui.Activator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ProcessingInfoLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			if(element instanceof IProcessingMessage message) {
				Image image;
				switch(message.getMessageType()) {
					case ERROR:
						image = getImage(Activator.ICON_ERROR);
						break;
					case WARN:
						image = getImage(Activator.ICON_WARN);
						break;
					case INFO:
						image = getImage(Activator.ICON_VALID); // INFO is blue, VALID is green
						break;
					default:
						image = getImage(Activator.ICON_UNKNOWN);
				}
				return image;
			}
			return null;
		} else {
			return null;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IProcessingMessage message) {
			switch(columnIndex) {
				case 0: // Message Type
					text = message.getMessageType().toString();
					break;
				case 1: // Description
					text = message.getDescription();
					break;
				case 2: // Message
					text = message.getMessage();
					break;
				case 3: // Date
					text = message.getDate().toString();
					break;
				case 4: // Solution
					text = message.getProposedSolution();
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	public Image getImage(String key) {

		ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
		if(imageRegistry != null) {
			return imageRegistry.get(key);
		} else {
			return null;
		}
	}
}
