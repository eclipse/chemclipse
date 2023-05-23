/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.settings.FirstDerivativePeakDetectorSettings;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.FirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlope;
import org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.support.IFirstDerivativeDetectorSlopes;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.core.PeakList;
import org.eclipse.chemclipse.model.detector.IMeasurementPeakDetector;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.nmr.model.core.SpectrumMeasurement;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.detector.Detector;
import org.eclipse.chemclipse.support.l10n.TranslationSupport;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.osgi.service.component.annotations.Component;

@Component(service = {IMeasurementPeakDetector.class, Detector.class})
public class FirstDerivativePeakDetector implements IMeasurementPeakDetector<FirstDerivativePeakDetectorSettings> {

	private static TranslationService translationService = TranslationSupport.getTranslationService();
	//
	public static String DETECTOR_DESCRIPTION = translationService.translate("%FirstDerivative", Activator.getContributorURI());

	@Override
	public String getName() {

		return DETECTOR_DESCRIPTION;
	}

	@Override
	public Class<FirstDerivativePeakDetectorSettings> getConfigClass() {

		return FirstDerivativePeakDetectorSettings.class;
	}

	@Override
	public <T extends IMeasurement> Map<T, PeakList> detectIMeasurementPeaks(Collection<T> detectorInputItems, FirstDerivativePeakDetectorSettings globalConfiguration, IMessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		LinkedHashMap<T, PeakList> result = new LinkedHashMap<>();
		SubMonitor convert = SubMonitor.convert(monitor, getName(), detectorInputItems.size() * 100);
		for(T measurement : detectorInputItems) {
			IFirstDerivativeDetectorSlopes slopes;
			FirstDerivativePeakDetectorSettings configuration;
			if(measurement instanceof IChromatogramMSD chromatogramWSD) {
				if(globalConfiguration == null) {
					configuration = new FirstDerivativePeakDetectorSettings(DataType.MSD);
				} else {
					configuration = globalConfiguration;
				}
				Collection<IMarkedIons> markedIons = globalConfiguration.getFilterIons();
				slopes = PeakDetectorMSD.getFirstDerivativeSlopes(new ChromatogramSelectionMSD(chromatogramWSD), configuration.getMovingAverageWindowSize(), markedIons.iterator().next());
			} else if(measurement instanceof IChromatogramCSD chromatogramCSD) {
				if(globalConfiguration == null) {
					configuration = new FirstDerivativePeakDetectorSettings(DataType.CSD);
				} else {
					configuration = globalConfiguration;
				}
				slopes = PeakDetectorCSD.getFirstDerivativeSlopes(new ChromatogramSelectionCSD(chromatogramCSD), configuration.getMovingAverageWindowSize());
			} else if(measurement instanceof IChromatogramWSD chromatogramWSD) {
				if(globalConfiguration == null) {
					configuration = new FirstDerivativePeakDetectorSettings(DataType.WSD);
				} else {
					configuration = globalConfiguration;
				}
				// TODO: filter wavelengths
				slopes = PeakDetectorWSD.getFirstDerivativeSlopes(new ChromatogramSelectionWSD(chromatogramWSD), configuration.getMovingAverageWindowSize(), null);
			} else if(measurement instanceof SpectrumMeasurement spectrumMeasurement) {
				if(globalConfiguration == null) {
					configuration = new FirstDerivativePeakDetectorSettings(DataType.NMR);
				} else {
					configuration = globalConfiguration;
				}
				slopes = getSignalSlopes((spectrumMeasurement).getSignals(), configuration.getMovingAverageWindowSize());
			} else {
				throw new IllegalArgumentException();
			}
			List<IRawPeak> rawPeaks = BasePeakDetector.getRawPeaks(slopes, configuration.getThreshold(), convert.split(100));
			result.put(measurement, new PeakList(Collections.unmodifiableCollection(rawPeaks), getID(), getName(), getDescription()));
		}
		return result;
	}

	@Override
	public String getDescription() {

		return translationService.translate("%FirstDerivativeDescription", Activator.getContributorURI());
	}

	private static IFirstDerivativeDetectorSlopes getSignalSlopes(List<? extends ISignal> signals, int windowSize) {

		IFirstDerivativeDetectorSlopes firstDerivativeSlopes = new FirstDerivativeDetectorSlopes(signals);
		for(int i = 1; i < signals.size(); i++) {
			ISignal signal = signals.get(i - 1);
			ISignal signalNext = signals.get(i);
			IFirstDerivativeDetectorSlope slope = new SignalSlope(signal, signalNext);
			firstDerivativeSlopes.add(slope);
		}
		firstDerivativeSlopes.calculateMovingAverage(windowSize);
		return firstDerivativeSlopes;
	}

	@Override
	public boolean acceptsIMeasurements(Collection<? extends IMeasurement> items) {

		for(IMeasurement measurement : items) {
			if(!canProcess(measurement)) {
				return false;
			}
		}
		return true;
	}

	private boolean canProcess(IMeasurement measurement) {

		if(measurement instanceof IChromatogramMSD) {
			return true;
		}
		if(measurement instanceof IChromatogramWSD) {
			return true;
		}
		if(measurement instanceof IChromatogramCSD) {
			return true;
		}
		if(measurement instanceof SpectrumMeasurement) {
			return true;
		}
		return false;
	}

	private static final class SignalSlope implements IFirstDerivativeDetectorSlope {

		private double slope;

		public SignalSlope(ISignal signal, ISignal signalNext) {

			IPoint p1 = new Point(signal.getX(), signal.getY());
			IPoint p2 = new Point(signalNext.getX(), signalNext.getY());
			slope = Equations.calculateSlopeAbs(p1, p2);
		}

		@Override
		public void setSlope(double slope) {

			this.slope = slope;
		}

		@Override
		public double getSlope() {

			return slope;
		}

		@Override
		public int getRetentionTime() {

			return -1;
		}

		@Override
		public String getDrift() {

			return "";
		}
	}
}
