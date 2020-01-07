/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.ui.adapter;

import org.eclipse.chemclipse.msd.converter.peak.IPeakSupplier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.editors.EditorDescriptor;
import org.eclipse.chemclipse.ux.extension.ui.editors.SnippetEditorDescriptor;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;

public class PeakSupplierAdapterFactory implements IAdapterFactory, SnippetEditorDescriptor {

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {

		if(adaptableObject instanceof IPeakSupplier) {
			if(adapterType.isInstance(this)) {
				return adapterType.cast(this);
			}
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {

		return new Class<?>[]{EditorDescriptor.class};
	}

	@Override
	public ImageDescriptor getImageDescriptor() {

		return ApplicationImageFactory.getInstance().getImageDescriptor(IApplicationImage.IMAGE_PEAKS, IApplicationImage.SIZE_16x16);
	}

	@Override
	public String getEditorSnippetId() {

		return "org.eclipse.chemclipse.ux.extension.xxd.ui.inputpart.peaklisteditor";
	}
}
