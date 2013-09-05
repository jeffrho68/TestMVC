//Script for the home page
var Home = function ($) {
	"use strict";
	var init;
	
	init = function () {
		//About modal dialog
		$('#aboutModal').modal({
			keyboard: true,
			show: false
		});

		//about navbar click handler
		$('#about').click(function () {
			$('#aboutModal').modal('show');
			//Close the navbar dropdown menu (shown if on a small screen)
			$('button.navbar-toggle').click();
		});
		
    };
	
	//Public interface
	return {
		init: init
	};
	
}(jQuery);
$(Home.init());