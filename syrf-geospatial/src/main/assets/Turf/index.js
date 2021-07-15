const turf = require('@turf/turf');

export class Geometry {

    static add(a, b) {
        return a + b;
    }

    static round(number) {
        return turf.round(number);
    }

    static point(lat, lon) {
	    const result = turf.point([lat, lon]);
	    return JSON.stringify(result);
    }
    
    static greatCircle(pointFirst, pointSecond, options) {
	    const result = turf.greatCircle(pointFirst, pointSecond, options);
	    return result;
    }    
    
    static midpoint(pointFirst, pointSecond) {
	    const result = turf.midpoint(pointFirst, pointSecond);
	    return result;
    }
    
    static lineString(lines, options) {
	    const result = turf.lineString(lines, options);
	    return result;
    }
    
    static distance(pointFirst, pointSecond, options) {
	    const result = turf.distance(pointFirst, pointSecond, options);
	    return result;
    }
    
    static lineIntersect(lineFirst, lineSecond) {
	    const result = turf.lineIntersect(lineFirst, lineSecond);
	    return result;
    }
    
    static simplify(json, options) {
	    const result = turf.simplify(json, options);
	    return result;
    }
    
    static pointToLineDistance(point, line) {
	    const result = turf.pointToLineDistance(point, line);
	    return result;
    }
}
