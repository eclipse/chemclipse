/*******************************************************************************
 * Copyright (c) 2016, 2024 Lablicate GmbH.
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
import java.util.Map;
import java.util.function.Function;

import org.eclipse.chemclipse.container.support.FileContainerSupport;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.ux.extension.ui.editors.EditorDescriptor;
import org.eclipse.chemclipse.ux.extension.ui.swt.IdentifierCacheSupport;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;

public class DataExplorerLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {

	private final Function<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> supplierFunction;
	private ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

	public DataExplorerLabelProvider(Collection<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {

		this(IdentifierCacheSupport.createIdentifierCache(supplierFileIdentifierList));
	}

	public DataExplorerLabelProvider(Function<File, Map<ISupplierFileIdentifier, Collection<ISupplier>>> identifier) {

		this.supplierFunction = identifier;
	}

	@Override
	public void dispose() {

		resourceManager.dispose();
	}

	@Override
	public String getDescription(Object element) {

		if(element instanceof File file) {
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

		if(element instanceof File file) {
			ImageDescriptor descriptor = null;
			//
			if(file.getName().equals("") || file.getParent() == null) {
				descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_DRIVE, IApplicationImageProvider.SIZE_16x16);
			} else {
				Map<ISupplierFileIdentifier, Collection<ISupplier>> map = supplierFunction.apply(file);
				for(Collection<ISupplier> suppliers : map.values()) {
					for(ISupplier supplier : suppliers) {
						EditorDescriptor editorDescriptor = Adapters.adapt(supplier, EditorDescriptor.class);
						if(editorDescriptor != null) {
							ImageDescriptor imageDescriptor = editorDescriptor.getImageDescriptor();
							if(imageDescriptor != null) {
								descriptor = imageDescriptor;
								break;
							}
						}
					}
				}
				/*
				 * Try to get the data type image.
				 */
				if(descriptor == null) {
					Collection<ISupplierFileIdentifier> identifier = map.keySet();
					for(ISupplierFileIdentifier fileIdentifier : identifier) {
						descriptor = getImageDescriptor(fileIdentifier, file);
						if(descriptor != null) {
							break;
						}
					}
				}
				/*
				 * Default folder or file.
				 */
				if(descriptor == null) {
					if(file.isDirectory()) {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_FOLDER_OPENED, IApplicationImageProvider.SIZE_16x16);
					} else if(FileContainerSupport.getCache().getFileContentProvider(file) != null) {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_ZIP_FILE, IApplicationImageProvider.SIZE_16x16);
					} else {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_FILE, IApplicationImageProvider.SIZE_16x16);
					}
				}
			}
			/*
			 * Fallback
			 */
			if(descriptor != null) {
				return resourceManager.get(descriptor);
			}
		}
		return null;
	}

	private ImageDescriptor getImageDescriptor(ISupplierFileIdentifier supplierFileIdentifier, File file) {

		ImageDescriptor descriptor = null;
		if(supplierFileIdentifier != null) {
			switch(supplierFileIdentifier.getType()) {
				case ISupplierFileIdentifier.TYPE_MSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM_MSD, IApplicationImageProvider.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_SCAN_MSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_MASS_SPECTRUM_FILE, IApplicationImageProvider.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_DATABASE_MSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_MASS_SPECTRUM_DATABASE, IApplicationImageProvider.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_CSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM_CSD, IApplicationImageProvider.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_WSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM_WSD, IApplicationImageProvider.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_TSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM_TSD, IApplicationImageProvider.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_VSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_CHROMATOGRAM_VSD, IApplicationImageProvider.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_NMR:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_SCAN_NMR, IApplicationImageProvider.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_SCAN_VSD:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_SCAN_VSD, IApplicationImageProvider.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_PCR:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_PLATE_PCR, IApplicationImageProvider.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_SEQ:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_SEQUENCE_LIST, IApplicationImageProvider.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_MTH:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_METHOD, IApplicationImageProvider.SIZE_16x16);
					break;
				case ISupplierFileIdentifier.TYPE_QDB:
					descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_DATABASE, IApplicationImageProvider.SIZE_16x16);
					break;
				default:
					if(file.isDirectory()) {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_FOLDER_OPENED, IApplicationImageProvider.SIZE_16x16);
					} else if(file.isFile()) {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_FILE, IApplicationImageProvider.SIZE_16x16);
					} else {
						descriptor = ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_QUESTION, IApplicationImageProvider.SIZE_16x16);
					}
			}
		}
		return descriptor;
	}

	@Override
	public String getText(Object element) {

		if(element instanceof File file) {
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
