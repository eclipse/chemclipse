/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;

public class ChromatogramFileExplorerLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {

	private List<? extends IChromatogramIdentifier> chromatogramIdentifierList;

	public ChromatogramFileExplorerLabelProvider(IChromatogramIdentifier chromatogramIdentifier) {
		this(ExplorerListSupport.getChromatogramIdentifierList(chromatogramIdentifier));
	}

	public ChromatogramFileExplorerLabelProvider(List<? extends IChromatogramIdentifier> chromatogramIdentifierList) {
		this.chromatogramIdentifierList = chromatogramIdentifierList;
	}

	@Override
	public String getDescription(Object anElement) {

		if(anElement instanceof File) {
			File file = (File)anElement;
			String name;
			if(file.getName().equals("")) {
				name = file.getAbsolutePath();
			} else {
				name = file.getName();
			}
			return "This is: " + name;
		}
		return "";
	}

	@Override
	public Image getImage(Object element) {

		ImageDescriptor descriptor = null;
		if(element instanceof File) {
			File file = (File)element;
			/*
			 * Root, directory or file.
			 */
			if(file.getName().equals("")) {
				descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_DRIVE, IApplicationImage.SIZE_16x16);
			} else {
				if(file.isDirectory()) {
					/*
					 * Check if the directory could be a registered
					 * chromatogram.
					 */
					boolean isNormalDirectory = true;
					exitloop:
					for(IChromatogramIdentifier chromatogramIdentifier : chromatogramIdentifierList) {
						if(chromatogramIdentifier.isChromatogramDirectory(file)) {
							/*
							 * Check and validate.
							 */
							descriptor = getImageDescriptor(chromatogramIdentifier);
							if(descriptor != null) {
								isNormalDirectory = false;
								break exitloop;
							}
						}
					}
					/*
					 * Default dir.
					 */
					if(isNormalDirectory) {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_FOLDER_OPENED, IApplicationImage.SIZE_16x16);
					}
				} else {
					/*
					 * Check if the file could be a registered chromatogram.
					 */
					boolean isNormalFile = true;
					exitloop:
					for(IChromatogramIdentifier chromatogramIdentifier : chromatogramIdentifierList) {
						if(chromatogramIdentifier.isChromatogram(file)) {
							/*
							 * Check and validate.
							 */
							descriptor = getImageDescriptor(chromatogramIdentifier);
							if(descriptor != null) {
								isNormalFile = false;
								break exitloop;
							}
						}
					}
					/*
					 * Default file.
					 */
					if(isNormalFile) {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_FILE, IApplicationImage.SIZE_16x16);
					}
				}
			}
			Image image = descriptor.createImage();
			return image;
		}
		return null;
	}

	private ImageDescriptor getImageDescriptor(IChromatogramIdentifier chromatogramIdentifier) {

		ImageDescriptor descriptor = null;
		if(chromatogramIdentifier != null) {
			switch(chromatogramIdentifier.getType()) {
				case IChromatogramIdentifier.TYPE_MSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM_MSD, IApplicationImage.SIZE_16x16);
					break;
				case IChromatogramIdentifier.TYPE_CSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM_CSD, IApplicationImage.SIZE_16x16);
					break;
				case IChromatogramIdentifier.TYPE_WSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM_WSD, IApplicationImage.SIZE_16x16);
					break;
				default:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImage.SIZE_16x16);
			}
		}
		return descriptor;
	}

	@Override
	public String getText(Object element) {

		if(element instanceof File) {
			File file = (File)element;
			String name;
			if(file.getName().equals("")) {
				name = file.getAbsolutePath();
			} else {
				name = file.getName();
			}
			return name;
		}
		return "";
	}
}
