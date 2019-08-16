/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import java.lang.annotation.Annotation;

import org.eclipse.chemclipse.support.settings.IDynamicSettingProperty;
import org.eclipse.chemclipse.support.settings.StringSelectionSettingProperty;

public class MassSpectrumComparatorDynamicSettingProperty implements IDynamicSettingProperty {

	private StringSelectionSettingProperty selectionSettingProperty;

	public MassSpectrumComparatorDynamicSettingProperty() {
		String[][] availableComparator = MassSpectrumComparator.getAvailableComparatorIds();
		int size = availableComparator.length;
		String[] ids = new String[size];
		String[] labels = new String[size];
		for(int i = 0; i < size; i++) {
			ids[i] = availableComparator[i][1];
			labels[i] = availableComparator[i][0];
		}
		selectionSettingProperty = new StringSelectionSettingProperty() {

			@Override
			public Class<? extends Annotation> annotationType() {

				return StringSelectionSettingProperty.class;
			}

			@Override
			public String[] labels() {

				return labels;
			}

			@Override
			public String[] ids() {

				return ids;
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A extends Annotation> A getAnnotation(Class<A> acls) {

		if(selectionSettingProperty.annotationType().equals(acls)) {
			return (A)selectionSettingProperty;
		}
		return null;
	}
}
