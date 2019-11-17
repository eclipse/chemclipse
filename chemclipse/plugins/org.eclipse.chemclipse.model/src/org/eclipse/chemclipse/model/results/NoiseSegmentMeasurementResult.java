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
package org.eclipse.chemclipse.model.results;

import java.util.List;

import org.eclipse.chemclipse.model.support.NoiseSegment;

public class NoiseSegmentMeasurementResult extends AnalysisSegmentMeasurementResult<NoiseSegment> {

	private final ChromatogramSegmentation segmentation;
	private final String noiseCalculatorId;
	private final List<NoiseSegment> segments;

	public NoiseSegmentMeasurementResult(List<NoiseSegment> segments, ChromatogramSegmentation segmentation, String noiseCalculatorId) {
		this.segments = segments;
		this.segmentation = segmentation;
		this.noiseCalculatorId = noiseCalculatorId;
	}

	@Override
	public String getName() {

		return "Noise Segments";
	}

	@Override
	public List<NoiseSegment> getResult() {

		return segments;
	}

	public ChromatogramSegmentation getSegmentation() {

		return segmentation;
	}

	public String getNoiseCalculatorId() {

		return noiseCalculatorId;
	}

	@Override
	public Class<NoiseSegment> getType() {

		return NoiseSegment.class;
	}
}
