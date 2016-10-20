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

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import kup.anomalies.SupplyReturnTempDiff;
import kup.anomalies.SupplyTempToHigh;
import kup.anomalies.SupplyTempToLow;
import kup.timeseries.CrossSampleException;
import kup.timeseries.DataPoint;
import kup.timeseries.TimeSeries;
import kup.timeseries.TimeSeriesException;


public class AnomaliesTest {
	
	private void tsAdd(TimeSeries ts, Calendar cal, float value) throws TimeSeriesException {
		ts.addDataPoint(new DataPoint(cal.getTime(), value));
	}
	
	@Test
	public void test() throws TimeSeriesException, CrossSampleException {
		GregorianCalendar cal = new GregorianCalendar();

		TimeSeries tSupSetpt = new TimeSeries("tSupSetpt");
		TimeSeries tSup = new TimeSeries("tSup");
		TimeSeries tRet = new TimeSeries("tRet");
		TimeSeries tOutd = new TimeSeries("tOutd");
		TimeSeries expected1 = new TimeSeries("expected1");
		TimeSeries expected2 = new TimeSeries("expected2");
		TimeSeries expected3 = new TimeSeries("expected3");

		cal.set(2016, 2, 8, 0, 0, 0);
		tsAdd(tOutd, cal, 8.0f);
		cal.set(2016, 2, 8, 6, 0, 0);
		tsAdd(tOutd, cal, 8.0f);
		cal.set(2016, 2, 8, 12, 0, 0);
		tsAdd(tOutd, cal, 8.0f);
		cal.set(2016, 2, 8, 18, 0, 0);
		tsAdd(tOutd, cal, 8.0f);
		cal.set(2016, 2, 9, 0, 0, 0);
		tsAdd(tOutd, cal, 9.0f);
		cal.set(2016, 2, 9, 6, 0, 0);
		tsAdd(tOutd, cal, 9.0f);
		cal.set(2016, 2, 9, 12, 0, 0);
		tsAdd(tOutd, cal, 9.0f);
		cal.set(2016, 2, 9, 18, 0, 0);
		tsAdd(tOutd, cal, 9.0f);
		cal.set(2016, 2, 10, 0, 0, 0);
		tsAdd(tOutd, cal, 10.0f);
		cal.set(2016, 2, 10, 6, 0, 0);
		tsAdd(tOutd, cal, 10.0f);
		cal.set(2016, 2, 10, 12, 0, 0);
		tsAdd(tOutd, cal, 10.0f);
		cal.set(2016, 2, 10, 18, 0, 0);
		tsAdd(tOutd, cal, 10.0f);
		
		cal.set(2016, 2, 10, 14, 0, 0);
		tsAdd(tSupSetpt, cal, 35.0f);
		tsAdd(tSup, cal, 34.5f);
		tsAdd(tRet, cal, 30.9f);
		tsAdd(expected1, cal, 0.0f);
		tsAdd(expected2, cal, 0.0f);
		tsAdd(expected3, cal, 1.0f);

		cal.set(2016, 2, 10, 14, 1, 0);
		tsAdd(tSupSetpt, cal, 35.0f);
		tsAdd(tSup, cal, 35.5f);
		tsAdd(tRet, cal, 32.1f);
		tsAdd(expected3, cal, 0.0f);
		
		cal.set(2016, 2, 10, 14, 2, 0);
		tsAdd(tSupSetpt, cal, 40.0f);
		tsAdd(tSup, cal, 35.5f);
		tsAdd(tRet, cal, 32.6f);
		tsAdd(expected1, cal, 1.0f);
		
		cal.set(2016, 2, 10, 14, 3, 0);
		tsAdd(tSupSetpt, cal, 40.0f);
		tsAdd(tSup, cal, 35.8f);
		tsAdd(tRet, cal, 32.6f);

		cal.set(2016, 2, 10, 14, 4, 0);
		tsAdd(tSupSetpt, cal, 40.0f);
		tsAdd(tSup, cal, 36.1f);
		tsAdd(tRet, cal, 32.0f);
		tsAdd(expected1, cal, 0.0f);
		tsAdd(expected3, cal, 1.0f);

		cal.set(2016, 2, 10, 14, 5, 0);
		tsAdd(tSupSetpt, cal, 35.0f);
		tsAdd(tSup, cal, 36.1f);
		tsAdd(tRet, cal, 32.5f);

		cal.set(2016, 2, 10, 14, 6, 0);
		tsAdd(tSupSetpt, cal, 35.0f);
		tsAdd(tSup, cal, 34.8f);
		tsAdd(tRet, cal, 32.6f);
		tsAdd(expected3, cal, 0.0f);
		
		cal.set(2016, 2, 10, 14, 7, 0);
		tsAdd(tSupSetpt, cal, 35.0f);
		tsAdd(tSup, cal, 34.8f);
		tsAdd(tRet, cal, 32.6f);

		cal.set(2016, 2, 10, 14, 8, 0);
		tsAdd(tSupSetpt, cal, 35.0f);
		tsAdd(tSup, cal, 31.4f);
		tsAdd(tRet, cal, 32.6f);
		tsAdd(expected1, cal, 1.0f);

		cal.set(2016, 2, 10, 14, 9, 0);
		tsAdd(tSupSetpt, cal, 35.0f);
		tsAdd(tSup, cal, 31.6f);
		tsAdd(tRet, cal, 32.6f);
		tsAdd(expected1, cal, 0.0f);
		
		cal.set(2016, 2, 10, 14, 10, 0);
		tsAdd(tSupSetpt, cal, 35.0f);
		tsAdd(tSup, cal, 31.4f);
		tsAdd(tRet, cal, 32.6f);
		tsAdd(expected1, cal, 1.0f);

		cal.set(2016, 2, 10, 14, 11, 0);
		tsAdd(tSupSetpt, cal, 35.0f);
		tsAdd(tSup, cal, 38.6f);
		tsAdd(tRet, cal, 32.6f);
		tsAdd(expected1, cal, 0.0f);
		tsAdd(expected2, cal, 1.0f);
		tsAdd(expected3, cal, 1.0f);

		cal.set(2016, 2, 10, 14, 12, 0);
		tsAdd(tSupSetpt, cal, 35.0f);
		tsAdd(tSup, cal, 39.1f);
		tsAdd(tRet, cal, 32.6f);

		cal.set(2016, 2, 10, 14, 13, 0);
		tsAdd(tSupSetpt, cal, 35.0f);
		tsAdd(tSup, cal, 38.4f);
		tsAdd(tRet, cal, 32.6f);
		tsAdd(expected2, cal, 0.0f);

		TimeSeries anomaly1 = SupplyTempToLow.compute(tSup, tSupSetpt, Float.MIN_VALUE, 0.1f);
		assertTrue(anomaly1.equalsData(expected1.compute()));

		TimeSeries anomaly2 = SupplyTempToHigh.compute(tSup, tSupSetpt, Float.MIN_VALUE, 0.1f);
		assertTrue(anomaly2.equalsData(expected2));

		TimeSeries anomaly3 = SupplyReturnTempDiff.compute(tSup, tRet, tSupSetpt, Float.MIN_VALUE, 0.1f, tOutd, 4.0f);
		assertTrue(anomaly3.equalsData(expected3));
	}
}
