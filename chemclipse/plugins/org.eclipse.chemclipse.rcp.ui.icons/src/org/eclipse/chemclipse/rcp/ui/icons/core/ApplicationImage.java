/*******************************************************************************
 * Copyright (c) 2013, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - reimplementation to support discovering of bundles
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.svg.JSVGRasterizer;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

public class ApplicationImage implements IApplicationImage {

	private static final Logger logger = Logger.getLogger(ApplicationImage.class);

	private static final String PATH_SEPARATOR = "/";
	private static final String PLATFORM_PREFIX = "platform:/plugin/";
	private static final String FOLDER_ICONS = "icons";
	private static final String FOLDER_SCALABLE = "scalable";
	private static final String EXTENSION_PNG = ".png";
	private static final String EXTENSION_GIF = ".gif";
	private static final String EXTENSION_SVG = ".svg";

	private Bundle bundle;
	private Map<String, ImageDescriptor> imageDescriptorCache = new HashMap<>();
	private Map<String, Image> imageCache = new HashMap<>();

	public ApplicationImage(Bundle bundle) {

		this.bundle = bundle;
	}

	@Override
	public Image getImage(String fileName, String size) {

		String path = getPath(fileName, size);
		String key = fileName + "/" + size;
		Image image = imageCache.get(key);
		if(image == null) {
			if(fileName.endsWith(EXTENSION_SVG)) {
				JSVGRasterizer rasterizer = new JSVGRasterizer();
				String[] sizes = size.split("x");
				int width = Integer.valueOf(sizes[0]);
				int height = Integer.valueOf(sizes[1]);
				URL fileLocation = FileLocator.find(bundle, new Path(path), null);
				try {
					ImageData data = rasterizer.rasterizeSVG(fileLocation.openStream(), width, height);
					image = new Image(Display.getCurrent(), data);
				} catch(IOException e) {
					// ignore , ImageDescriptor.createImage() will return null so same semantic
				}
			} else {
				ImageDescriptor imageDescriptor = getImageDescriptor(path);
				if(imageDescriptor != null) {
					image = imageDescriptor.createImage();
				}
			}
		}
		if(image != null) {
			imageCache.put(key, image);
		}

		return image;
	}

	@Override
	public ImageDescriptor getImageDescriptor(String fileName, String size) {

		String path = getPath(fileName, size);
		return getImageDescriptor(path);
	}

	@Override
	public InputStream getImageAsInputStream(String fileName, String size) throws IOException {

		URL url = FileLocator.find(bundle, new Path(getPath(fileName, size)), null);
		return url.openConnection().getInputStream();
	}

	@Override
	public Collection<String> listImages(String size) {

		List<String> images = new ArrayList<>();

		try {
			StringBuilder builder = new StringBuilder();
			builder.append(PATH_SEPARATOR);
			builder.append(FOLDER_ICONS);
			builder.append(PATH_SEPARATOR);
			if(FOLDER_SCALABLE.equals(size)) {
				builder.append(FOLDER_SCALABLE);
			} else {
				builder.append(size);
			}
			builder.append(PATH_SEPARATOR);

			IPath path = new Path(builder.toString());
			URL url = FileLocator.find(bundle, path, null);
			File directory = new File(FileLocator.resolve(url).getPath());
			if(directory.isDirectory()) {
				for(File file : directory.listFiles()) {
					String name = file.getName().toLowerCase();
					if(name.endsWith(EXTENSION_PNG) || name.endsWith(EXTENSION_GIF)) {
						images.add(file.getName());
					}
				}
			}
		} catch(IOException e) {
			logger.error(e);
		}

		return images;
	}

	@Override
	public String getURI(String fileName, String size) {

		StringBuilder builder = new StringBuilder();

		builder.append(PLATFORM_PREFIX);
		builder.append(bundle.getSymbolicName());
		builder.append(PATH_SEPARATOR);
		builder.append(getPath(fileName, size));

		return builder.toString();
	}

	/**
	 * Calculates the path, given by the file name and size.
	 */
	protected String getPath(String fileName, String size) {

		StringBuilder builder = new StringBuilder();

		builder.append(FOLDER_ICONS);
		builder.append(PATH_SEPARATOR);
		if(fileName.endsWith(EXTENSION_SVG)) {
			builder.append(FOLDER_SCALABLE);
		} else {
			builder.append(size);
		}
		builder.append(PATH_SEPARATOR);
		builder.append(fileName);

		return builder.toString();
	}

	/**
	 * Return the image descriptor given by the path.
	 */
	private ImageDescriptor getImageDescriptor(String path) {

		ImageDescriptor imageDescriptor = imageDescriptorCache.get(path);
		if(imageDescriptor == null) {
			try {
				URL fileLocation = FileLocator.find(bundle, new Path(path), null);
				imageDescriptor = ImageDescriptor.createFromURL(fileLocation);
				if(imageDescriptor != null) {
					imageDescriptorCache.put(path, imageDescriptor);
				}
			} catch(Exception e) {
			}
		}

		return imageDescriptor;
	}
}