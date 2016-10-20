/**
 * BaaS heating anomaly detection example
 * 
 * copyright Kieback&Peter GmbH & Co KG (2016) http://www.kieback-peter.de/
 * 
 * Sie können diese Datei unter folgenden Bedingungen weiterverwenden:
 * 
 * Die Datei wurde unter der Lizenz
 * „Creative Commons Namensnennung-Weitergabe unter gleichen Bedingungen Deutschland“
 * in Version 3.0 (abgekürzt „CC-by-sa 3.0/de“) veröffentlicht.
 * 
 * Den rechtsverbindlichen Lizenzvertrag finden Sie unter http://creativecommons.org/licenses/by-sa/3.0/de/legalcode.
 * 
 * Es folgt eine vereinfachte Zusammenfassung des Vertrags in allgemeinverständlicher Sprache ohne juristische Wirkung.
 * 
 * Es ist Ihnen gestattet,
 * - das Werk zu vervielfältigen, zu verbreiten und öffentlich zugänglich zu machen sowie
 * - Abwandlungen und Bearbeitungen des Werkes anzufertigen,
 * 
 * sofern Sie folgende Bedingungen einhalten:
 * Namensnennung: Sie müssen den Urheber bzw. den Rechteinhaber in der von ihm festgelegten Weise, die URI (z. B. die Internetadresse dieser Seite) sowie den Titel des Werkes und bei einer Abwandlung einen Hinweis darauf angeben.
 *
 * Weitergabe unter gleichen Bedingungen: Wenn Sie das lizenzierte Werk bearbeiten, abwandeln oder als Vorlage für ein neues Werk verwenden, dürfen Sie die neu entstandenen Werke nur unter dieser oder einer zu dieser kompatiblen Lizenz nutzen und weiterverbreiten.
 * 
 * Lizenzangabe: Sie müssen anderen alle Lizenzbedingungen mitteilen, die für dieses Werk gelten. Am einfachsten ist es, wenn Sie dazu einen Link auf den Lizenzvertrag (siehe oben) einbinden.
 *
 * Bitte beachten Sie, dass andere Rechte die Weiterverwendung einschränken können.
 * 
 */

package kup.anomalies.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import kup.anomalies.SupplyReturnTempDiff;
import kup.anomalies.SupplyTempToHigh;
import kup.anomalies.SupplyTempToLow;
import kup.timeseries.CrossSample;
import kup.timeseries.CrossSampleException;
import kup.timeseries.DataPoint;
import kup.timeseries.TimeSeries;
import kup.timeseries.TimeSeriesCsvReader;
import kup.timeseries.TimeSeriesException;
import kup.timeseries.function.FakeBoolLine;
import kup.timeseries.function.K;

public class AnomaliesView extends ApplicationFrame {
	private static final long serialVersionUID = 1L;

	public AnomaliesView(String title, Dimension preferredSize, java.awt.Paint paints[],
			kup.timeseries.TimeSeries tss[], boolean printShapes) {
		super("anomaly view");
		XYDataset dataset = createDataset(title, tss);
		JFreeChart chart = createChart(title, dataset, printShapes);
		for (int i = 0; i < paints.length; i++) {
			setSeriesPaint(chart, i, paints[i]);
		}
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(preferredSize);
		chartPanel.setMouseZoomable(true, false);
		setContentPane(chartPanel);
	}

	private XYDataset createDataset(String title, kup.timeseries.TimeSeries tss[]) {
		TimeSeriesCollection ret = new TimeSeriesCollection();
		for (int i = 0; i < tss.length; i++) {
			ret.addSeries(TimeSeriesConverter.convert(tss[i], i == tss.length - 1 ? title : null));
		}
		return ret;
	}

	private void setSeriesPaint(JFreeChart chart, int series, Paint paint) {
		XYPlot plot = (XYPlot) chart.getPlot();
		XYItemRenderer r = plot.getRenderer();
		r.setSeriesPaint(series, paint);
	}

	private JFreeChart createChart(String title, XYDataset dataset, boolean printShapes) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Date", "Value", dataset, true, false, false);
		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			if (printShapes) {
				renderer.setBaseShapesVisible(true);
				renderer.setBaseShapesFilled(true);
			} else {
				renderer.setSeriesShapesVisible(0, true);
				//Shape cross = ShapeUtilities.createDiagonalCross(3, 1);
				//renderer.setSeriesShape(0, cross);
			}
			renderer.setDrawSeriesLineAsPath(true);
		}

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat(DataPoint.dateFormat));
		axis.setVerticalTickLabels(true);

		return chart;
	}

	public static void view(String title, Dimension preferredSize, Paint paints[], kup.timeseries.TimeSeries tss[], boolean printShapes) {
		AnomaliesView view = new AnomaliesView(title, preferredSize, paints, tss, printShapes);
		view.pack();
		RefineryUtilities.positionFrameRandomly(view);
		view.setVisible(true);
	}

	private static TimeSeries fakeBoolLineScaleUp(TimeSeries anomaly) {
		FakeBoolLine fakeBool = new FakeBoolLine(anomaly, 0.5f);
		K anomalyScaledUp = new K(fakeBool.compute(), 30.0f);
		return anomalyScaledUp.compute();
	}

	private static void view(String prefix, String filenames[], Date start, Date end, boolean printShapes) {

		try {
			TimeSeries tSupSetptRaw = TimeSeriesCsvReader.read(filenames[0], "").getPeriod(start, end, "supplyTempSetpoint");
			TimeSeries tSupRaw = TimeSeriesCsvReader.read(filenames[1], "").getPeriod(start, end, "supplyTemp");
			TimeSeries tRetRaw = TimeSeriesCsvReader.read(filenames[2], "").getPeriod(start, end, "returnTemp");
			TimeSeries tOutdRaw = TimeSeriesCsvReader.read(filenames[3], "").getPeriod(start, end, "outdoorTemp");
			Vector<TimeSeries> tss = new Vector<TimeSeries>();
			tss.addElement(tSupSetptRaw);
			tss.addElement(tSupRaw);
			tss.addElement(tRetRaw);
			tss.addElement(tOutdRaw);
			CrossSample cs = new CrossSample(tss, "");
			Vector<TimeSeries> tssCs = cs.compute();
			TimeSeries tSupSetpt = tssCs.elementAt(0);
			TimeSeries tSup = tssCs.elementAt(1);
			TimeSeries tRet = tssCs.elementAt(2);
			TimeSeries tOutd = tssCs.elementAt(3);

			TimeSeries anomaly1 = SupplyTempToLow.compute(tSup, tSupSetpt, 1800.0f, 0.15f);
			//System.out.println(anomaly1.name());
			//System.out.println(anomaly1);

			TimeSeries anomaly1_tss[] = { tSupSetptRaw, tSupRaw, fakeBoolLineScaleUp(anomaly1) };
			Paint anomaly1_paints[] = { Color.DARK_GRAY, Color.RED, Color.YELLOW };
			view(prefix + "supply temp too low", new java.awt.Dimension(560, 370), anomaly1_paints, anomaly1_tss, printShapes);

			TimeSeries anomaly2 = SupplyTempToHigh.compute(tSup, tSupSetpt, 1800.0f, 0.15f);
			//System.out.println(anomaly2.name());
			//System.out.println(anomaly2);
			TimeSeries anomaly2_tss[] = { tSupSetptRaw, tSupRaw, fakeBoolLineScaleUp(anomaly2) };
			view(prefix + "supply temp too high", new java.awt.Dimension(560, 370), anomaly1_paints, anomaly2_tss, printShapes);

			TimeSeries anomaly3 = SupplyReturnTempDiff.compute(tSup, tRet, tSupSetpt, 1800.0f, 0.25f, tOutd, 3.0f);
			//System.out.println(anomaly3.name());
			//System.out.println(anomaly3);
			TimeSeries anomaly3_tss[] = { tSupSetptRaw, tSupRaw, tRetRaw, fakeBoolLineScaleUp(anomaly3) };
			Paint anomaly3_paints[] = { Color.DARK_GRAY, Color.RED, Color.BLUE, Color.YELLOW };
			view(prefix + "supply/return temp diff too high", new java.awt.Dimension(560, 370), anomaly3_paints, anomaly3_tss, printShapes);

		} catch (CrossSampleException | IOException | ParseException | TimeSeriesException e) {
			e.printStackTrace();
		}
	}

	/**
	 * command line arguments: start-date, end-date, dataset, printShapes(y/n)
	 * command line example: 17.11.2015 19.11.2015 2.OG y 
	 */
	public static void main(String[] args) {
		String dataPath = "C:/Kieback/Workspaces/iBMS/KuP_TW50_analysen/data/";
		HashMap<String, String[]> data = new HashMap<>();
		data.put("2.OG", new String[] {
				dataPath + "4_000_08_01_S239_01_5151.csv",
				dataPath + "4_000_08_01_S239_01_5102.csv",
				dataPath + "4_000_08_01_S325_01_1.csv",
				dataPath + "4_000_08_01_S239_01_5103.csv", });
		data.put("4.OG", new String[] {
				dataPath + "4_000_10_01_S239_01_5151.csv",
				dataPath + "4_000_10_01_S239_01_5102.csv",
				dataPath + "4_000_10_01_S325_01_1.csv",
				dataPath + "4_000_10_01_S239_01_5103.csv", });
		data.put("5.OG", new String[] {
				dataPath + "4_000_11_01_S239_01_5151.csv",
				dataPath + "4_000_11_01_S239_01_5102.csv",
				dataPath + "4_000_11_01_S325_01_1.csv",
				dataPath + "4_000_11_01_S239_01_5103.csv", });
		data.put("6.OG", new String[] {
				dataPath + "4_000_12_01_S239_01_5151.csv",
				dataPath + "4_000_12_01_S239_01_5102.csv",
				dataPath + "4_000_12_01_S325_01_1.csv",
				dataPath + "4_000_12_01_S239_01_5103.csv", });

		String dateFormat = "dd.MM.yyyy";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(2015, 10, 1, 0, 0, 0);
		Date start = cal.getTime();
		cal.set(2015, 10, 30, 23, 59, 59);
		Date end = cal.getTime();
		if (args.length >= 2) {
			try {
				start = formatter.parse(args[0]);
				end = formatter.parse(args[1]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		boolean printShapes = false;
		if (args.length > 3 && args[3].equals("y")) { printShapes = true; }
		
		if (args.length > 2 && data.containsKey(args[2])) {
			view(args[2] + " ", data.get(args[2]), start, end, printShapes);
		} else {
			for (String dataset : data.keySet()) {
				view(dataset + " ", data.get(dataset), start, end, printShapes);
			}
		}
	}
}