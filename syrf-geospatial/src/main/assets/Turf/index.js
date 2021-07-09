const turf = require('@turf/turf');

export class Geometry {

    static point(coords) {
	let result = turf.point(coords);
	return result;
    }
    
    static greatCircle(pointFirst, pointSecond, options) {
	let result = turf.greatCircle(pointFirst, pointSecond, options);
	return result;
    }    
    
    static midpoint(pointFirst, pointSecond) {
	let result = turf.midpoint(pointFirst, pointSecond);
	return result;
    }
    
    static lineString(lines, options) {
	let result = turf.lineString(lines, options);
	return result;
    }
    
    static distance(pointFirst, pointSecond, options) {
	let result = turf.distance(pointFirst, pointSecond, options);
	return result;
    }
    
    static lineIntersect(lineFirst, lineSecond) {
	let result = turf.lineIntersect(lineFirst, lineSecond);
	return result;
    }
    
    static simplify(json, options) {
	let result = turf.simplify(json, options);
	return result;
    }
    
    static pointToLineDistance(point, line) {
	let result = turf.pointToLineDistance(point, line);
	return result;
    }
}
