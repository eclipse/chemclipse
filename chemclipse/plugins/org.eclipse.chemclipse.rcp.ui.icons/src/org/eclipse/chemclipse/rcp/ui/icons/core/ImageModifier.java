/*******************************************************************************
 * Copyright (c) 2000, 2020 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * Philip Wenig - modification to retrieve image data (adjusted grayscale)
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons.core;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

/**
 * The SWT Image doesn't allow to directly modify the image data. Hence, code from the following class has been copied and modified:
 * https://github.com/eclipse/eclipse.platform.swt/blob/master/bundles/org.eclipse.swt/Eclipse%20SWT/win32/org/eclipse/swt/graphics/Image.java
 */
public class ImageModifier {

	/*
	 * The image shall be converted to light gray scale.
	 */
	private static final int INTENSITY_MIN = 180;
	private static final int INTENSITY_MAX = 255;

	public static ImageData toGrayScale(Image image) {

		//
		ImageData data = image.getImageData();
		int width = data.width;
		int height = data.height;
		//
		PaletteData palette = data.palette;
		ImageData newData = data;
		//
		if(!palette.isDirect) {
			/* Convert the palette entries to gray. */
			RGB[] rgbs = palette.getRGBs();
			for(int i = 0; i < rgbs.length; i++) {
				if(data.transparentPixel != i) {
					RGB color = rgbs[i];
					int red = color.red;
					int green = color.green;
					int blue = color.blue;
					int intensity = calculateIntensity(red, green, blue);
					color.red = color.green = color.blue = intensity;
				}
			}
			newData.palette = new PaletteData(rgbs);
		} else {
			/* Create a 8 bit depth image data with a gray palette. */
			RGB[] rgbs = new RGB[256];
			for(int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(i, i, i);
			}
			newData = new ImageData(width, height, 8, new PaletteData(rgbs));
			newData.alpha = data.alpha;
			newData.alphaData = data.alphaData;
			newData.maskData = data.maskData;
			newData.maskPad = data.maskPad;
			if(data.transparentPixel != -1)
				newData.transparentPixel = 254;
			/* Convert the pixels. */
			int[] scanline = new int[width];
			int redMask = palette.redMask;
			int greenMask = palette.greenMask;
			int blueMask = palette.blueMask;
			int redShift = palette.redShift;
			int greenShift = palette.greenShift;
			int blueShift = palette.blueShift;
			for(int y = 0; y < height; y++) {
				int offset = y * newData.bytesPerLine;
				data.getPixels(0, y, width, scanline, 0);
				for(int x = 0; x < width; x++) {
					int pixel = scanline[x];
					if(pixel != data.transparentPixel) {
						int red = pixel & redMask;
						red = (redShift < 0) ? red >>> -redShift : red << redShift;
						int green = pixel & greenMask;
						green = (greenShift < 0) ? green >>> -greenShift : green << greenShift;
						int blue = pixel & blueMask;
						blue = (blueShift < 0) ? blue >>> -blueShift : blue << blueShift;
						int intensity = calculateIntensity(red, green, blue);
						if(newData.transparentPixel == intensity)
							intensity = 255;
						newData.data[offset] = (byte)intensity;
					} else {
						newData.data[offset] = (byte)254;
					}
					offset++;
				}
			}
		}
		//
		return newData;
	}

	private static int calculateIntensity(int red, int green, int blue) {

		int intensity = (red + red + green + green + green + green + green + blue) >> 3;
		intensity = (intensity < INTENSITY_MIN) ? INTENSITY_MIN : intensity;
		intensity = (intensity > INTENSITY_MAX) ? INTENSITY_MAX : intensity;
		//
		return intensity;
	}
}
