import android.app.Activity

interface SYRFGeospatialInterface {
    fun configure()
}

object SYRFGeospatial: SYRFGeospatialInterface {

    override fun configure() {
        System.loadLibrary("geos");
    }

    fun test() {
        selfDivide
    }
}