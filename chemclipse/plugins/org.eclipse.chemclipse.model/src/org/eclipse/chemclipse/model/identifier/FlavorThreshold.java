/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.util.Objects;

public class FlavorThreshold {

	private String content = "";
	private String unit = "";

	public FlavorThreshold(String content, String unit) {

		this.content = content;
		this.unit = unit;
	}

	public String getContent() {

		return content;
	}

	public void setContent(String content) {

		this.content = content;
	}

	public String getUnit() {

		return unit;
	}

	public void setUnit(String unit) {

		this.unit = unit;
	}

	@Override
	public int hashCode() {

		return Objects.hash(content, unit);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		FlavorThreshold other = (FlavorThreshold)obj;
		return Objects.equals(content, other.content) && Objects.equals(unit, other.unit);
	}

	@Override
	public String toString() {

		return "FlavorThreshold [content=" + content + ", unit=" + unit + "]";
	}
}