import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Ayush Bandil on 22/11/2019.
 */
public class XYPlotter extends ApplicationFrame {

    public XYPlotter(final String title, ArrayList<Coordinates> bingCoordinates, ArrayList<Coordinates> osmCoordinates) {
        super(title);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Bing and OSM Path Comparision",
                "Longitude",
                "Latitude",
                createDataset(bingCoordinates, osmCoordinates),
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1200, 1200));
        final XYPlot plot = chart.getXYPlot();
        ValueAxis domainAxis = plot.getDomainAxis();
        ValueAxis rangeAxis = plot.getRangeAxis();

        Double[] domainRange = getDomainRange(bingCoordinates, osmCoordinates);
        Double[] rangeRange = getRangeRange(bingCoordinates, osmCoordinates);
        domainAxis.setRange(domainRange[0] - 0.01d, domainRange[1] + 0.01d);
        rangeAxis.setRange(rangeRange[0] - 0.01d, rangeRange[1] + 0.01d);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.DARK_GRAY);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
        setContentPane(chartPanel);

    }

    private Double[] getDomainRange(ArrayList<Coordinates> bingCoordinates, ArrayList<Coordinates> osmCoordinates) {
        final Double[] domainRange = {Double.MAX_VALUE, -Double.MAX_VALUE};

        ArrayList<Coordinates> temp = (ArrayList<Coordinates>) bingCoordinates.clone();
        temp.addAll(osmCoordinates);

        temp.forEach(point ->{
            if (point.getLon()< domainRange[0]){
                domainRange[0] = Double.valueOf(point.getLon());
            }
            if (point.getLon()> domainRange[1]) {
                domainRange[1] = Double.valueOf(point.getLon());
            }
        });
        return domainRange;
    }

    private Double[] getRangeRange(ArrayList<Coordinates> bingCoordinates, ArrayList<Coordinates> osmCoordinates) {
        final Double[] rangeRange = {Double.MAX_VALUE, -Double.MAX_VALUE};

        ArrayList<Coordinates> temp = (ArrayList<Coordinates>) bingCoordinates.clone();
        temp.addAll(osmCoordinates);

        temp.forEach(point ->{
            if (point.getLat()< rangeRange[0]){
                rangeRange[0] = Double.valueOf(point.getLat());
            }
            if (point.getLat()> rangeRange[1]) {
                rangeRange[1] = Double.valueOf(point.getLat());
            }
        });
        return rangeRange;
    }

    private XYDataset createDataset(ArrayList<Coordinates> bingCoordinates, ArrayList<Coordinates> osmCoordinates) {
        final XYSeries bingPoints = new XYSeries("Bing Path", false);
        bingCoordinates.forEach(point ->
                bingPoints.add(point.getLon(), point.getLat())
        );

        final XYSeries osmPoints = new XYSeries("OSM Path", false);
        osmCoordinates.forEach(point -> osmPoints.add(point.getLon(), point.getLat()));

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(bingPoints);
        dataset.addSeries(osmPoints);
        return dataset;
    }
}
