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
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;

public class ScanTargetReference implements TargetReference {

	public static final String TYPE_SCAN = "Scan";
	public static final String TYPE_PEAK = "Peak";
	private static final NumberFormat FORMAT = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
	private final IScan scan;
	private final String name;
	private final String id;
	private final String type;

	public ScanTargetReference(IScan scan, String type) {
		this.scan = scan;
		this.type = type;
		name = FORMAT.format(TimeUnit.MILLISECONDS.toMinutes(scan.getRetentionTime()));
		id = type + "." + String.valueOf(scan.getRetentionTime());
	}

	@Override
	public Set<IIdentificationTarget> getTargets() {

		return scan.getTargets();
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public String getType() {

		return type;
	}

	@Override
	public String getID() {

		return id;
	}

	public static List<ScanTargetReference> getScanReferences(List<? extends IScan> items) {

		return getReferences(items, TYPE_SCAN, scan -> scan);
	}

	public static List<ScanTargetReference> getPeakReferences(List<? extends IPeak> items) {

		return getReferences(items, TYPE_PEAK, peak -> peak.getPeakModel().getPeakMaximum());
	}

	public static <T> List<ScanTargetReference> getReferences(List<T> items, String type, Function<T, IScan> conversionFunction) {

		List<ScanTargetReference> list = new ArrayList<>();
		for(T item : items) {
			IScan scan = conversionFunction.apply(item);
			if(scan != null) {
				list.add(new ScanTargetReference(scan, type));
			}
		}
		return list;
	}

	public IScan getScan() {

		return scan;
	}
}
