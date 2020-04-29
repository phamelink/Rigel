package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.function.Function;

/**
 * Function for converting horizontal coordinates to a strereographic projection
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {

    private final HorizontalCoordinates center;
    private final double cosPhi1;
    private final double sinPhi1;

    /**
     * Class constructor returning a stereographic projection centered at "center"
     * @param center
     *      where the stereographic projection is centered
     */
    public StereographicProjection(HorizontalCoordinates center) {
        this.center = center;
        this.cosPhi1 = Math.cos(center.alt());
        this.sinPhi1 = Math.sin(center.alt());
    }

    /**
     * returns the coordinates of the center of the circle according to the projection
     * of the parallel passing through the point "hor"
     * The ordinate of this center can be infinite
     * @param hor
     *          the horizontal coordinates of the point through which the parallel passes
     * @return the coordinates of the center of the circle according to the projection
     *          of the parallel passing through the point "hor"
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor){

        return CartesianCoordinates.of(0,cosPhi1 / (Math.sin(hor.alt()) + sinPhi1));
    }

    /**
     * returns the radius of the circle corresponding to the projection of the parallel passing through coordinates
     * @param parallel horizontal coordinates of parallel
     * @return the radius of the circle corresponding to the projection of the parallel passing through coordinates
     */
    public double circleRadiusForParallel (HorizontalCoordinates parallel){
        return Math.cos(parallel.alt()) / (Math.sin(parallel.alt()) + sinPhi1);
    }

    @Override
    public CartesianCoordinates apply(HorizontalCoordinates horizontalCoordinates) {
        double lam = horizontalCoordinates.az();
        double phi = horizontalCoordinates.alt();
        double lam0 = center.az();
        double lamDelta = lam - lam0;

        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);
        double sinLamDelta = Math.sin(lamDelta);
        double cosLamDelta = Math.cos(lamDelta);

        double d = 1 / (1 + sinPhi * sinPhi1 + cosPhi * cosPhi1 * cosLamDelta);
        double x = d * cosPhi * sinLamDelta;
        double y = d * (sinPhi * cosPhi1 - cosPhi * sinPhi1 * cosLamDelta);

        return CartesianCoordinates.of(x,y);

    }

    /**
     * Returns horizontal coordinates of the point of which the projection is the point
     * of cartesian coordinates xy
     * @param xy
     *          Cartesian coordinates of the projection's point
     * @return horizontal coordinates of the point of which the projection is the point
     *          of cartesian coordinates xy
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy){
        double x = xy.x();
        double y = xy.y();
        double rho = Math.sqrt(x*x + y*y);
        double rho2 = rho * rho;
        double sinC = (2 * rho) / (rho2 + 1);
        double cosC = (1-rho2) / (rho2 + 1);

        double lam = Math.atan2((x * sinC) , (rho * cosPhi1 * cosC - y * sinPhi1 * sinC)) + center.az();

        double phi = Math.asin(cosC * sinPhi1 + (y * sinC * cosPhi1) / rho );


        return HorizontalCoordinates.of(Angle.normalizePositive(lam), phi);

    }

    /**
     * returns the diameter projected by a sphere of angular size rad centered
     * at the center of projection, considering it is on the horizon
     * @param rad
     *          angular size
     * @return the diameter projected by a sphere of angular size rad centered
     *          at the center of projection, considering it is on the horizon
     */
    public double applyToAngle(double rad){
        return 2 * Math.tan(rad / 4);
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "StereographicProjection at " + center.toString();
    }


}
