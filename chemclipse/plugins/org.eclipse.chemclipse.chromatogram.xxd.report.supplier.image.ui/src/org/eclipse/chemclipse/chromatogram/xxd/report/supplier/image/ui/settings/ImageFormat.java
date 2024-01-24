/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.supplier.image.ui.settings;

import org.eclipse.chemclipse.support.text.ILabel;
import org.eclipse.swt.SWT;

public enum ImageFormat implements ILabel {

	PNG(SWT.IMAGE_PNG, "*.png"), //
	BMP(SWT.IMAGE_BMP, "*.bmp"), //
	TIFF(SWT.IMAGE_TIFF, "*.tiff"), //
	JPEG(SWT.IMAGE_JPEG, "*.jpeg");

	private int format;
	private String label;

	private ImageFormat(int format, String label) {

		this.format = format;
		this.label = label;
	}

	public int getConstant() {

		return format;
	}

	public String getExtension() {

		return label.replace("*", "");
	}

	@Override
	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}