/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.peakpicker.localmax.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.settings.AbstractMassSpectrumFilterSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.MassSpectrumType;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.support.literature.LiteratureReference;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class LocalMaximaPeakPickerFilterSettings extends AbstractMassSpectrumFilterSettings {

	@JsonProperty(value = "Half Window Size", defaultValue = "100")
	@IntSettingsProperty(minValue = 1)
	private int halfWindowSize = 100;

	@JsonProperty(value = "S/N ratio", defaultValue = "2")
	@JsonPropertyDescription(value = "Signal to noise/ratio above which peaks are detected.")
	private int signalToNoiseRatio = 2;

	public int getHalfWindowSize() {

		return halfWindowSize;
	}

	public void setHalfWindowSize(int halfWindowSize) {

		this.halfWindowSize = halfWindowSize;
	}

	public int getSignalToNoiseRatio() {

		return signalToNoiseRatio;
	}

	public void setSignalToNoiseRatio(int signalToNoiseRatio) {

		this.signalToNoiseRatio = signalToNoiseRatio;
	}

	@Override
	public List<MassSpectrumType> appliesToMassSpectrumTypes() {

		return Arrays.asList(MassSpectrumType.PROFILE);
	}

	@Override
	public String getCategory() {

		return ICategories.PEAK_PICKER;
	}

	@Override
	public List<LiteratureReference> getLiteratureReferences() {

		List<LiteratureReference> literatureReferences = new ArrayList<>();
		literatureReferences.add(createLiteratureReference("citations-20261015T073814.ris", "https://doi.org/10.1093/bioinformatics/bts447"));
		return literatureReferences;
	}

	private static LiteratureReference createLiteratureReference(String file, String url) {

		String content;
		try {
			content = new String(LocalMaximaPeakPickerFilterSettings.class.getResourceAsStream(file).readAllBytes());
		} catch(Exception e) {
			content = url;
			Logger.getLogger(LocalMaximaPeakPickerFilterSettings.class).warn(e);
		}
		return new LiteratureReference(content);
	}
}
