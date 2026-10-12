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
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.truyols.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.literature.LiteratureReference;

public class LiteratureSupport {

	private static final Logger logger = Logger.getLogger(LiteratureSupport.class);

	public static List<LiteratureReference> getLiteratureReferences() {

		List<LiteratureReference> literatureReferences = new ArrayList<>();
		literatureReferences.add(createLiteratureReference("S0021967305006424.ris", "10.1016/j.chroma.2005.03.092"));
		literatureReferences.add(createLiteratureReference("S0021967305006394.ris", "10.1016/j.chroma.2005.03.072"));
		return literatureReferences;
	}

	private static LiteratureReference createLiteratureReference(String file, String doi) {

		String content;
		try {
			content = new String(PeakDetectorSettingsCSD.class.getResourceAsStream(file).readAllBytes());
		} catch(IOException | NullPointerException e) {
			content = doi;
			logger.warn(e);
		}
		return new LiteratureReference(content);
	}
}
