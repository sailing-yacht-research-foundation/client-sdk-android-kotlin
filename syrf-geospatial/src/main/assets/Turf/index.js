const turf = require('@turf/turf');

export class Geometry {

    static point(lat, lon) {
	    const result = turf.point([lat, lon]);
	    return JSON.stringify(result);
    }
    
    static greatCircle(start, end, options) {
	    const result = turf.greatCircle(start, end, options);
	    return JSON.stringify(result);
    }    
    
    static midpoint(pointFirst, pointSecond) {
	    const result = turf.midpoint(pointFirst, pointSecond);
	    return JSON.stringify(result);
    }
    
    static lineString(coordinates, options) {
	    const result = turf.lineString(coordinates, options);
	    return JSON.stringify(result);
    }
    
    static distance(pointFirst, pointSecond, options) {
	    const result = turf.distance(pointFirst, pointSecond, options);
	    return result;
    }
    
    static lineIntersect(lineFirst, lineSecond) {
	    const result = turf.lineIntersect(lineFirst, lineSecond);
	    return JSON.stringify(result);
    }
    
    static simplify(json, options) {
	    const result = turf.simplify(json, options);
	    return JSON.stringify(result);
    }
    
    static pointToLineDistance(point, line) {
	    const result = turf.pointToLineDistance(point, line);
	    return result;
    }
}
