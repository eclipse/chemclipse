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

import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

public class JsonMassspectrum extends PeakMassSpectrum {

	private static final String KEY_IONS = "ions";
	private static final String KEY_RI = "ri";
	private static final String KEY_RT_C2 = "rt2";
	private static final String KEY_RT_C1 = "rt1";
	private static final String KEY_RT = "rt";
	private final int rt;
	private final int rt1;
	private final int rt2;
	private final float ri;

	public JsonMassspectrum(int rt, int rt1, int rt2, float ri) {
		this.rt = rt;
		this.rt1 = rt1;
		this.rt2 = rt2;
		this.ri = ri;
	}

	@Override
	public int getRetentionTime() {

		return rt;
	}

	@Override
	public int getRetentionTimeColumn1() {

		return rt1;
	}

	@Override
	public int getRetentionTimeColumn2() {

		return rt2;
	}

	@Override
	public float getRetentionIndex() {

		return ri;
	}

	private static final long serialVersionUID = 1L;

	public static void writeMassspectrum(JsonWriter writer, IScanMSD massSpectrum) throws IOException {

		writer.beginObject();
		writer.name(KEY_RT);
		writer.value(massSpectrum.getRetentionTime());
		writer.name(KEY_RT_C1);
		writer.value(massSpectrum.getRetentionTimeColumn1());
		writer.name(KEY_RT_C2);
		writer.value(massSpectrum.getRetentionTimeColumn2());
		writer.name(KEY_RI);
		writer.value(massSpectrum.getRetentionIndex());
		writer.name(KEY_IONS);
		writer.beginArray();
		if(massSpectrum != null) {
			for(final IIon ion : massSpectrum.getIons()) {
				writer.value(ion.getIon());
				writer.value(ion.getAbundance());
			}
		}
		writer.endArray();
		writer.endObject();
	}

	public static IPeakMassSpectrum readMassspectrum(JsonElement element) throws IOException {

		JsonObject object = element.getAsJsonObject();
		PeakMassSpectrum massSpectrum = new JsonMassspectrum(object.get(KEY_RT).getAsInt(), object.get(KEY_RT_C1).getAsInt(), object.get(KEY_RT_C2).getAsInt(), object.get(KEY_RI).getAsFloat());
		JsonArray ions = object.get(KEY_IONS).getAsJsonArray();
		for(int i = 0; i < ions.size() - 1; i += 2) {
			double ion = ions.get(i).getAsDouble();
			float intensity = ions.get(i + 1).getAsFloat();
			try {
				massSpectrum.addIon(new Ion(ion, intensity));
			} catch(AbundanceLimitExceededException
					| IonLimitExceededException e) {
				throw new IOException("can't read ion", e);
			}
		}
		return massSpectrum;
	}
}
