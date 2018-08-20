/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

import java.util.HashMap;
import java.util.Map;

import javax.swing.text.Position;

public class Well extends ScanPCR implements IWell {

	private static final long serialVersionUID = -9183326941662161376L;
	//
	private int id = 0;
	private Position position;
	private String sampleId = "";
	private String status = "";
	private String result = "";
	private String interpretation = "";
	private Map<Integer, IChannel> channels = new HashMap<>();

	@Override
	public int getId() {

		return id;
	}

	@Override
	public void setId(int id) {

		this.id = id;
	}

	@Override
	public Position getPosition() {

		return position;
	}

	@Override
	public void setPosition(Position position) {

		this.position = position;
	}

	@Override
	public String getSampleId() {

		return sampleId;
	}

	@Override
	public void setSampleId(String sampleId) {

		this.sampleId = sampleId;
	}

	@Override
	public String getStatus() {

		return status;
	}

	@Override
	public void setStatus(String status) {

		this.status = status;
	}

	@Override
	public String getResult() {

		return result;
	}

	@Override
	public void setResult(String result) {

		this.result = result;
	}

	@Override
	public String getInterpretation() {

		return interpretation;
	}

	@Override
	public void setInterpretation(String interpretation) {

		this.interpretation = interpretation;
	}

	@Override
	public Map<Integer, IChannel> getChannels() {

		return channels;
	}

	@Override
	public int compareTo(IWell well) {

		if(well != null) {
			return Integer.compare(id, well.getId());
		} else {
			return 0;
		}
	}
}
