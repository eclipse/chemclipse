/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui.internal.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public class ProcessingInfoLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			if(element instanceof IProcessingMessage) {
				IProcessingMessage message = (IProcessingMessage)element;
				Image image;
				switch(message.getMessageType()) {
					case ERROR:
						image = getImage(element, IApplicationImage.IMAGE_ERROR);
						break;
					case WARN:
						image = getImage(element, IApplicationImage.IMAGE_WARN);
						break;
					case INFO:
						image = getImage(element, IApplicationImage.IMAGE_INFO);
						break;
					default:
						image = getImage(element, IApplicationImage.IMAGE_UNKNOWN);
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
		if(element instanceof IProcessingMessage) {
			IProcessingMessage message = (IProcessingMessage)element;
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
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	public Image getImage(Object element, String imageDescription) {

		return ApplicationImageFactory.getInstance().getImage(imageDescription, IApplicationImage.SIZE_16x16);
	}
}
