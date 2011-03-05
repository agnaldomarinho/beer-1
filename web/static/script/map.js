$(document).ready(function(){
    Map.Initialize();
});

var Map = function(){
    return {
        Initialize: function(){
        	map = new OpenLayers.Map('map');
			var base = new OpenLayers.Layer.OSM("Google Streets");
			Map.Bars = new OpenLayers.Layer.Vector(
				"Bar Markers",
				{
					styleMap: new OpenLayers.StyleMap({
						externalGraphic: "/static/script/ol/img/marker.png",
						backgroundXOffset: 0,
						backgroundYOffset: -7,
						pointRadius: 10
					}),
					isBaseLayer: false,
					rendererOptions: { yOrdering: true }
				}
			);
			map.addLayers([base, Map.Bars]);
			Map.Projections = [new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()];
			map.setCenter(Map.ProjectLonLat(-75.16, 39.963), 16);
			
			var dragFeature = new OpenLayers.Control.DragFeature(Map.Bars);
			map.addControl(dragFeature);
			dragFeature.activate();

			dragFeature.onComplete = Map.BarMoved; 
            
			$.ajax({
                url: "/bars/(1.1,1.1)/",
                success: Map.DisplayMarkers,
                dataType: "json"
            });

        },

		BarMoved: function(vector) {
			var loc = Map.ProjectPoint(vector.geometry.x, vector.geometry.y, true);
			$.ajax(sprintf('/moveBar/%s/(%s,%s)/', vector.attributes.id, loc.x, loc.y));
		},

        DisplayMarkers: function(items){
			Map.Bars.removeFeatures(Map.Bars.features);
            for each (var bar in items.bars){
                var feature = new OpenLayers.Feature.Vector(
					Map.ProjectPoint(bar.location[0], bar.location[1]),
					{ id: bar.id }
				);
				Map.Bars.addFeatures(feature);
            }
        },

		ProjectLonLat: function(lon, lat, reverse) {
			return Map.Project(lon, lat, OpenLayers.LonLat, !!reverse);
		},

		ProjectPoint: function(x, y, reverse) {
			return Map.Project(x, y, OpenLayers.Geometry.Point, !!reverse);
		},

		Project: function(x, y, t, reverse) {
			return new t(x, y).transform(
				Map.Projections[+reverse],
				Map.Projections[+!reverse]
			);
		}
    };
}();

String.prototype.format = function() {
	var formatted = this;
	for(arg in arguments) {
		formatted = formatted.replace("{" + arg + "}", arguments[arg]);
	}
	return formatted;
};
						
