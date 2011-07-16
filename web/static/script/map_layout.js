$(document).ready(function(){
	MapLayout.Init();
});

var MapLayout = function(){

	return {
		Init: function(){
			$(window).bind('resize', MapLayout.Resize);
			MapLayout.Resize();
		},

		Resize: function(){
			$('.map, .data').height($(window).height() - $('.header').height());
			$('.map').width($(window).width() - ($('.data').width()));
		}
	};

}();
