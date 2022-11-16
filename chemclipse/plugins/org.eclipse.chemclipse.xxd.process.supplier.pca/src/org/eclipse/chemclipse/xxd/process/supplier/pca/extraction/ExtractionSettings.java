/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.extraction;

import org.eclipse.chemclipse.xxd.process.supplier.pca.core.ExtractionOption;

public class ExtractionSettings {

	private ExtractionOption extractionOption = ExtractionOption.RETENTION_TIME_MS;
	private int groupValueWindow = 0;

	public ExtractionSettings(ExtractionOption extractionOption, int groupValueWindow) {

		this.extractionOption = extractionOption;
		this.groupValueWindow = groupValueWindow;
	}

	public ExtractionOption getExtractionOption() {

		return extractionOption;
	}

	public int getGroupValueWindow() {

		return groupValueWindow;
	}
}