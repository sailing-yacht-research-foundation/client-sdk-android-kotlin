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
        testGeospatial();

    }
    
    private external fun testGeospatial()
}