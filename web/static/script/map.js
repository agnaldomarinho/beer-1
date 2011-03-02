$(document).ready(function(){
    Map.Initialize();
});

var Map = function(){
    return {
        Initialize: function(){
        	map = new OpenLayers.Map('map');
			var gmap = new OpenLayers.Layer.Google(
				"Google Streets",
				{numZoomLevels: 20}
			);
			Map.Bars = new OpenLayers.Layer.Markers("Bar Markers");
			map.addLayers([gmap, Map.Bars]);
			map.setCenter(Map.Project(-75.16, 39.963), 14);
			var controls = [
				new OpenLayers.Control.DragMarker(Map.Bars)
			];
			for (control in controls){
				map.addControls(control);
			}
            $.ajax({
                url: "/bars/(1.1,1.1)/",
                success: Map.DisplayMarkers,
                dataType: "json"
            });

        },

        DisplayMarkers: function(items){
            for (var bar in items.bars){
                var marker = new OpenLayers.Marker(Map.Project(bar.location[0], bar.location[1]));
				Map.Bars.addMarker(marker);
            }
        },

		Project: function(lon, lat) {
			return new OpenLayers.LonLat(lon, lat).transform(
				new OpenLayers.Projection("EPSG:4326"),
				map.getProjectionObject()
			);

		}
    };
}();
