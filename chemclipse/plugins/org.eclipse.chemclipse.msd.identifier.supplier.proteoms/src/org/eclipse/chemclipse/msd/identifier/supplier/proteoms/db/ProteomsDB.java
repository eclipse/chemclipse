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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.Peak;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMS;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMSMS;

public class ProteomsDB {

	private static final Logger log = Logger.getLogger(ProteomsDB.class);
	private Path projectDir;
	private PathManager pathManager;

	public ProteomsDB(Path projectDir) {
		this.projectDir = projectDir;
		pathManager = new PathManager(projectDir);
	}

	public Path getProjectDir() {

		return projectDir;
	}

	public void saveAllSpectrumsMS(List<SpectrumMS> spectrums) throws IOException {

		saveSpectrumsMS(spectrums, true, true, true);
	}

	public void saveSpectrumsMS(List<SpectrumMS> spectrums, boolean withMSpeaks, boolean withMSMS, boolean withMSMSpeaks) throws IOException {

		for(SpectrumMS ms : spectrums) {
			// Path msDir = pathManager.getMSdir(ms.getId());
			saveSpectrumMS(ms, ms.getId());
			if(withMSpeaks)
				saveSpectrumMSpeaks(ms.getPeaks(), ms.getId());
			if(withMSMS)
				saveSpectrumsMSMS(ms.getMsmsSpectrumsChildren(), ms.getId(), withMSMSpeaks);
		}
	}

	public void saveSpectrumMSpeaks(List<Peak> peaks, long msId) throws IOException {

		if(peaks == null || peaks.isEmpty()) {
			return;
		}
		Path msDataFile = pathManager.getMSpeaksFile(msId);
		// Path msDataFile = Paths.get(msDir.toString(), "ms_data_peaks.bin");
		DataOutputStream out = null;
		try {
			out = getDataOutStream(msDataFile);
			writePeaks(peaks, out);
		} catch(IOException e) {
			log.error("Error: " + e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	private void saveSpectrumMS(SpectrumMS ms, long msId) throws IOException {

		Path msDataFile = pathManager.getMSdataFile(msId);
		// Path msDataFile = Paths.get(msDir.toString(), "ms_data.bin");
		DataOutputStream out = null;
		try {
			out = getDataOutStream(msDataFile);
			write(ms, out);
		} catch(IOException e) {
			log.error("Error: " + e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	private void saveSpectrumsMSMS(List<SpectrumMSMS> msmsList, long msId, boolean withPeaks) throws IOException {

		if(msmsList == null || msmsList.isEmpty()) {
			return;
		}
		for(SpectrumMSMS msms : msmsList) {
			saveSpectrumMSMS(msms, msId, withPeaks);
		}
	}

	private void saveSpectrumMSMS(SpectrumMSMS msms, long msId, boolean withPeaks) throws IOException {

		Path msmsDataFile = pathManager.getMSMSdataFile(msId, msms.getId());
		DataOutputStream out = null;
		try {
			out = getDataOutStream(msmsDataFile);
			writeMSMS(msms, out);
			if(withPeaks) {
				Path peaksPath = pathManager.getMSMSpeaksFile(msId, msms.getId());
				writePeaks(msms.getPeaks(), peaksPath);
			}
		} catch(IOException e) {
			log.error("Error: " + e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	private DataOutputStream getDataOutStream(Path msDataFile) throws FileNotFoundException {

		return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(msDataFile.toFile())));
	}

	public List<SpectrumMS> getMSandMSMSwithoutPeaks() throws IOException {

		return getSpectrumsMS(false, true, false);
	}

	public List<SpectrumMS> getSpectrumsMS(boolean withMSPeaks, boolean withMSMS, boolean withMSMSpeaks) throws IOException {

		List<SpectrumMS> list = new ArrayList<>();
		List<Long> msIds = pathManager.getMSidList();
		for(Long msId : msIds) {
			SpectrumMS ms = getSpectrumMS(msId);
			list.add(ms);
			if(withMSPeaks) {
				List<Peak> peaks = getSpectrumMSpeaks(msId);
				ms.setPeaks(peaks);
			}
			if(withMSMS) {
				List<SpectrumMSMS> msmsList = getSpectrumsMSMS(msId, withMSMSpeaks);
				ms.setMsmsSpectrumsChildren(msmsList);
			}
		}
		return list;
	}

	private SpectrumMS getSpectrumMS(long msId) throws IOException {

		Path msDataFile = pathManager.getMSdataFile(msId);
		DataInputStream in = null;
		SpectrumMS ms = new SpectrumMS();
		try {
			in = getDataInStream(msDataFile);
			readSpectrumMS(ms, in);
			return ms;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private List<SpectrumMSMS> getSpectrumsMSMS(long msId, boolean withMSMSPeaks) throws IOException {

		List<SpectrumMSMS> list = new ArrayList<>();
		List<Long> msmsIdlist = pathManager.getMSMSidList(msId);
		for(Long msmsId : msmsIdlist) {
			SpectrumMSMS msms = getSpectrumMSMS(msId, msmsId);
			list.add(msms);
			if(withMSMSPeaks) {
				List<Peak> peaks = getSpectrumMSMSpeaks(msId, msmsId);
				msms.setPeaks(peaks);
			}
		}
		return list;
	}

	private List<Peak> getSpectrumMSMSpeaks(long msId, long msmsId) throws NotSerializableException, IOException {

		DataInputStream in = null;
		try {
			Path msmsPeaksFile = pathManager.getMSMSpeaksFile(msId, msmsId);
			if(Files.exists(msmsPeaksFile)) {
				in = getDataInStream(msmsPeaksFile);
				List<Peak> peaks = readPeaks(in);
				return peaks;
			} else {
				return null;
			}
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private DataInputStream getDataInStream(Path file) throws FileNotFoundException {

		return new DataInputStream(new BufferedInputStream(new FileInputStream(file.toFile())));
	}

	private SpectrumMSMS getSpectrumMSMS(long msId, long msmsId) throws IOException {

		Path msmsDataFile = pathManager.getMSMSdataFile(msId, msmsId);
		DataInputStream in = null;
		try {
			in = getDataInStream(msmsDataFile);
			SpectrumMSMS msms = readSpectrumMSMS(in);
			return msms;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private List<Peak> getSpectrumMSpeaks(Long msId) throws IOException {

		Path msDataFile = pathManager.getMSpeaksFile(msId);
		if(!Files.isRegularFile(msDataFile)) {
			return null;
		}
		DataInputStream in = null;
		List<Peak> peaks = null;
		try {
			in = getDataInStream(msDataFile);
			peaks = readPeaks(in);
			return peaks;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private void writePeaks(List<Peak> peaks, DataOutputStream out) throws IOException, NotSerializableException {

		out.writeInt(Peak.version);
		switch(Peak.version) {
			case 1:
				out.writeInt(peaks.size());
				for(Peak p : peaks) {
					writePeak(p, out);
				}
				break;
			default:
				throw new NotSerializableException("Not implemented Peak version: " + SpectrumMS.version);
		}
	}

	private void write(SpectrumMS ms, DataOutputStream out) throws IOException, NotSerializableException {

		out.writeInt(SpectrumMS.version);
		switch(SpectrumMS.version) {
			case 1:
				out.writeLong(ms.getId());
				out.writeUTF(ms.getName());
				break;
			default:
				throw new NotSerializableException("Not implemented MS version: " + SpectrumMS.version);
		}
	}

	private void writePeaks(List<Peak> peaks, Path peaksPath) throws FileNotFoundException {

		if(peaks != null && !peaks.isEmpty()) {
			DataOutputStream outPeaks = null;
			try {
				outPeaks = getDataOutStream(peaksPath);
				writePeaks(peaks, outPeaks);
			} catch(IOException e) {
				log.error("Error: " + e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(outPeaks);
			}
		}
	}

	private void writePeak(Peak p, DataOutputStream out) throws IOException {

		if(p == null) {
			out.writeDouble(-1);
			out.writeDouble(-1);
		} else {
			out.writeDouble(p.getMz());
			out.writeDouble(p.getIntensity());
		}
	}

	private void readSpectrumMS(SpectrumMS ms, DataInputStream in) throws IOException, NotSerializableException {

		int version = in.readInt();
		switch(version) {
			case 1:
				ms.setId(in.readLong());
				ms.setName(in.readUTF());
				break;
			default:
				throw new NotSerializableException("Not implemented MS version: " + version);
		}
	}

	private SpectrumMSMS readSpectrumMSMS(DataInputStream in) throws IOException {

		int version = in.readInt();
		switch(version) {
			case 1:
				SpectrumMSMS msms = new SpectrumMSMS();
				msms.setId(in.readLong());
				msms.setName(in.readUTF());
				Peak precursorPeak = readPeak(in);
				if(precursorPeak.getMz() != -1 && precursorPeak.getIntensity() != -1) {
					// It is null
					msms.setPrecursorPeak(precursorPeak);
				}
				return msms;
			default:
				throw new NotSerializableException("Not implemented MSMS version: " + SpectrumMSMS.version);
		}
	}

	private Peak readPeak(DataInputStream in) throws IOException {

		return new Peak(in.readDouble(), in.readDouble());
	}

	private void writeMSMS(SpectrumMSMS msms, DataOutputStream out) throws IOException {

		out.writeInt(SpectrumMSMS.version);
		switch(SpectrumMSMS.version) {
			case 1:
				out.writeLong(msms.getId());
				out.writeUTF(msms.getName());
				writePeak(msms.getPrecursorPeak(), out);
				break;
			default:
				throw new NotSerializableException("Not implemented MSMS version: " + SpectrumMSMS.version);
		}
	}

	private List<Peak> readPeaks(DataInputStream in) throws IOException, NotSerializableException {

		List<Peak> peaks;
		int version = in.readInt();
		switch(version) { // Peak.version
			case 1:
				int peaksSize = in.readInt();
				peaks = new ArrayList<>(peaksSize);
				for(int i = 0; i < peaksSize; i++) {
					peaks.add(readPeak(in));
				}
				break;
			default:
				throw new NotSerializableException("Not implemented Peak version: " + version);
		}
		return peaks;
	}
}
