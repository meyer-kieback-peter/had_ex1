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

package kup.anomalies;

import kup.timeseries.CrossSampleException;
import kup.timeseries.TimeSeries;
import kup.timeseries.function.Abs;
import kup.timeseries.function.BackDiff;
import kup.timeseries.function.DailyMeanTemp;
import kup.timeseries.function.GreaterOrEqual;
import kup.timeseries.function.InterpolateAt;
import kup.timeseries.function.K;
import kup.timeseries.function.LessThanFix;
import kup.timeseries.function.Mult;
import kup.timeseries.function.Sub;
import kup.timeseries.function.T1;
import kup.timeseries.function.Taper;

/**
 * HVAC anomaly: difference between supply temperature and return temperature above limits - energy deficit situation or plant problems.
 * 
 * This anomaly is detected, if the difference between supply temperature and return temperature
 * exceeds supply temperature setpoint * (1.0 +/- limit) and
 * no significant daily mean temperature change has been detected 
 *
 * parameters:
 * tSup - supply temperature
 * tRet - return temperature
 * tsupSetpt - supply temperature setpoint
 * t1 - low-pass filter coefficient for tSup and tRet useful default is 1800s
 * limit - detection limit for anomaly situation 0 .. 1 (0 .. 100% ), useful default is 0.25 (difference 25% off setpoint) 
 * tOutd - outdoor temperature
 * dailyMeanLimit - limit for significant daily mean temperature change, useful default is 3K
 */
public class SupplyReturnTempDiff {
	
	public static TimeSeries compute(TimeSeries tSup, TimeSeries tRet, TimeSeries tSupSetpt, float t1, float limit, TimeSeries tOutd, float dailyMeanLimit) throws CrossSampleException {
		if (t1 <= Float.MIN_NORMAL) t1 = Float.MIN_NORMAL;
		if (limit <= 0.0f) limit = 0.0f;
		if (limit >= 1.0f) limit = 1.0f;
		
		T1 tSupT1 = new T1(tSup, t1);
		T1 tRetT1 = new T1(tRet, t1);
		Sub diff = new Sub(tSupT1.compute(), tRetT1.compute());
		Abs absDiff = new Abs(diff.compute()); 
		K maxDiff = new K(tSupSetpt.compute(), limit);
		GreaterOrEqual tDiffGeLimit = new GreaterOrEqual(absDiff.compute(), maxDiff.compute());
		TimeSeries tDiffGeLimitTs = tDiffGeLimit.compute(); // tDiffGeLimitTs needed twice
		//System.out.print(new Taper(tDiffGeLimitTs, 0.5f).compute());
		DailyMeanTemp dmt = new DailyMeanTemp(tOutd);
		BackDiff dmtBd = new BackDiff(dmt.compute(), BackDiff.MSECS_PER_DAY);
		Abs dmtBda = new Abs(dmtBd.compute());
		InterpolateAt dmtBdas = new InterpolateAt(dmtBda.compute(), tDiffGeLimitTs); 
		LessThanFix dmtBdsLtLimit = new LessThanFix(dmtBdas.compute(), dailyMeanLimit);
		//System.out.print(new Taper(dmtBdsLtLimit.compute(), 0.5f).compute());		
		Mult anomalyUntapered = new Mult(tDiffGeLimitTs, dmtBdsLtLimit.compute());
		Taper anomaly = new Taper(anomalyUntapered.compute(), 0.5f);
		return anomaly.compute();
	}
}
