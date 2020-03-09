package ch.epfl.rigel.coordinates;

import java.util.function.Function;

public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {

    private final HorizontalCoordinates center;
    private final double cosPhi1;
    private final double sinPhi1;

    public StereographicProjection(HorizontalCoordinates center) {
        this.center = center;
        this.cosPhi1 = Math.cos(center.alt());
        this.sinPhi1 = Math.sin(center.az());
    }

    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor){

        return CartesianCoordinates.of(0,circleRadiusForParallel(hor));
    }

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

    public HorizontalCoordinates inverseApply(CartesianCoordinates xy){
        double x = xy.x();
        double y = xy.y();
        double rho = Math.sqrt(x*x + y*y);
        double rho2 = rho * rho;
        double sinC = (2 * rho) / (rho2 + 1);
        double cosC = (1-rho2) / (rho2 + 1);

        double lam = (x * sinC) / (rho * cosPhi1 * cosC - y * sinPhi1 * sinC) + center.az();

        double phi = Math.asin(cosC * sinPhi1) + (y * sinC * cosPhi1) / rho ;

        //TODO Verify case where rho = 0 !

        return HorizontalCoordinates.of(lam, phi);

    }

    public double applyToAngle(double rad){
        return 2 * Math.tan(rad / 4);
    }

}
