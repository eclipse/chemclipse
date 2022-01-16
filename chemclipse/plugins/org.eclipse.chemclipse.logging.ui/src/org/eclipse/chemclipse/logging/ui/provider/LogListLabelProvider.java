/*******************************************************************************
 * Copyright (c) 2021, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui.provider;

import org.eclipse.chemclipse.logging.ui.ParsedLogEntry;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class LogListLabelProvider extends AbstractChemClipseLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 2) {
			return getImage(element);
		} else {
			return null;
		}
	}

	@Override
	public Color getBackground(Object element) {

		if(element instanceof ParsedLogEntry) {
			ParsedLogEntry logEntry = (ParsedLogEntry)element;
			if(logEntry.getMessage().contains("successfully") || logEntry.getMessage().contains("activated")) {
				return Colors.LIGHT_GREEN;
			} else if(logEntry.getLevel().contains("WARN")) {
				return Colors.LIGHT_YELLOW;
			} else if(logEntry.getLevel().contains("ERROR")) {
				return Colors.LIGHT_RED;
			} else if(logEntry.getLevel().contains("INFO")) {
				return Colors.WHITE;
			}
		}
		return Colors.WHITE;
	}

	@Override
	public Image getImage(Object element) {

		if(element instanceof ParsedLogEntry) {
			ParsedLogEntry logEntry = (ParsedLogEntry)element;
			if(logEntry.getMessage().contains("successfully") || logEntry.getMessage().contains("activated")) {
				return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_VALID, IApplicationImage.SIZE_16x16);
			} else if(logEntry.getLevel().contains("INFO")) {
				return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16);
			} else if(logEntry.getLevel().contains("WARN")) {
				return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_WARN, IApplicationImage.SIZE_16x16);
			} else if(logEntry.getLevel().contains("ERROR")) {
				return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ERROR, IApplicationImage.SIZE_16x16);
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof ParsedLogEntry) {
			ParsedLogEntry logEntry = (ParsedLogEntry)element;
			switch(columnIndex) {
				case 0:
					text = logEntry.getDate();
					break;
				case 1:
					text = logEntry.getTime();
					break;
				case 2:
					text = logEntry.getLevel();
					break;
				case 3:
					text = logEntry.getThread();
					break;
				case 4:
					text = logEntry.getLocation();
					break;
				case 5:
					text = logEntry.getMessage();
					break;
				default:
					text = "";
			}
		}
		return text;
	}
}
