/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class SelectViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final Logger logger = Logger.getLogger(SelectViewLabelProvider.class);
	private Map<URI, Image> imageMap = new HashMap<>();
	//
	@Inject
	private TranslationService translationService;

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		Image iconImage = null;
		if(columnIndex == 0 && element instanceof MPart part) {
			try {
				/*
				 * TODO dispose image
				 */
				URI iconURI = new URI(part.getIconURI());
				iconImage = imageMap.get(iconURI);
				if(iconImage == null) {
					/*
					 * Create and store the image if neccessary.
					 */
					iconImage = ImageDescriptor.createFromURL(iconURI.toURL()).createImage();
					imageMap.put(iconURI, iconImage);
				}
			} catch(MalformedURLException | URISyntaxException e) {
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
		for(URI iconURI : imageMap.keySet()) {
			Image iconImage = imageMap.get(iconURI);
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
		if(element instanceof MPart part) {
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
