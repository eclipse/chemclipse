/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.model;

import org.eclipse.chemclipse.support.text.ILabel;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;

public enum TracesExportOption implements ILabel {

	SIMPLE_TEXT(ExtensionMessages.text), //
	NAMED_TRACE(ExtensionMessages.namedTrace); //

	private String label = "";

	private TracesExportOption(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}
