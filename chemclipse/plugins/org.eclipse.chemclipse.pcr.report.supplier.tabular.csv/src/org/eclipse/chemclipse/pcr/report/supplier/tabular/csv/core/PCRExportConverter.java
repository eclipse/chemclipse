/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.report.supplier.tabular.csv.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.pcr.converter.core.AbstractPlateExportConverter;
import org.eclipse.chemclipse.pcr.converter.core.IPlateExportConverter;
import org.eclipse.chemclipse.pcr.model.core.Channel;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.Utils;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.csv.l10n.HeaderMessages;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.csv.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMapping;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.ChannelMappings;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.LogicalOperator;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.VirtualChannel;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.VirtualChannels;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.WellComparator;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.WellMapping;
import org.eclipse.chemclipse.pcr.report.supplier.tabular.model.WellMappings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class PCRExportConverter extends AbstractPlateExportConverter implements IPlateExportConverter {

	private static final Logger logger = Logger.getLogger(PCRExportConverter.class);
	//
	private static final String DESCRIPTION = "PCR CSV Export";
	//
	private DecimalFormat decimalFormat = new DecimalFormat("00" + PreferenceSupplier.getDecimalSeparator().getCharacter() + "00");

	@Override
	public IProcessingInfo<File> convert(File file, IPlate plate, IProgressMonitor monitor) {

		decimalFormat.applyPattern("#,##0.00");
		CSVFormat csvFormat = CSVFormat.RFC4180.builder() //
				.setDelimiter(PreferenceSupplier.getDelimiter().getCharacter()).build();
		IProcessingInfo<File> processingInfo = new ProcessingInfo<>();
		if(plate != null) {
			try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(file, false), csvFormat)) {
				List<String> sampleSubsets = new ArrayList<>(getSampleSubsets(plate));
				for(String sampleSubset : sampleSubsets) {
					printReport(plate, sampleSubset, csvPrinter);
					csvPrinter.println();
				}
				csvPrinter.println();
				csvPrinter.println();
				for(Entry<String, String> entry : plate.getHeaderDataMap().entrySet()) {
					if(entry.getKey().equals(IPlate.DATE)) {
						csvPrinter.print(entry.getKey() + ": " + entry.getValue());
					}
				}
				processingInfo.setProcessingResult(file);
			} catch(IOException e) {
				logger.warn(e);
				processingInfo.addErrorMessage(DESCRIPTION, "Failed to write the report.");
			}
		} else {
			processingInfo.addErrorMessage(DESCRIPTION, "The PCR plate is not available.");
		}
		return processingInfo;
	}

	private Set<String> getSampleSubsets(IPlate plate) {

		Set<String> sampleSubsets = new HashSet<>();
		for(IWell well : plate.getWells()) {
			Map<String, String> dataMap = well.getHeaderDataMap();
			String sampleSubset = dataMap.getOrDefault(IWell.SAMPLE_SUBSET, "");
			if(!"".equals(sampleSubset)) {
				sampleSubsets.add(sampleSubset);
			}
		}
		return sampleSubsets;
	}

	private void printReport(IPlate plate, String targetSubset, CSVPrinter csvPrinter) throws IOException {

		if(Utils.isEmpty(plate, targetSubset)) {
			return;
		}
		printHeader(targetSubset, csvPrinter);
		csvPrinter.println();
		printResults(plate.makeDeepCopy(), targetSubset, csvPrinter);
	}

	void printHeader(String targetSubset, CSVPrinter csvPrinter) throws IOException {

		csvPrinter.print(HeaderMessages.sample);
		ChannelMappings channelMappings = PreferenceSupplier.getChannelMappings();
		List<ChannelMapping> targetMappings = channelMappings.stream().filter(m -> StringUtils.equalsIgnoreCase(targetSubset, m.getSubset())).collect(Collectors.toList());
		List<ChannelMapping> sortedMappings = targetMappings.stream().sorted(Comparator.comparing(ChannelMapping::getChannel)).collect(Collectors.toList());
		for(ChannelMapping sortedMapping : sortedMappings) {
			csvPrinter.print(sortedMapping.getLabel());
		}
	}

	void generateVirtualChannels(Set<IWell> wells, String targetSubset) {

		WellMappings wellMapings = PreferenceSupplier.getWellMappings();
		VirtualChannels virtualChannels = PreferenceSupplier.getVirtualChannels();
		for(VirtualChannel virtualChannel : virtualChannels) {
			if(!StringUtils.equalsIgnoreCase(targetSubset, virtualChannel.getSubset())) {
				continue;
			}
			Map<Integer, IChannel> targetChannels = new HashMap<>();
			for(IWell well : wells) {
				if(!StringUtils.equalsIgnoreCase(targetSubset, well.getSampleSubset())) {
					continue;
				}
				Pattern pattern = Pattern.compile(virtualChannel.getSample(), Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(well.getSampleId());
				if(!matcher.find()) {
					continue;
				}
				List<Integer> keys = new ArrayList<>(well.getChannels().keySet());
				Collections.sort(keys);
				for(int key : keys) {
					IChannel channel = well.getChannels().get(key);
					if(virtualChannel.getSourceChannels().length == 1) {
						if(ArrayUtils.contains(virtualChannel.getSourceChannels(), key + 1)) {
							// copy a reference value from another row
							int target = virtualChannel.getTargetChannel();
							IChannel targetChannel = channel.makeDeepCopy();
							targetChannel.setId(target - 1);
							targetChannels.put(target, targetChannel);
						}
					} else {
						// add a blank to later sum up partial results from the same row
						int target = virtualChannel.getTargetChannel();
						targetChannels.put(target, null);
					}
				}
			}
			if(targetChannels.isEmpty()) {
				continue;
			}
			for(IWell well : wells) {
				if(StringUtils.equalsIgnoreCase(targetSubset, well.getSampleSubset())) {
					for(WellMapping wellMapping : wellMapings) {
						if(!StringUtils.equalsIgnoreCase(targetSubset, wellMapping.getSubset())) {
							continue;
						}
						Pattern pattern = Pattern.compile(wellMapping.getSample(), Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(well.getSampleId());
						if(!matcher.find()) {
							continue;
						}
						int target = virtualChannel.getTargetChannel();
						if(!ArrayUtils.contains(wellMapping.getChannels(), target)) {
							continue;
						}
						if(!targetChannels.containsKey(target)) {
							continue;
						}
						IChannel targetChannel = targetChannels.get(target);
						if(virtualChannel.getSourceChannels().length > 1) {
							float virtualCrossingPoint = 0;
							targetChannel = new Channel();
							for(int channel : virtualChannel.getSourceChannels()) {
								double cp = well.getChannels().get(channel - 1).getCrossingPoint();
								if(cp == 0 && virtualChannel.getLogicalOperator() == LogicalOperator.AND) {
									virtualCrossingPoint = 0;
									break;
								}
								virtualCrossingPoint += cp;
							}
							targetChannel.setCrossingPoint(virtualCrossingPoint);
						}
						if(well.getChannels().containsKey(target - 1)) {
							logger.warn("Channel " + target + " for " + well.getSampleId() + " is already defined. Skipping.");
						}
						well.getChannels().putIfAbsent(target - 1, targetChannel);
					}
				}
			}
		}
	}

	void printResults(IPlate plate, String targetSubset, CSVPrinter csvPrinter) throws IOException {

		Set<IWell> sortedWells = new TreeSet<>(new WellComparator());
		sortedWells.addAll(plate.getWells());
		generateVirtualChannels(sortedWells, targetSubset);
		WellMappings wellMapings = PreferenceSupplier.getWellMappings();
		for(IWell well : sortedWells) {
			if(StringUtils.equalsIgnoreCase(targetSubset, well.getSampleSubset())) {
				for(WellMapping wellMapping : wellMapings) {
					if(!StringUtils.equalsIgnoreCase(targetSubset, wellMapping.getSubset())) {
						continue;
					}
					Pattern pattern = Pattern.compile(wellMapping.getSample(), Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(well.getSampleId());
					if(!matcher.find()) {
						continue;
					}
					if(matcher.groupCount() > 0) {
						csvPrinter.print(matcher.group(1));
					} else {
						csvPrinter.print(matcher.group());
					}
					for(int index : wellMapping.getChannels()) {
						if(well.getChannels().containsKey(index - 1)) {
							IChannel channel = well.getChannels().get(index - 1);
							String result = "";
							if(wellMapping.getCutoff() < 0) {
								result = decimalFormat.format(channel.getCrossingPoint());
							} else {
								result = channel.getCrossingPoint() > wellMapping.getCutoff() ? wellMapping.getPositive() : wellMapping.getNegative();
							}
							csvPrinter.print(result);
						} else if(index == 0) {
							// channels are not zero based so we can use zero as a spacer and to not confuse
							csvPrinter.print("");
						}
					}
					csvPrinter.println();
				}
			}
		}
	}
}
