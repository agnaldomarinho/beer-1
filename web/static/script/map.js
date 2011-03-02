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
			map.setCenter(Map.ProjectLonLat(-75.16, 39.963), 16);
			
			var dragFeature = new OpenLayers.Control.DragFeature(Map.Bars);
			map.addControl(dragFeature);
			dragFeature.activate();
            
			$.ajax({
                url: "/bars/(1.1,1.1)/",
                success: Map.DisplayMarkers,
                dataType: "json"
            });

        },

        DisplayMarkers: function(items){
			Map.Bars.removeFeatures(Map.Bars.features);
            for each (var bar in items.bars){
                var feature = new OpenLayers.Feature.Vector(
					Map.ProjectPoint(bar.location[0], bar.location[1])
				);
				Map.Bars.addFeatures(feature);
            }
        },

		ProjectLonLat: function(lon, lat) {
			return Map.Project(lon, lat, OpenLayers.LonLat);
		},

		ProjectPoint: function(x, y) {
			return Map.Project(x, y, OpenLayers.Geometry.Point);
		},

		Project: function(x, y, t) {
			return new t(x, y).transform(
				new OpenLayers.Projection("EPSG:4326"),
				map.getProjectionObject()
			);
		}
    };
}();
