interface SYRFGeospatialInterface {
    fun configure()
}

object SYRFGeospatial: SYRFGeospatialInterface {
    init {
        System.loadLibrary("main");
    }

    override fun configure() {

    }

    fun test() {
        testGeometry(10F, 10F);

    }

    external fun testGeometry(lat: Float, ltd: Float): Void
}