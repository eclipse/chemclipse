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
 * Christoph Läubrich - fix bug with local perspectives
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
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class PerspectiveSwitcherLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final Logger logger = Logger.getLogger(PerspectiveSwitcherLabelProvider.class);
	private Map<URI, Image> imageMap = new HashMap<>();
	//
	@Inject
	private TranslationService translationService;

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		Image iconImage = null;
		if(columnIndex == 0 && element instanceof MPerspective perspective) {
			try {
				URI iconURI = new URI(perspective.getIconURI());
				iconImage = imageMap.get(iconURI);
				if(iconImage == null) {
					/*
					 * Create and store the image if neccessary.
					 */
					iconImage = ImageDescriptor.createFromURL(iconURI.toURL()).createImage();
					imageMap.put(iconURI, iconImage);
				}
			} catch(MalformedURLException e) {
				logger.warn(e);
			} catch(URISyntaxException e) {
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
		 * Returns the label of the perspective
		 */
		String text = "";
		if(element instanceof MPerspective perspective) {
			switch(columnIndex) {
				case 0: // Perspective Label
					text = perspective.getLabel();
					if(text == null || text.equals("")) {
						text = "Nameless perspective";
					} else if(text.startsWith("<") && text.endsWith(">")) {
						text = text.substring(1, text.length() - 1);
						text = translationService.translate(text, perspective.getContributorURI());
					}
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}
}