/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.db;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PathManager {

	private static final String MSMS_DATA = "msms_data_";
	private Path projectDir;

	public PathManager(Path projectDir) {
		this.projectDir = projectDir;
	}

	public Path getMSdir(long msId) throws IOException {

		Path msDir = Paths.get(projectDir.toString(), "ms_" + msId);
		if(!Files.isDirectory(msDir)) {
			Files.createDirectory(msDir);
		}
		return msDir;
	}

	public List<Long> getMSidList() throws IOException {

		List<Long> list = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(projectDir, "ms_*")) {
			for(Path msDir : stream) {
				String name = msDir.getName(msDir.getNameCount() - 1).toString();
				String msId = name.substring(3, name.length());
				list.add(Long.parseLong(msId));
			}
		}
		return list;
	}

	public List<Long> getMSMSidList(long msId) throws IOException {

		List<Long> list = new ArrayList<>();
		Path msDir = getMSdir(msId);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(msDir, MSMS_DATA + "*")) {
			for(Path msmsDir : stream) {
				String name = msmsDir.getName(msmsDir.getNameCount() - 1).toString();
				String msmsId = name.substring(MSMS_DATA.length(), name.length());
				list.add(Long.parseLong(msmsId));
			}
		}
		return list;
	}

	public Path getMSpeaksFile(long msId) throws IOException {

		Path msPeaksFile = Paths.get(getMSdir(msId).toString(), "ms_peaks.bin");
		return msPeaksFile;
	}

	public Path getMSdataFile(long msId) throws IOException {

		Path msDataFile = Paths.get(getMSdir(msId).toString(), "ms_data.bin");
		return msDataFile;
	}

	public Path getMSMSdataFile(long msId, long msmsId) throws IOException {

		Path msDir = getMSdir(msId);
		Path msmsDataFile = Paths.get(msDir.toString(), MSMS_DATA + msmsId);
		return msmsDataFile;
	}

	public Path getMSMSpeaksFile(long msId, long msmsId) throws IOException {

		Path peaksPath = Paths.get(getMSdir(msId).toString(), "msms_peaks_" + msmsId);
		return peaksPath;
	}
}
