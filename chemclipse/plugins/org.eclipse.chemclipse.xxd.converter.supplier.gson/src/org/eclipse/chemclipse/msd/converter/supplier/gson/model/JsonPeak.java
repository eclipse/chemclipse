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

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMSD;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

public final class JsonPeak extends PeakMSD {

	private static final String KEY_PEAK_AREA = "area";
	private static final String KEY_PEAK_ACTIVE = "active";
	private static final String KEY_PEAK_START = "start";
	private static final String KEY_PEAK_END = "stop";
	private static final String KEY_PEAK_MAX = "max";
	private static final String KEY_PEAK_MODEL = "model";
	private static final String KEY_PEAK_CLASSIFIER = "classifier";
	private static final String KEY_PEAK_TARGETS = "targets";
	private final double area;
	private final int startRt;
	private final int maxRt;
	private final int endRt;

	private JsonPeak(IPeakModelMSD peakModel, double area, int startRt, int maxRt, int endRt) throws IllegalArgumentException {
		super(peakModel);
		this.area = area;
		this.startRt = startRt;
		this.maxRt = maxRt;
		this.endRt = endRt;
	}

	@Override
	public double getIntegratedArea() {

		double integratedArea = super.getIntegratedArea();
		if(integratedArea > 0) {
			return integratedArea;
		}
		return area;
	}

	@Override
	public int getPeakEnd() {

		return endRt;
	}

	@Override
	public int getPeakStart() {

		return startRt;
	}

	@Override
	public int getPeakMaximum() {

		return maxRt;
	}

	public static void writePeak(JsonWriter writer, IPeakMSD peak) throws IOException {

		writer.beginObject();
		writer.name(KEY_PEAK_ACTIVE);
		writer.value(peak.isActiveForAnalysis());
		writer.name(KEY_PEAK_AREA);
		writer.value(peak.getIntegratedArea());
		writer.name(KEY_PEAK_START);
		writer.value(peak.getPeakStart());
		writer.name(KEY_PEAK_END);
		writer.value(peak.getPeakEnd());
		writer.name(KEY_PEAK_MAX);
		writer.value(peak.getPeakMaximum());
		writer.name(KEY_PEAK_MODEL);
		JsonPeakModelMSD.writePeakModel(writer, peak.getPeakModel());
		writer.name(KEY_PEAK_CLASSIFIER);
		writer.beginArray();
		for(String s : peak.getClassifier()) {
			writer.value(s);
		}
		writer.endArray();
		writer.name(KEY_PEAK_TARGETS);
		JsonIdentificationTarget.writeTargets(writer, peak.getTargets());
		writer.endObject();
	}

	public static IPeakMSD readPeak(JsonElement element) throws IOException {

		JsonObject json = element.getAsJsonObject();
		final double area = json.get(KEY_PEAK_AREA).getAsDouble();
		int startRt = json.get(KEY_PEAK_START).getAsInt();
		int endRt = json.get(KEY_PEAK_END).getAsInt();
		int maxRt = json.get(KEY_PEAK_MAX).getAsInt();
		IPeakModelMSD peakModel = JsonPeakModelMSD.readPeakModel(json.get(KEY_PEAK_MODEL));
		PeakMSD peakMSD = new JsonPeak(peakModel, area, startRt, maxRt, endRt);
		for(JsonElement s : json.get(KEY_PEAK_CLASSIFIER).getAsJsonArray()) {
			peakMSD.addClassifier(s.getAsString());
		}
		peakMSD.setActiveForAnalysis(json.get(KEY_PEAK_ACTIVE).getAsBoolean());
		JsonIdentificationTarget.readTargets(json.get(KEY_PEAK_TARGETS).getAsJsonArray(), peakMSD.getTargets()::add);
		return peakMSD;
	}
}