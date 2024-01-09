/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.system;

import org.eclipse.chemclipse.processing.system.ISystemProcessSettings;
import org.eclipse.chemclipse.support.settings.FloatSettingsProperty;
import org.eclipse.chemclipse.support.settings.IntSettingsProperty;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class TargetsProcessSettings implements ISystemProcessSettings {

	@JsonProperty(value = "Use Target List", defaultValue = "true")
	@JsonPropertyDescription(value = "Use the target list for auto-completion.")
	private boolean useTargetList = true;
	@JsonProperty(value = "Propagate Target on Update", defaultValue = "false")
	@JsonPropertyDescription(value = "Send updates on selection.")
	private boolean propagateTargetOnUpdate = false;
	@JsonProperty(value = "Table Sortable", defaultValue = "false")
	@JsonPropertyDescription(value = "Sort the results")
	private boolean targetsTableSortable = false;
	@JsonProperty(value = "Show Deviation Retention Time", defaultValue = "false")
	@JsonPropertyDescription(value = "Show the retention time deviation in brackets.")
	private boolean showDeviationRI = false;
	@JsonProperty(value = "Show Deviation Retention Index", defaultValue = "false")
	@JsonPropertyDescription(value = "Show the retention index deviation in brackets.")
	private boolean showDeviationRT = false;
	//
	@JsonProperty(value = "Retention Time: Use absolute deviation", defaultValue = "false")
	@JsonPropertyDescription(value = "Use an absolute or relative calculation.")
	private boolean useAbsoluteDeviationRetentionTime = false;
	@JsonProperty(value = "Allowed Deviation RT [%]", defaultValue = "20")
	@JsonPropertyDescription(value = "Percentage of allowed deviation (OK).")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_DEVIATION_RELATIVE, maxValue = PreferenceSupplier.MAX_DEVIATION_RELATIVE)
	private float retentionTimeDeviationRelOK = 20.0f;
	@JsonProperty(value = "Warn Deviation RT [%]", defaultValue = "40")
	@JsonPropertyDescription(value = "Percentage of allowed deviation (Warn).")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_DEVIATION_RELATIVE, maxValue = PreferenceSupplier.MAX_DEVIATION_RELATIVE)
	private float retentionTimeDeviationRelWarn = 40.0f;
	@JsonProperty(value = "Allowed Deviation RT [ms]", defaultValue = "1000")
	@JsonPropertyDescription(value = "Absolute deviation retention time in milliseconds (OK)")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_DEVIATION_RETENTION_TIME, maxValue = PreferenceSupplier.MAX_DEVIATION_RETENTION_TIME)
	private int retentionTimeDeviationAbsOK = 1000;
	@JsonProperty(value = "Warn Deviation RT [ms]", defaultValue = "2000")
	@JsonPropertyDescription(value = "Absolute deviation retention time in milliseconds (Warn)")
	@IntSettingsProperty(minValue = PreferenceSupplier.MIN_DEVIATION_RETENTION_TIME, maxValue = PreferenceSupplier.MAX_DEVIATION_RETENTION_TIME)
	private int retentionTimeDeviationAbsWarn = 2000;
	//
	@JsonProperty(value = "Retention Index: Use absolute deviation", defaultValue = "false")
	@JsonPropertyDescription(value = "Use an absolute or relative calculation.")
	private boolean useAbsoluteDeviationRetentionIndex = false;
	@JsonProperty(value = "Allowed Deviation RI [%]", defaultValue = "20")
	@JsonPropertyDescription(value = "Percentage of allowed deviation (OK).")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_DEVIATION_RELATIVE, maxValue = PreferenceSupplier.MAX_DEVIATION_RELATIVE)
	private float retentionIndexDeviationRelOK = 20.0f;
	@JsonProperty(value = "Warn Deviation RI [%]", defaultValue = "40")
	@JsonPropertyDescription(value = "Percentage of allowed deviation (Warn).")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_DEVIATION_RELATIVE, maxValue = PreferenceSupplier.MAX_DEVIATION_RELATIVE)
	private float retentionIndexDeviationRelWarn = 40.0f;
	@JsonProperty(value = "Allowed Deviation RI [abs]", defaultValue = "20")
	@JsonPropertyDescription(value = "Absolute deviation retention index (OK)")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_DEVIATION_RETENTION_INDEX, maxValue = PreferenceSupplier.MAX_DEVIATION_RETENTION_INDEX)
	private float retentionIndexDeviationAbsOK = 20.0f;
	@JsonProperty(value = "Warn Deviation RI [abs]", defaultValue = "40")
	@JsonPropertyDescription(value = "Absolute deviation retention index (Warn)")
	@FloatSettingsProperty(minValue = PreferenceSupplier.MIN_DEVIATION_RETENTION_INDEX, maxValue = PreferenceSupplier.MAX_DEVIATION_RETENTION_INDEX)
	private float retentionIndexDeviationAbsWarn = 40.0f;

	public boolean isUseTargetList() {

		return useTargetList;
	}

	public void setUseTargetList(boolean useTargetList) {

		this.useTargetList = useTargetList;
	}

	public boolean isPropagateTargetOnUpdate() {

		return propagateTargetOnUpdate;
	}

	public void setPropagateTargetOnUpdate(boolean propagateTargetOnUpdate) {

		this.propagateTargetOnUpdate = propagateTargetOnUpdate;
	}

	public boolean isTargetsTableSortable() {

		return targetsTableSortable;
	}

	public void setTargetsTableSortable(boolean targetsTableSortable) {

		this.targetsTableSortable = targetsTableSortable;
	}

	public boolean isShowDeviationRI() {

		return showDeviationRI;
	}

	public void setShowDeviationRI(boolean showDeviationRI) {

		this.showDeviationRI = showDeviationRI;
	}

	public boolean isShowDeviationRT() {

		return showDeviationRT;
	}

	public void setShowDeviationRT(boolean showDeviationRT) {

		this.showDeviationRT = showDeviationRT;
	}

	public boolean isUseAbsoluteDeviationRetentionTime() {

		return useAbsoluteDeviationRetentionTime;
	}

	public void setUseAbsoluteDeviationRetentionTime(boolean useAbsoluteDeviationRetentionTime) {

		this.useAbsoluteDeviationRetentionTime = useAbsoluteDeviationRetentionTime;
	}

	public float getRetentionTimeDeviationRelOK() {

		return retentionTimeDeviationRelOK;
	}

	public void setRetentionTimeDeviationRelOK(float retentionTimeDeviationRelOK) {

		this.retentionTimeDeviationRelOK = retentionTimeDeviationRelOK;
	}

	public float getRetentionTimeDeviationRelWarn() {

		return retentionTimeDeviationRelWarn;
	}

	public void setRetentionTimeDeviationRelWarn(float retentionTimeDeviationRelWarn) {

		this.retentionTimeDeviationRelWarn = retentionTimeDeviationRelWarn;
	}

	public int getRetentionTimeDeviationAbsOK() {

		return retentionTimeDeviationAbsOK;
	}

	public void setRetentionTimeDeviationAbsOK(int retentionTimeDeviationAbsOK) {

		this.retentionTimeDeviationAbsOK = retentionTimeDeviationAbsOK;
	}

	public int getRetentionTimeDeviationAbsWarn() {

		return retentionTimeDeviationAbsWarn;
	}

	public void setRetentionTimeDeviationAbsWarn(int retentionTimeDeviationAbsWarn) {

		this.retentionTimeDeviationAbsWarn = retentionTimeDeviationAbsWarn;
	}

	public boolean isUseAbsoluteDeviationRetentionIndex() {

		return useAbsoluteDeviationRetentionIndex;
	}

	public void setUseAbsoluteDeviationRetentionIndex(boolean useAbsoluteDeviationRetentionIndex) {

		this.useAbsoluteDeviationRetentionIndex = useAbsoluteDeviationRetentionIndex;
	}

	public float getRetentionIndexDeviationRelOK() {

		return retentionIndexDeviationRelOK;
	}

	public void setRetentionIndexDeviationRelOK(float retentionIndexDeviationRelOK) {

		this.retentionIndexDeviationRelOK = retentionIndexDeviationRelOK;
	}

	public float getRetentionIndexDeviationRelWarn() {

		return retentionIndexDeviationRelWarn;
	}

	public void setRetentionIndexDeviationRelWarn(float retentionIndexDeviationRelWarn) {

		this.retentionIndexDeviationRelWarn = retentionIndexDeviationRelWarn;
	}

	public float getRetentionIndexDeviationAbsOK() {

		return retentionIndexDeviationAbsOK;
	}

	public void setRetentionIndexDeviationAbsOK(float retentionIndexDeviationAbsOK) {

		this.retentionIndexDeviationAbsOK = retentionIndexDeviationAbsOK;
	}

	public float getRetentionIndexDeviationAbsWarn() {

		return retentionIndexDeviationAbsWarn;
	}

	public void setRetentionIndexDeviationAbsWarn(float retentionIndexDeviationAbsWarn) {

		this.retentionIndexDeviationAbsWarn = retentionIndexDeviationAbsWarn;
	}
}