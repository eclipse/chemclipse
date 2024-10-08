/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.internal.peaks.settings;

import org.eclipse.chemclipse.support.settings.ISettingsMigrationHandler;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.DeletePeaksFilterSettings;
import org.eclipse.chemclipse.xxd.filter.support.DeletePeakOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeletePeaksFilterSettings_v2 implements ISettingsMigrationHandler<DeletePeaksFilterSettings> {

	@JsonProperty(value = "Delete Peaks", defaultValue = "false")
	@JsonPropertyDescription(value = "Confirm to delete the peaks.")
	private boolean deletePeaks;
	@JsonProperty(value = "Unidentified Only", defaultValue = "false")
	@JsonPropertyDescription(value = "Only delete the unidentified peaks.")
	private boolean deleteUnidentifiedOnly;
	@JsonProperty(value = "Inactive Only", defaultValue = "false")
	@JsonPropertyDescription(value = "Only delete the inactive peaks.")
	private boolean deleteInactiveOnly;

	public boolean isDeletePeaks() {

		return deletePeaks;
	}

	public void setDeletePeaks(boolean deletePeaks) {

		this.deletePeaks = deletePeaks;
	}

	public boolean isDeleteUnidentifiedOnly() {

		return deleteUnidentifiedOnly;
	}

	public void setDeleteUnidentifiedOnly(boolean deleteUnidentifiedOnly) {

		this.deleteUnidentifiedOnly = deleteUnidentifiedOnly;
	}

	public boolean isDeleteInactiveOnly() {

		return deleteInactiveOnly;
	}

	public void setDeleteInactiveOnly(boolean deleteInactiveOnly) {

		this.deleteInactiveOnly = deleteInactiveOnly;
	}

	@Override
	public boolean migrateSettings(DeletePeaksFilterSettings settings, String content) {

		boolean success = true;
		try {
			ObjectMapper objectMapper = getObjectMapper();
			DeletePeaksFilterSettings_v2 version = objectMapper.readValue(content, DeletePeaksFilterSettings_v2.class);
			boolean deletePeaks = version.isDeletePeaks();
			if(deletePeaks) {
				boolean deleteUnidentifiedOnly = version.isDeleteUnidentifiedOnly();
				boolean deleteInactiveOnly = version.isDeleteInactiveOnly();
				if(deleteUnidentifiedOnly) {
					settings.setDeletePeakOption(DeletePeakOption.UNIDENTIFIED);
				} else if(deleteInactiveOnly) {
					settings.setDeletePeakOption(DeletePeakOption.INACTIVE);
				} else {
					settings.setDeletePeakOption(DeletePeakOption.ALL);
				}
			} else {
				settings.setDeletePeakOption(DeletePeakOption.NONE);
			}
		} catch(Exception e) {
			success = false;
		}
		//
		return success;
	}
}