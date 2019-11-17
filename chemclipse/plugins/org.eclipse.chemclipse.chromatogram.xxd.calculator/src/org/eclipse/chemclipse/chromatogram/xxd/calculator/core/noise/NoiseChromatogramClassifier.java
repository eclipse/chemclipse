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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise;

import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.classifier.core.AbstractChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.AbstractChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.IChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.settings.NoiseChromatogramClassifierSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.results.ChromatogramSegmentation;
import org.eclipse.chemclipse.model.results.NoiseSegmentMeasurementResult;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.NoiseSegment;
import org.eclipse.chemclipse.model.support.SegmentWidth;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class NoiseChromatogramClassifier extends AbstractChromatogramClassifier {

	private static final String NAME = "Set Noise Settings";

	@Override
	public IProcessingInfo<IChromatogramClassifierResult> applyClassifier(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramClassifierSettings chromatogramClassifierSettings, IProgressMonitor monitor) {

		ProcessingInfo<IChromatogramClassifierResult> info = new ProcessingInfo<IChromatogramClassifierResult>(new AbstractChromatogramClassifierResult(ResultStatus.OK, NAME) {
		});
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		NoiseChromatogramClassifierSettings settings;
		if(chromatogramClassifierSettings instanceof NoiseChromatogramClassifierSettings) {
			settings = (NoiseChromatogramClassifierSettings)chromatogramClassifierSettings;
		} else {
			settings = new NoiseChromatogramClassifierSettings();
		}
		NoiseSegmentMeasurementResult noiseSettings = applyNoiseSettings(chromatogram, settings, monitor);
		if(noiseSettings == null) {
			info.addErrorMessage(NAME, "Can't find any noise segments in the given Chromatogram");
		} else if(noiseSettings.getSegmentation().getWidth() != settings.getSegmentWidth()) {
			info.addWarnMessage(NAME, "No noise segments found with segmentation width " + settings.getSegmentWidth() + " using value " + noiseSettings.getSegmentation().getWidth() + " instead, you might want to adjust settings to get better results");
		}
		return info;
	}

	private static NoiseSegmentMeasurementResult applyNoiseSettings(IChromatogram<?> chromatogram, NoiseChromatogramClassifierSettings settings, IProgressMonitor monitor) {

		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		INoiseCalculator noiseCalculator = settings.getNoiseCalculator();
		SegmentWidth segmentWidth = settings.getSegmentWidth();
		do {
			ChromatogramSegmentation segmentation = new ChromatogramSegmentation(chromatogram, segmentWidth);
			chromatogram.addMeasurementResult(segmentation);
			List<NoiseSegment> noiseSegments = noiseCalculator.getNoiseSegments(chromatogram, subMonitor.split(80));
			if(noiseSegments.isEmpty()) {
				segmentWidth = SegmentWidth.getLower(segmentWidth);
				subMonitor.setWorkRemaining(100);
			} else {
				NoiseSegmentMeasurementResult result = new NoiseSegmentMeasurementResult(Collections.unmodifiableList(noiseSegments), segmentation, settings.getNoiseCalculatorId());
				chromatogram.addMeasurementResult(result);
				return result;
			}
		} while(segmentWidth != null);
		return null;
	}

	public static List<NoiseSegment> getNoiseSegments(IChromatogram<?> chromatogram, IScanRange range, boolean includeBorders, IProgressMonitor monitor) {

		NoiseSegmentMeasurementResult noiseSegmentMeasurementResult = chromatogram.getMeasurementResult(NoiseSegmentMeasurementResult.class);
		if(noiseSegmentMeasurementResult == null) {
			noiseSegmentMeasurementResult = applyNoiseSettings(chromatogram, new NoiseChromatogramClassifierSettings(), monitor);
			if(noiseSegmentMeasurementResult == null) {
				return Collections.emptyList();
			}
		}
		return noiseSegmentMeasurementResult.getSegments(range, includeBorders);
	}
}
