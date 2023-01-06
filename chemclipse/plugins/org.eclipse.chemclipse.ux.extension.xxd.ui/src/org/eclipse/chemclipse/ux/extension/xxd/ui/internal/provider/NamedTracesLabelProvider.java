/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.traces.NamedTrace;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.swt.graphics.Image;

public class NamedTracesLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String IDENTIFIER = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.IDENTIFIER);
	public static final String TRACES = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.TRACES);
	//
	public static final String[] TITLES = { //
			IDENTIFIER, //
			TRACES //
	};
	public static final int[] BOUNDS = { //
			300, //
			250 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof NamedTrace) {
			NamedTrace namedTrace = (NamedTrace)element;
			switch(columnIndex) {
				case 0:
					text = namedTrace.getIdentifier();
					break;
				case 1:
					text = namedTrace.getTraces();
					break;
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK_TRACES, IApplicationImage.SIZE_16x16);
	}
}
