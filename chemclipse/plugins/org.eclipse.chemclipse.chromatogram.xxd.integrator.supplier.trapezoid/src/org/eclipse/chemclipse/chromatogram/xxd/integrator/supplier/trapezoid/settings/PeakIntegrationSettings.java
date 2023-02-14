/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - list ions to integrate
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid.settings;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.peaks.AbstractPeakIntegrationSettings;
import org.eclipse.chemclipse.model.core.IMarkedTrace;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.model.core.MarkedTrace;
import org.eclipse.chemclipse.support.settings.LabelProperty;
import org.eclipse.chemclipse.support.settings.StringSettingsProperty;
import org.eclipse.chemclipse.support.util.TraceSettingUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PeakIntegrationSettings extends AbstractPeakIntegrationSettings {

	@JsonIgnore
	private static final String TIC = "0";
	//
	@JsonProperty(value = "Traces to Integrate", defaultValue = TIC)
	@LabelProperty(value = "%TracesToIntegrate", tooltip = "%TracesToIntegrateDescription")
	@StringSettingsProperty(regExp = "(\\d+[;|\\s]?)+", description = "%MustBeSpaceSeparatedDigits", isMultiLine = false, allowEmpty = false)
	private String tracesToIntegrate = TIC;
	/*
	 * The selected ions are handled separately.
	 * They must not be persisted. If selected ions is
	 * empty, TIC will be integrated.
	 */
	@JsonIgnore
	private IMarkedTraces<IMarkedTrace> markedTraces = null;

	@Override
	public IMarkedTraces<IMarkedTrace> getMarkedTraces() {

		if(markedTraces == null) {
			markedTraces = super.getMarkedTraces();
			if(!tracesToIntegrate.equals(TIC)) {
				TraceSettingUtil traceSettingUtil = new TraceSettingUtil();
				int[] traces = traceSettingUtil.extractTraces(traceSettingUtil.deserialize(tracesToIntegrate));
				for(int trace : traces) {
					markedTraces.add(new MarkedTrace(trace));
				}
			}
		}
		//
		return markedTraces;
	}

	public void setSelectedTraces(String tracesToIntegrate) {

		this.tracesToIntegrate = tracesToIntegrate;
	}

	@JsonProperty(value = "Include Background", defaultValue = "false")
	@LabelProperty(value = "%IncludeBackground", tooltip = "%IncludeBackgroundDescription")
	private boolean includeBackground = false;

	public boolean isIncludeBackground() {

		return includeBackground;
	}

	public void setIncludeBackground(boolean includeBackground) {

		this.includeBackground = includeBackground;
	}

	@JsonProperty(value = "Area Constraint", defaultValue = "true")
	@LabelProperty(value = "%AreaConstraint", tooltip = "%AreaConstraintDescription")
	private boolean useAreaConstraint = true;

	public boolean isUseAreaConstraint() {

		return useAreaConstraint;
	}

	public void setUseAreaConstraint(boolean useAreaConstraint) {

		this.useAreaConstraint = useAreaConstraint;
	}
}