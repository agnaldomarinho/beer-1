/**
 * Created by IntelliJ IDEA.
 * User: mmclarnon
 * Date: Nov 7, 2010
 * Time: 7:26:10 PM
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(function(){
    Map.Initialize();
});

var Map = function(){
    return {
        Initialize: function(){
            var latlng = new google.maps.LatLng(39.963, -75.16);
            var opts = {
                zoom: 15,
                center: latlng,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };
            Map.map = new google.maps.Map(document.getElementById("map"), opts);

            $.ajax({
                url: "/bars/(1.1,1.1)/",
                success: Map.Display,
                dataType: "json"
            });
        },

        Display: function(items){
            for each (var bar in items.bars){
                var marker = new google.maps.Marker({
                    position: new google.maps.LatLng(bar.location[1], bar.location[0]),
                    map: Map.map,
                    title: bar.name
                })
            }
        },

        SampleData: {
            bars:[{
                "name": "Prohibition Taproom",
                "location": [-75.159, 39.961],
                "beers": [{
                    "brewery": "Dock Street Brewery",
                    "name": "Bubbly Whit"
                }]
            }, {
                "name": "The Institute",
                "location": [-75.157, 39.963],
                "beers": []
            }]
        }
    };
}();