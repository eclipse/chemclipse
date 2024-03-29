/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.support.l10n.SupportMessages;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class EditHistoryLabelProvider extends LabelProvider implements ITableLabelProvider {

	public static final String[] TITLES = {//
			SupportMessages.columnDescription, //
			SupportMessages.columnEditor, //
			SupportMessages.columnDate, //
	};
	//
	public static final int[] BOUNDS = {//
			300, //
			100, //
			100, //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		// Don't use an image.
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IEditInformation editInformation) {
			switch(columnIndex) {
				case 0:
					text = editInformation.getDescription();
					break;
				case 1:
					text = editInformation.getEditor();
					break;
				case 2:
					text = editInformation.getDate().toString();
					break;
				default:
					text = SupportMessages.labelNotAvailable;
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FILE, IApplicationImageProvider.SIZE_16x16);
	}
}
