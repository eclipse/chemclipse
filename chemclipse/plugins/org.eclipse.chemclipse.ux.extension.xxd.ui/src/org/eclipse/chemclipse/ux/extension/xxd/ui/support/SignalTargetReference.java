/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.core.runtime.IAdaptable;

public class SignalTargetReference implements TargetReference, IAdaptable {

	public static final String TYPE_SCAN = "Scan";
	public static final String TYPE_PEAK = "Peak";
	private static final NumberFormat FORMAT = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
	private final ISignal signal;
	private final String name;
	private final String id;
	private final String type;
	private final ITargetSupplier supplier;

	public <X extends ISignal & ITargetSupplier> SignalTargetReference(X item, String type, String name) {
		this.signal = item;
		this.supplier = item;
		this.type = type;
		this.name = name;
		id = type + "." + name;
	}

	@Override
	public Set<IIdentificationTarget> getTargets() {

		return supplier.getTargets();
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

	public ISignal getSignal() {

		return signal;
	}

	public static List<SignalTargetReference> getScanReferences(List<? extends IScan> items) {

		List<SignalTargetReference> list = new ArrayList<>();
		for(IScan scan : items) {
			if(scan != null && !scan.getTargets().isEmpty()) {
				String name = FORMAT.format(scan.getRetentionTime() / (1000d * 60d));
				list.add(new SignalTargetReference(scan, TYPE_SCAN, name));
			}
		}
		return list;
	}

	public static List<SignalTargetReference> getPeakReferences(List<? extends IPeak> items) {

		return getPeakReferences(items, null);
	}

	public static List<SignalTargetReference> getPeakReferences(List<? extends IPeak> items, Function<? super IPeak, IScan> ticProvider) {

		List<SignalTargetReference> list = new ArrayList<>();
		for(IPeak peak : items) {
			Set<IIdentificationTarget> targets = peak.getTargets();
			if(peak != null && (targets.size() > 0 || peak.getName() != null) || peak.getClassifier().size()>0) {
				String name = FORMAT.format(peak.getPeakModel().getRetentionTimeAtPeakMaximum() / (1000d * 60d));
				if(ticProvider == null) {
					list.add(new SignalTargetReference(peak, TYPE_PEAK, name));
				} else {
					list.add(new TICSignalTargetReference(peak, ticProvider.apply(peak), TYPE_PEAK, name));
				}
			}
		}
		return list;
	}

	public static Predicate<TargetReference> createVisibleFilter(TargetDisplaySettings settings) {

		if(settings == null) {
			return always -> true;
		}
		Predicate<TargetReference> typePredicate = new Predicate<TargetReference>() {

			@Override
			public boolean test(TargetReference reference) {

				if(settings != null) {
					if(TYPE_PEAK.equals(reference.getType())) {
						return settings.isShowPeakLabels();
					} else if(TYPE_SCAN.equals(reference.getType())) {
						return settings.isShowScanLables();
					}
				}
				return true;
			}
		};
		if(settings instanceof VisibilityTargetDisplaySettings) {
			VisibilityTargetDisplaySettings visibility = (VisibilityTargetDisplaySettings)settings;
			return typePredicate.and(visibility::isVisible);
		}
		return typePredicate;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {

		if(adapter.isInstance(signal)) {
			return adapter.cast(signal);
		}
		return null;
	}

	private static final class TICSignalTargetReference extends SignalTargetReference {

		private final IScan tic;

		public TICSignalTargetReference(IPeak peak, IScan tic, String type, String name) {
			super(peak, type, name);
			this.tic = tic;
		}

		@Override
		public ISignal getSignal() {

			if(tic != null) {
				return tic;
			} else {
				return super.getSignal();
			}
		}

		@Override
		public <T> T getAdapter(Class<T> adapter) {

			T t = super.getAdapter(adapter);
			if(t == null) {
				if(adapter.isInstance(tic)) {
					return adapter.cast(tic);
				}
			}
			return t;
		}
	}
}
