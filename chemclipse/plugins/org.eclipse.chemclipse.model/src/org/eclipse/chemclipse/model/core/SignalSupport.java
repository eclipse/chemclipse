/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved.
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class SignalSupport {

	public static String asText(double signal, DecimalFormat decimalFormat) {

		if(signal == ISignal.TOTAL_INTENSITY) {
			return ISignal.TOTAL_INTENSITY_DESCRIPTION;
		} else {
			return decimalFormat.format(signal);
		}
	}

	public static String asText(List<Double> signals, DecimalFormat decimalFormat) {

		if(signals.isEmpty()) {
			return ISignal.TOTAL_INTENSITY_DESCRIPTION;
		} else if(signals.size() == 1 && signals.get(0) == ISignal.TOTAL_INTENSITY) {
			return ISignal.TOTAL_INTENSITY_DESCRIPTION;
		} else {
			return signals.stream().sorted().map(value -> decimalFormat.format(value)).collect(Collectors.joining(", "));
		}
	}

	public static int compare(List<Double> signals1, List<Double> signals2) {

		int sortOrder = 0;
		int minSize = Math.min(signals1.size(), signals2.size());
		//
		exitloop:
		for(int i = 0; i < minSize; i++) {
			if(sortOrder == 0) {
				sortOrder = Double.compare(signals1.get(i), signals2.get(i));
			} else {
				break exitloop;
			}
		}
		//
		return sortOrder;
	}
}