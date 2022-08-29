/*******************************************************************************
 * Copyright (c) 2012, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - fix bug with local perspectives
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.provider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class PerspectiveSwitcherLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final Logger logger = Logger.getLogger(PerspectiveSwitcherLabelProvider.class);
	private Map<URL, Image> imageMap = new HashMap<URL, Image>();

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		Image iconImage = null;
		if(columnIndex == 0 && element instanceof MPerspective) {
			MPerspective perspective = (MPerspective)element;
			try {
				URL iconURL = new URL(perspective.getIconURI());
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
		 * Returns the label of the perspective
		 */
		String text = "";
		if(element instanceof MPerspective) {
			MPerspective perspective = (MPerspective)element;
			switch(columnIndex) {
				case 0: // Perspective Label
					text = perspective.getLabel();
					if(text == null || text.equals("")) {
						text = "Nameless perspective";
					} else if(text.startsWith("<") && text.endsWith(">")) {
						text = text.substring(1, text.length() - 1);
						if(text.startsWith("%")) {
							/*
							 * TODO - Translate
							 */
							text = text.substring(1, text.length());
						}
					}
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}
}