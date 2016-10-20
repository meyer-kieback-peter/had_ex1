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

package kup.timeseries.function;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import kup.timeseries.DataPoint;
import kup.timeseries.TimeSeries;
import kup.timeseries.TimeSeriesException;

public final class DailyMeanTemp extends FunctionBase implements Function {
	
	public DailyMeanTemp(TimeSeries input) {
		setInput(input);
	}

	private static Date dateWithoutTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    return cal.getTime();
	}
	
	public Vector<TimeSeries> splitDays() {
		Vector<TimeSeries> ret = new Vector<TimeSeries>();
		TimeSeries daySeries = null;
		Date lastDay = null;
		for (int i = 0; i<input.size(); i++) {
			DataPoint dp = input.getDataPoint(i);
			Date day = dateWithoutTime(dp.date());
			if (null == lastDay || !day.equals(lastDay)) {
				lastDay = day;
				daySeries = new TimeSeries(day.toString());
				ret.addElement(daySeries);
			}
			try {
				daySeries.addDataPoint(dp);
			} catch (TimeSeriesException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	public static float mean(TimeSeries dayTs) {
		float ret = 0.0f;
		for (int i = 0; i<dayTs.size(); i++) {
			DataPoint dp = dayTs.getDataPoint(i);
			if (i == 0) {
				ret = dp.value();
			} else {
				ret += dp.value();
			}
		}
		return ret / dayTs.size();
	}
	
	private static long dateDiffMillis(Date d1, Date d2) {
		return d2.getTime() - d1.getTime();
	}
	
	private static float dateDiffHours(Date d1, Date d2) {
		return dateDiffMillis(d1, d2) / (1000 * 3600);
	}
	
	public static float dailyMean(TimeSeries dayTs) {
		if (dayTs.size()>= 24) {
			boolean sampleToHourly = true;
			for (int i = 1; i<dayTs.size(); i++) {
				DataPoint dp1 = dayTs.getDataPoint(i-1);
				DataPoint dp2 = dayTs.getDataPoint(i);
				if (dateDiffHours(dp1.date(), dp2.date()) > 1.5f) {
					sampleToHourly = false;
				}
			}
			if (sampleToHourly) {
				return dailyMeanHourly(dayTs);
			} else {
				return dailyMeanSynoptic(dayTs);
			}
		}
		return dailyMeanSynoptic(dayTs);
	}

	private static float dailyMeanHourly(TimeSeries dayTs) {
		Date date = dateWithoutTime(dayTs.getDataPoint(0).date());
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		float sum = 0.0f;
		for (int i = 0; i<24; i++) {
			cal.set(Calendar.HOUR_OF_DAY, i);
			DataPoint dp = dayTs.interpolate(cal.getTime());
			if (i == 0) {
				sum = dp.value();
			} else {
				sum += dp.value();
			}
		}
		return sum / 24;
	}

	private static float dailyMeanSynoptic(TimeSeries dayTs) {
		Date date = dateWithoutTime(dayTs.getDataPoint(0).date());
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		float sum = 0.0f;
		for (int i = 0; i<24; i+= 6) {
			cal.set(Calendar.HOUR_OF_DAY, i);
			DataPoint dp = dayTs.interpolate(cal.getTime());
			if (i == 0) {
				sum = dp.value();
			} else {
				sum += dp.value();
			}
		}
		return sum / 4;
	}

	public TimeSeries compute(String name) {
		TimeSeries ret = new TimeSeries(name);
		Vector<TimeSeries> days = splitDays();
		for (int i = 0; i<days.size(); i++) {
			TimeSeries day = days.elementAt(i);
			float dm = dailyMean(day);
			Date date = dateWithoutTime(day.getDataPoint(0).date());
			try {
				ret.addDataPoint(new DataPoint(date, dm));
			} catch (TimeSeriesException e) {
				e.printStackTrace();
			}
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			cal.set(Calendar.HOUR, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			try {
				ret.addDataPoint(new DataPoint(cal.getTime(), dm));
			} catch (TimeSeriesException e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
}
