////////////////////////////////////////////////////////////////////////////////////////////////////
// Copyright 2011 Astina AG, Zurich
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
////////////////////////////////////////////////////////////////////////////////////////////////////
package ch.astina.hesperid.web.components;

import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@Import(stylesheet = {"context:styles/observerchart.css"})
public class ObserverChart {

    private static final int MAX_NUM = 50;
    @Parameter
    @Property
    private Observer observer;
    @Property
    private ObserverParameter observerParameter;
    @Inject
    private ObserverDAO observerDAO;
    @Property
    @Persist
    private String chart;
    @Property
    @Persist
    private Boolean displayMinMax;
    private static final String CHART_LINECHART = "lineChart";
    private static final String CHART_ANNOTATEDTIMELINE = "annotatedTimeLine";
    private int row = 0;
    private int col = 0;
    private int colCount;
    private SimpleDateFormat dateFormatPart1;
    private SimpleDateFormat dateFormatPart2;
    private static String COLOR_MIN = "#4582ec";
    private static String COLOR_MAX = "#da3811";
    private static String COLOR_VALUE = "#fd9700";

    boolean setupRender()
    {
        if (observer.getExpectedValueMax() != null && observer.getExpectedValueMin() != null) {
            colCount = 4;
        } else if (observer.getExpectedValueMax() != null || observer.getExpectedValueMin() != null) {
            colCount = 3;
        } else {
            colCount = 2;
        }

        if (chart == null) {
            chart = CHART_ANNOTATEDTIMELINE;
        }
        if (displayMinMax == null) {
            displayMinMax = true;
        }

        dateFormatPart1 = new SimpleDateFormat("yyyy, ");
        dateFormatPart2 = new SimpleDateFormat(", dd, HH, mm, ss, SSS");

        return observer.getObserverStrategy().getResultType().isNumeric();
    }

    public List<ObserverParameter> getObserverParameters()
    {
        List<ObserverParameter> parameters = observerDAO.getObserverParameters(observer, MAX_NUM);
        Collections.reverse(parameters);
        return parameters;
    }

    public String getChartId()
    {
        return String.format("observer-chart-%d", observer.getId());
    }

    public int getRowCount()
    {
        return getObserverParameters().size();
    }

    public int getRow()
    {
        return row++ / 4;
    }

    public int getCol()
    {
        return col++ % colCount;
    }

    public boolean isExpectedValueMin()
    {
        return displayMinMax && observer.getExpectedValueMin() != null;
    }

    public boolean isExpectedValueMax()
    {
        return displayMinMax && observer.getExpectedValueMax() != null;
    }

    public float getParameterValue()
    {
        try {
            return Float.parseFloat(observerParameter.getValue());
        } catch (Exception e) {
            return 0.0f;
        }
    }

    public String getNewJsDate()
    {
        Date date = observerParameter.getUpdated();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String dateString = dateFormatPart1.format(date) + calendar.get(Calendar.MONTH)
                + dateFormatPart2.format(date);
        return String.format("new Date(%s)", dateString);
    }

    public String getColors()
    {
        if (isExpectedValueMin() && isExpectedValueMax()) {
            return String.format("['%s', '%s', '%s']", COLOR_MIN, COLOR_MAX, COLOR_VALUE);
        }
        if (isExpectedValueMin() || isExpectedValueMax()) {
            return String.format("['%s', '%s']", COLOR_MAX, COLOR_VALUE);
        }
        return String.format("['%s']", COLOR_VALUE);
    }

    public boolean isAnnotatedTimeLine()
    {
        return chart.equals(CHART_ANNOTATEDTIMELINE);
    }

    public boolean isLineChart()
    {
        return chart.equals(CHART_LINECHART);
    }

    public String[] getAvailableCharts()
    {
        String[] charts = {CHART_ANNOTATEDTIMELINE, CHART_LINECHART};
        return charts;
    }
}
