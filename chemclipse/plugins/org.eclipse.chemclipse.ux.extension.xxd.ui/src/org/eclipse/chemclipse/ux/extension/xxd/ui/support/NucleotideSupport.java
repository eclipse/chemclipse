/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import org.eclipse.chemclipse.dsd.model.core.Nucleobase;
import org.eclipse.swt.graphics.Color;

public class NucleotideSupport {

	public static Color getColor(Nucleobase nucleobase) {

		switch(nucleobase) {
			case Nucleobase.ADENINE:
				return new Color(0, 175, 0);
			case Nucleobase.CYTOSINE:
				return new Color(0, 0, 255);
			case Nucleobase.GUANINE:
				return new Color(0, 0, 0);
			case Nucleobase.THYMINE:
				return new Color(255, 0, 0);
			default:
				return new Color(255, 0, 255);
		}
	}
}
