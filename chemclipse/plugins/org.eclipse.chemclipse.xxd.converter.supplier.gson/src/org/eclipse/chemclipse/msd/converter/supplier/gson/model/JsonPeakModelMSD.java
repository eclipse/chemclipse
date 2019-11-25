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
package org.eclipse.chemclipse.msd.converter.supplier.gson.model;

import java.io.IOException;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

public class JsonPeakModelMSD extends PeakModelMSD {

	private static final String KEY_PEAK_MODEL_MASS_SPECTRUM = "massspectrum";
	private static final String KEY_PEAK_MODEL_INTENSITIES = "intensities";
	private static final long serialVersionUID = 1L;

	private JsonPeakModelMSD(IPeakMassSpectrum peakMaximum, IPeakIntensityValues peakIntensityValues) throws IllegalArgumentException, PeakException {
		super(peakMaximum, peakIntensityValues);
	}

	public static void writePeakModel(JsonWriter writer, IPeakModelMSD peakModel) throws IOException {

		writer.beginObject();
		writer.name(KEY_PEAK_MODEL_MASS_SPECTRUM);
		JsonMassspectrum.writeMassspectrum(writer, peakModel.getPeakMassSpectrum());
		writer.name(KEY_PEAK_MODEL_INTENSITIES);
		writeIntensities(writer, peakModel);
		writer.endObject();
	}

	public static IPeakModelMSD readPeakModel(JsonElement jsonElement) throws IOException {

		JsonObject object = jsonElement.getAsJsonObject();
		IPeakIntensityValues intensities = readIntensities(object.get(KEY_PEAK_MODEL_INTENSITIES).getAsJsonArray());
		try {
			return new JsonPeakModelMSD(JsonMassspectrum.readMassspectrum(object.get(KEY_PEAK_MODEL_MASS_SPECTRUM)), intensities);
		} catch(IllegalArgumentException e) {
			throw new IOException("can't read peaks from file", e);
		}
	}

	private static void writeIntensities(JsonWriter json, IPeakModelMSD peakModel) throws IOException {

		json.beginArray();
		List<Integer> retentionTimes = peakModel.getRetentionTimes();
		for(Integer rt : retentionTimes) {
			float abundance = peakModel.getPeakAbundance(rt);
			json.value(rt.floatValue());
			json.value(abundance);
		}
		json.endArray();
	}

	private static IPeakIntensityValues readIntensities(JsonArray json) {

		PeakIntensityValues intensityValues = new PeakIntensityValues(Float.MAX_VALUE);
		for(int i = 0; i < json.size(); i += 2) {
			int rt = json.get(i).getAsInt();
			float abundance = json.get(i + 1).getAsFloat();
			intensityValues.addIntensityValue(rt, abundance);
		}
		intensityValues.normalize();
		return intensityValues;
	}
}
