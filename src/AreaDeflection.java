/**
 * Created by Ayush Bandil on 23/1/2020.
 */
public class AreaDeflection {
    private Area area;
    private Integer rangeUpper;
    private Integer rangeLower;
    private Double avgDeflection;
    private Integer noOfPoints;

    public AreaDeflection(Area area, Integer rangeUpper, Integer rangeLower, Double avgDeflection, Integer noOfPoints) {
        this.area = area;
        this.rangeUpper = rangeUpper;
        this.rangeLower = rangeUpper - 5; // can change later
        this.avgDeflection = avgDeflection;
        this.noOfPoints = noOfPoints;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Integer getRangeUpper() {
        return rangeUpper;
    }

    public void setRangeUpper(Integer rangeUpper) {
        this.rangeUpper = rangeUpper;
    }

    public Integer getRangeLower() {
        return rangeLower;
    }

    public void setRangeLower(Integer rangeLower) {
        this.rangeLower = rangeLower;
    }

    public Double getAvgDeflection() {
        return avgDeflection;
    }

    public void setAvgDeflection(Double avgDeflection) {
        this.avgDeflection = avgDeflection;
    }

    public Integer getNoOfPoints() {
        return noOfPoints;
    }

    public void setNoOfPoints(Integer noOfPoints) {
        this.noOfPoints = noOfPoints;
    }
}
