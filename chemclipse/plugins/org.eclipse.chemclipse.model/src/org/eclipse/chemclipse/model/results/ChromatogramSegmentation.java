/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Matthias Mailänder - remove enums
 *******************************************************************************/
package org.eclipse.chemclipse.model.results;

import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.support.AnalysisSupport;
import org.eclipse.chemclipse.model.support.ChromatogramSegment;

public class ChromatogramSegmentation extends AnalysisSegmentMeasurementResult<ChromatogramSegment> {

	private final List<ChromatogramSegment> segments;
	private final int width;

	public ChromatogramSegmentation(IChromatogram<?> chromatogram, int width) {

		this.width = width;
		segments = Collections.unmodifiableList(AnalysisSupport.getChromatogramSegments(chromatogram, width));
	}

	@Override
	public String getName() {

		return "Chromatogram Segments";
	}

	@Override
	public List<ChromatogramSegment> getResult() {

		return segments;
	}

	public int getWidth() {

		return width;
	}

	@Override
	public boolean isVisible() {

		return false;
	}

	@Override
	public Class<ChromatogramSegment> getType() {

		return ChromatogramSegment.class;
	}
}
