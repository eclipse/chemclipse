/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons.core;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.graphics.Point;

public class ImageDecorator extends CompositeImageDescriptor {

	private static final Logger logger = Logger.getLogger(ImageDecorator.class);
	//
	private Image image = null;
	private String size = "";
	private Point point = null;

	public ImageDecorator() {

		setSize(IApplicationImageProvider.SIZE_16x16);
	}

	public void setImage(Image image) {

		this.image = image;
	}

	public void setSize(String size) {

		this.size = size;
		calculateSize();
	}

	@Override
	protected void drawCompositeImage(int width, int height) {

		drawImage(width, height);
		drawDecorator(width, height);
	}

	@Override
	protected Point getSize() {

		return point;
	}

	private void drawImage(int width, int height) {

		if(image != null) {
			ImageDataProvider imageDataProvider = new ImageDataProvider() {

				@Override
				public ImageData getImageData(int zoom) {

					return image.getImageData();
				}
			};
			drawImage(imageDataProvider, 0, 0);
		}
	}

	private void drawDecorator(int width, int height) {

		Image decorator = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DECORATOR_ACTIVE, IApplicationImage.SIZE_7x7);
		ImageDataProvider imageDataProvider = new ImageDataProvider() {

			@Override
			public ImageData getImageData(int zoom) {

				return decorator.getImageData();
			}
		};
		/*
		 * Place the decorator in the upper right edge, space 1 px.
		 */
		drawImage(imageDataProvider, width - 8, 1);
	}

	private void calculateSize() {

		String[] values = size.split("x");
		if(values.length == 2) {
			try {
				int width = Integer.parseInt(values[0]);
				int height = Integer.parseInt(values[1]);
				point = new Point(width, height);
			} catch(NumberFormatException e) {
				logger.warn(e);
			}
		} else {
			point = new Point(16, 16);
		}
	}
}
