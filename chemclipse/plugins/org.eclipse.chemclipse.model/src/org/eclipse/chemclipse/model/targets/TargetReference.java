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
 * Philip Wenig - refactoring target label support
 *******************************************************************************/
package org.eclipse.chemclipse.model.targets;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class TargetReference implements ITargetReference {

	private static final DecimalFormat FORMAT = ValueFormat.getDecimalFormatEnglish("0.000");
	//
	private ISignal signal = null;
	private String name = "";
	private String id = "";
	private float retentionIndex = 0.0f;
	private TargetReferenceType type = TargetReferenceType.NONE;
	//
	private ITargetSupplier supplier = null;

	public static String createID(TargetReferenceType type, int retentionTime) {

		String name = FORMAT.format(retentionTime / IChromatogram.MINUTE_CORRELATION_FACTOR);
		return createID(type, name);
	}

	public static String createID(TargetReferenceType type, String name) {

		return type.label() + "." + name;
	}

	public <X extends ISignal & ITargetSupplier> TargetReference(X item, TargetReferenceType type, String name, float retentionIndex) {

		this.signal = item;
		this.supplier = item;
		this.type = type;
		this.name = name;
		this.retentionIndex = retentionIndex;
		//
		id = createID(type, name);
	}

	@Override
	public ISignal getSignal() {

		return signal;
	}

	@Override
	public Set<IIdentificationTarget> getTargets() {

		if(supplier != null) {
			return supplier.getTargets();
		} else {
			return Collections.emptySet();
		}
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public float getRetentionIndex() {

		return retentionIndex;
	}

	@Override
	public String getID() {

		return id;
	}

	@Override
	public TargetReferenceType getType() {

		return type;
	}

	public static List<TargetReference> getScanReferences(List<? extends IScan> scans, ITargetDisplaySettings targetDisplaySettings) {

		List<TargetReference> targetReferences = new ArrayList<>();
		for(IScan scan : scans) {
			if(scan != null && !scan.getTargets().isEmpty()) {
				String name = FORMAT.format(scan.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR);
				TargetReference targetReference = new TargetReference(scan, TargetReferenceType.SCAN, name, scan.getRetentionIndex());
				targetReferences.add(targetReference);
				if(!targetDisplaySettings.isMapped(targetReference)) {
					targetDisplaySettings.setVisible(targetReference, true);
				}
			}
		}
		return targetReferences;
	}

	public static List<TargetReference> getPeakReferences(Collection<? extends IPeak> peaks, ITargetDisplaySettings targetDisplaySettings) {

		List<TargetReference> targetReferences = new ArrayList<>();
		for(IPeak peak : peaks) {
			Set<IIdentificationTarget> targets = peak.getTargets();
			if(peak != null && (targets.size() > 0 || peak.getClassifier().size() > 0)) {
				IPeakModel peakModel = peak.getPeakModel();
				String name = FORMAT.format(peakModel.getRetentionTimeAtPeakMaximum() / IChromatogram.MINUTE_CORRELATION_FACTOR);
				float retentionIndex = peakModel.getPeakMaximum().getRetentionIndex();
				TargetReference targetReference = new TargetReference(peak, TargetReferenceType.PEAK, name, retentionIndex);
				targetReferences.add(targetReference);
				if(!targetDisplaySettings.isMapped(targetReference)) {
					targetDisplaySettings.setVisible(targetReference, true);
				}
			}
		}
		return targetReferences;
	}

	/**
	 * Create the visibility filter.
	 * 
	 * @param targetDisplaySettings
	 * @return {@link Predicate}
	 */
	public static Predicate<ITargetReference> createVisibilityFilter(ITargetDisplaySettings targetDisplaySettings) {

		if(targetDisplaySettings == null) {
			return always -> true;
		}
		//
		Predicate<ITargetReference> typePredicate = new Predicate<ITargetReference>() {

			@Override
			public boolean test(ITargetReference targetReference) {

				if(targetDisplaySettings.isVisible(targetReference)) {
					if(TargetReferenceType.PEAK.equals(targetReference.getType())) {
						return targetDisplaySettings.isShowPeakLabels();
					} else if(TargetReferenceType.SCAN.equals(targetReference.getType())) {
						return targetDisplaySettings.isShowScanLabels();
					}
				}
				return false;
			}
		};
		//
		return typePredicate;
	}
}
