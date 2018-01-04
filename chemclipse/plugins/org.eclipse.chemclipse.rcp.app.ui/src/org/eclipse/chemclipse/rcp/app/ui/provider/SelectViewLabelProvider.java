/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.provider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import org.eclipse.chemclipse.logging.core.Logger;

public class SelectViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final Logger logger = Logger.getLogger(SelectViewLabelProvider.class);
	private Map<URL, Image> imageMap = new HashMap<URL, Image>();

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		Image iconImage = null;
		if(columnIndex == 0 && element instanceof MPart) {
			MPart part = (MPart)element;
			try {
				/*
				 * TODO dispose image
				 */
				URL iconURL = new URL(part.getIconURI());
				iconImage = imageMap.get(iconURL);
				if(iconImage == null) {
					/*
					 * Create and store the image if neccessary.
					 */
					iconImage = ImageDescriptor.createFromURL(iconURL).createImage();
					imageMap.put(iconURL, iconImage);
				}
			} catch(MalformedURLException e) {
				logger.warn(e);
			}
		}
		return iconImage;
	}

	@Override
	public void dispose() {

		super.dispose();
		/*
		 * Dispose the images and clear the map.
		 */
		for(URL iconURL : imageMap.keySet()) {
			Image iconImage = imageMap.get(iconURL);
			if(iconImage != null) {
				iconImage.dispose();
			}
		}
		imageMap.clear();
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		/*
		 * Returns the label of the part
		 */
		String text = "";
		if(element instanceof MPart) {
			MPart part = (MPart)element;
			switch(columnIndex) {
				case 0: // Part Label
					text = part.getLabel();
					if(text == null || text.equals("")) {
						text = "Nameless Part";
					}
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}
}
