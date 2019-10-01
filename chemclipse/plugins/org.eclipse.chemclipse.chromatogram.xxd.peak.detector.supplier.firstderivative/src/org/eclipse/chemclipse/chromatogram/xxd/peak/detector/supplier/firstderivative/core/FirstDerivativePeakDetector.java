/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.firstderivative.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.peak.detector.support.IRawPeak;
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
import org.eclipse.chemclipse.nmr.model.core.SpectrumMeasurement;
import org.eclipse.chemclipse.numeric.core.IPoint;
import org.eclipse.chemclipse.numeric.core.Point;
import org.eclipse.chemclipse.numeric.equations.Equations;
import org.eclipse.chemclipse.numeric.statistics.WindowSize;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.detector.Detector;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IMeasurementPeakDetector.class, Detector.class})
public class FirstDerivativePeakDetector implements IMeasurementPeakDetector<FirstDerivativePeakDetectorSettings> {

	@Override
	public String getName() {

		return "First Derivative Peak Detector";
	}

	@Override
	public Class<FirstDerivativePeakDetectorSettings> getConfigClass() {

		return FirstDerivativePeakDetectorSettings.class;
	}

	@Override
	public <T extends IMeasurement> Map<T, PeakList> detectIMeasurementPeaks(Collection<T> detectorInputItems, FirstDerivativePeakDetectorSettings globalConfiguration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		LinkedHashMap<T, PeakList> result = new LinkedHashMap<>();
		SubMonitor convert = SubMonitor.convert(monitor, getName(), detectorInputItems.size() * 100);
		for(T measurement : detectorInputItems) {
			IFirstDerivativeDetectorSlopes slopes;
			FirstDerivativePeakDetectorSettings configuration;
			if(measurement instanceof IChromatogramMSD) {
				if(globalConfiguration == null) {
					configuration = new FirstDerivativePeakDetectorSettings(DataType.MSD);
				} else {
					configuration = globalConfiguration;
				}
				slopes = PeakDetectorMSD.getFirstDerivativeSlopes(new ChromatogramSelectionMSD((IChromatogramMSD)measurement), configuration.getMovingAverageWindowSize(), PeakDetectorMSD.getIonFilter(configuration.getFilterIon(), configuration.getFilterMode()));
			} else if(measurement instanceof IChromatogramCSD) {
				if(globalConfiguration == null) {
					configuration = new FirstDerivativePeakDetectorSettings(DataType.CSD);
				} else {
					configuration = globalConfiguration;
				}
				slopes = PeakDetectorCSD.getFirstDerivativeSlopes(new ChromatogramSelectionCSD((IChromatogramCSD)measurement), configuration.getMovingAverageWindowSize());
			} else if(measurement instanceof IChromatogramWSD) {
				if(globalConfiguration == null) {
					configuration = new FirstDerivativePeakDetectorSettings(DataType.WSD);
				} else {
					configuration = globalConfiguration;
				}
				slopes = PeakDetectorWSD.getFirstDerivativeSlopes(new ChromatogramSelectionWSD((IChromatogramWSD)measurement), configuration.getMovingAverageWindowSize());
			} else if(measurement instanceof SpectrumMeasurement) {
				if(globalConfiguration == null) {
					configuration = new FirstDerivativePeakDetectorSettings(DataType.NMR);
				} else {
					configuration = globalConfiguration;
				}
				slopes = getSignalSlopes(((SpectrumMeasurement)measurement).getSignals(), configuration.getMovingAverageWindowSize());
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

		return "Implementation of a first derivative peak detector.";
	}

	private static IFirstDerivativeDetectorSlopes getSignalSlopes(List<? extends ISignal> signals, WindowSize windowSize) {

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
