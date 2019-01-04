/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;

public class DataExplorerLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {

	private List<? extends ISupplierFileIdentifier> supplierFileIdentifierList;

	public DataExplorerLabelProvider(ISupplierFileIdentifier supplierFileIdentifier) {
		this(ExplorerListSupport.getSupplierFileIdentifierList(supplierFileIdentifier));
	}

	public DataExplorerLabelProvider(List<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {
		this.supplierFileIdentifierList = supplierFileIdentifierList;
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
					 * Check if the directory could be a registered file type.
					 */
					boolean isNormalDirectory = true;
					exitloop:
					for(ISupplierFileIdentifier supplierFileIdentifier : supplierFileIdentifierList) {
						if(supplierFileIdentifier.isSupplierFileDirectory(file)) {
							/*
							 * Check and validate.
							 */
							if(supplierFileIdentifier.isMatchMagicNumber(file)) {
								descriptor = getImageDescriptor(supplierFileIdentifier, file);
								if(descriptor != null) {
									isNormalDirectory = false;
									break exitloop;
								}
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
					 * Check if the file could be a registered supplier file.
					 */
					boolean isNormalFile = true;
					exitloop:
					for(ISupplierFileIdentifier supplierFileIdentifier : supplierFileIdentifierList) {
						if(supplierFileIdentifier.isSupplierFile(file)) {
							/*
							 * Check and validate.
							 */
							if(supplierFileIdentifier.isMatchMagicNumber(file)) {
								descriptor = getImageDescriptor(supplierFileIdentifier, file);
								if(descriptor != null) {
									isNormalFile = false;
									break exitloop;
								}
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

	private ImageDescriptor getImageDescriptor(ISupplierFileIdentifier supplierFileIdentifier, File file) {

		ImageDescriptor descriptor = null;
		if(supplierFileIdentifier != null) {
			switch(supplierFileIdentifier.getType()) {
				case ISupplierFileIdentifier.TYPE_MSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM_MSD, IApplicationImage.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_SCAN_MSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_MASS_SPECTRUM_FILE, IApplicationImage.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_DATABASE_MSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_MASS_SPECTRUM_DATABASE, IApplicationImage.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_CSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM_CSD, IApplicationImage.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_WSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM_WSD, IApplicationImage.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_NMR:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_SCAN_NMR, IApplicationImage.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_XIR:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_SCAN_XIR, IApplicationImage.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_PCR:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_PLATE_PCR, IApplicationImage.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_SEQ:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_SEQUENCE_LIST, IApplicationImage.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_MTH:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_QDB:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_DATABASE, IApplicationImage.SIZE_16x16);
					break;
				default:
					/*
					 * Default
					 */
					if(file.isDirectory()) {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_FOLDER_OPENED, IApplicationImage.SIZE_16x16);
					} else if(file.isFile()) {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_FILE, IApplicationImage.SIZE_16x16);
					} else {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_QUESTION, IApplicationImage.SIZE_16x16);
					}
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
