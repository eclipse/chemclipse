/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaScalingData implements IDataModification {

	public enum Centering {
		MEAN, MEDIAN;
	}

	public enum Scaling {
		SCALING_AUTO, SCALING_LEVEL, SCALING_PARETO, SCALING_RANGE, SCALING_VAST
	}

	public enum Transformation {
		LOG10, NONE, POWER
	}

	private Centering centering;
	private boolean enableModificationData;
	private boolean onlySelected;
	private Scaling scalingType;
	private Function<Double, Double> transformation;
	private Function<Double, Double> transformationNone = d -> d;
	private Function<Double, Double> transfromationLog10 = d -> 10.0 * Math.log10(Math.abs(d));
	private Function<Double, Double> transfromationPower = d -> Math.sqrt(Math.abs(d));
	private Transformation transfromationType;

	public PcaScalingData(boolean enableModificationData) {
		this.transformation = transformationNone;
		this.transfromationType = Transformation.NONE;
		centering = Centering.MEAN;
		scalingType = Scaling.SCALING_AUTO;
		onlySelected = true;
		this.enableModificationData = enableModificationData;
	}

	private void autoScaling(IPcaResults pcaResults, boolean onlySeleted) {

		// fix it
		List<Integer> retentionTime = pcaResults.getExtractedRetentionTimes();
		List<ISample> samples = pcaResults.getSampleList();
		for(int i = 0; i < retentionTime.size(); i++) {
			final double mean = getCenteringValue(samples, i, onlySeleted);
			final double deviation = getStandartDeviation(samples, i, onlySeleted);
			for(ISample sample : samples) {
				ISampleData sampleData = sample.getSampleData().get(i);
				if(!sampleData.isEmpty() && (sample.isSelected() || !onlySeleted)) {
					double data = sampleData.getNormalizedData();
					double scaleData = 0;
					if(deviation != 0) {
						scaleData = (data - mean) / deviation;
					}
					sampleData.setNormalizedData(scaleData);
				}
			}
		}
	}

	public Centering getCentering() {

		return centering;
	}

	private double getCenteringValue(List<ISample> sample, int position, boolean onlySelected) {

		DoubleStream selectedData = sample.stream().filter(s -> s.isSelected() || !onlySelected).map(s -> s.getSampleData().get(position)).filter(d -> !d.isEmpty()).mapToDouble(d -> d.getData());
		switch(centering) {
			case MEAN:
				return selectedData.summaryStatistics().getAverage();
			case MEDIAN:
				List<Double> data = selectedData.sorted().boxed().collect(Collectors.toList());
				int lenght = data.size();
				double median = lenght % 2 == 0 ? (data.get(lenght / 2 - 1) + data.get(lenght / 2)) / 2.0 // even
						: data.get(lenght / 2); //
				return median;
			default:
				throw new RuntimeException("undefine centering");
		}
	}

	private double getMax(List<ISample> samples, int index, boolean onlySelected) {

		return samples.stream().filter(s -> s.isSelected() || !onlySelected).map(s -> s.getSampleData().get(index)).filter(d -> !d.isEmpty()).mapToDouble(s -> transformation.apply(s.getData())).summaryStatistics().getMax();
	}

	private double getMin(List<ISample> samples, int index, boolean onlySelected) {

		return samples.stream().filter(s -> s.isSelected() || !onlySelected).map(s -> s.getSampleData().get(index)).filter(d -> !d.isEmpty()).mapToDouble(s -> transformation.apply(s.getData())).summaryStatistics().getMin();
	}

	public Scaling getScalingType() {

		return scalingType;
	}

	private double getStandartDeviation(List<ISample> samples, int position, boolean onlySelected) {

		return Math.sqrt(Math.abs(getVariance(samples, position, onlySelected)));
	}

	public Transformation getTransformationType() {

		return transfromationType;
	}

	private double getVariance(List<ISample> samples, int position, boolean onlySelected) {

		List<ISampleData> sampleData = samples.stream().filter(s -> s.isSelected() || onlySelected).map(s -> s.getSampleData().get(position)).collect(Collectors.toList());
		int count = sampleData.size();
		if(count > 1) {
			final double mean = getCenteringValue(samples, position, onlySelected);
			double sum = sampleData.stream().filter(d -> !d.isEmpty()).mapToDouble(d -> {
				double data = transformation.apply(d.getData());
				return (data - mean) * (data - mean);
			}).sum();
			return sum / (count - 1);
		}
		return 0;
	}

	@Override
	public boolean isEnableModificationData() {

		return enableModificationData;
	}

	@Override
	public boolean isOnlySelected() {

		return onlySelected;
	}

	private void levelScaling(IPcaResults pcaResults, boolean onlySeleted) {

		List<Integer> retentionTime = pcaResults.getExtractedRetentionTimes();
		List<ISample> samples = pcaResults.getSampleList();
		for(int i = 0; i < retentionTime.size(); i++) {
			final double mean = getCenteringValue(samples, i, onlySeleted);
			final double deviation = getStandartDeviation(samples, i, onlySeleted);
			for(ISample sample : samples) {
				ISampleData sampleData = sample.getSampleData().get(i);
				if(!sampleData.isEmpty() && (sample.isSelected() || !onlySeleted)) {
					double data = sampleData.getNormalizedData();
					if(deviation != 0) {
						double scaleData = (data - mean) / mean;
						sampleData.setNormalizedData(scaleData);
					}
				}
			}
		}
	}

	private void paretoScaling(IPcaResults pcaResults, boolean onlySeleted) {

		List<Integer> retentionTime = pcaResults.getExtractedRetentionTimes();
		List<ISample> samples = pcaResults.getSampleList();
		for(int i = 0; i < retentionTime.size(); i++) {
			final double mean = getCenteringValue(samples, i, onlySeleted);
			final double deviationSqrt = Math.sqrt(getStandartDeviation(samples, i, onlySeleted));
			for(ISample sample : samples) {
				ISampleData sampleData = sample.getSampleData().get(i);
				if(!sampleData.isEmpty() && (sample.isSelected() || !onlySeleted)) {
					double data = sampleData.getNormalizedData();
					if(deviationSqrt != 0) {
						double scaleData = (data - mean) / deviationSqrt;
						sampleData.setNormalizedData(scaleData);
					}
				}
			}
		}
	}

	@Override
	public void process(IPcaResults pcaResults, IProgressMonitor monitor) {

		if(enableModificationData) {
			switch(scalingType) {
				case SCALING_AUTO:
					autoScaling(pcaResults, onlySelected);
					break;
				case SCALING_LEVEL:
					levelScaling(pcaResults, onlySelected);
					break;
				case SCALING_PARETO:
					paretoScaling(pcaResults, onlySelected);
					break;
				case SCALING_RANGE:
					rangeScaling(pcaResults, onlySelected);
					break;
				case SCALING_VAST:
					vastscaling(pcaResults, onlySelected);
					break;
			}
		}
	}

	private void rangeScaling(IPcaResults pcaResults, boolean onlySeleted) {

		List<Integer> retentionTime = pcaResults.getExtractedRetentionTimes();
		List<ISample> samples = pcaResults.getSampleList();
		for(int i = 0; i < retentionTime.size(); i++) {
			final double mean = getCenteringValue(samples, i, onlySeleted);
			final double max = getMax(samples, i, onlySeleted);
			final double min = getMin(samples, i, onlySeleted);
			for(ISample sample : samples) {
				ISampleData sampleData = sample.getSampleData().get(i);
				if(!sampleData.isEmpty() && (sample.isSelected() || !onlySeleted)) {
					double data = sampleData.getNormalizedData();
					if(max != min) {
						double scaleData = (data - mean) / (max - min);
						sampleData.setNormalizedData(scaleData);
					}
				}
			}
		}
	}

	public void setCentering(Centering centering) {

		this.centering = centering;
	}

	@Override
	public void setEnableModificationData(boolean enable) {

		enableModificationData = enable;
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.onlySelected = onlySelected;
	}

	public void setScalingType(Scaling scalingType) {

		this.scalingType = scalingType;
	}

	public void setTransformationType(Transformation transformation) {

		switch(transformation) {
			case NONE:
				this.transformation = transformationNone;
				break;
			case LOG10:
				this.transformation = transfromationLog10;
				break;
			case POWER:
				this.transformation = transfromationPower;
				break;
		}
		this.transfromationType = transformation;
	}

	private void vastscaling(IPcaResults pcaResults, boolean onlySeleted) {

		List<Integer> retentionTime = pcaResults.getExtractedRetentionTimes();
		List<ISample> samples = pcaResults.getSampleList();
		for(int i = 0; i < retentionTime.size(); i++) {
			final double mean = getCenteringValue(samples, i, onlySeleted);
			final double variace = getVariance(samples, i, onlySeleted);
			for(ISample sample : samples) {
				ISampleData sampleData = sample.getSampleData().get(i);
				if(!sampleData.isEmpty() && (sample.isSelected() || !onlySeleted)) {
					double data = sampleData.getNormalizedData();
					if(variace != 0) {
						double scaleData = ((data - mean) / variace) * mean;
						sampleData.setNormalizedData(scaleData);
					}
				}
			}
		}
	}
}
