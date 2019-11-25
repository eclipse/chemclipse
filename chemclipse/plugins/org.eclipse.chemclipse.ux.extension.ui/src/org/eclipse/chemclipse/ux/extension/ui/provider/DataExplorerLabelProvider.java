/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - query the content provider instead to participate in caching, use ResourceManager
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.io.File;
import java.util.Collection;
import java.util.function.Function;

import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;

public class DataExplorerLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {

	ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
	private Function<File, Collection<ISupplierFileIdentifier>> supplierFunction;

	public DataExplorerLabelProvider(Function<File, Collection<ISupplierFileIdentifier>> supplierFunction) {
		this.supplierFunction = supplierFunction;
	}

	@Override
	public void dispose() {

		resourceManager.dispose();
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

		if(element instanceof File) {
			File file = (File)element;
			ImageDescriptor descriptor = null;
			if(file.getName().equals("") || file.getParent() == null) {
				descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_DRIVE, IApplicationImage.SIZE_16x16);
			} else {
				Collection<ISupplierFileIdentifier> identifier = supplierFunction.apply(file);
				for(ISupplierFileIdentifier fileIdentifier : identifier) {
					descriptor = getImageDescriptor(fileIdentifier, file);
					if(descriptor != null) {
						break;
					}
				}
				if(descriptor == null) {
					if(file.isDirectory()) {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_FOLDER_OPENED, IApplicationImage.SIZE_16x16);
					} else {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_FILE, IApplicationImage.SIZE_16x16);
					}
				}
			}
			if(descriptor != null) {
				return (Image)resourceManager.get(descriptor);
			}
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
