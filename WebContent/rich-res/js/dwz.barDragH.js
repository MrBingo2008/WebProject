/**
 * @author Roger Wu 
 * @version 1.0
 */
(function(){
	$.fn.cssv = function(pre){
		var cssPre = $(this).css(pre);
		return cssPre.substring(0, cssPre.indexOf("px")) * 1;
	};
	$.fn.jBarH = function(options){
		var op = $.extend({container:"#jbsxBox", gridScroller:"#gridScroller", collapse:".collapse", toggleBut:".sidebarHeaderContent a", sideBar:"#sidebarH", sideBar2:"#sidebarH_s", splitBar:"#splitBarH", splitBar2:"#splitBarHProxy"}, options);

		var jbar = this;
		var sbar = $(op.sideBar2, jbar);
		var bar = $(op.sideBar, jbar);
		var barBut = $(op.toggleBut, bar);
			
		var container = $(op.container);
		var gridScroller = $(op.gridScroller);
			
		barBut.click(function(){
			
			var display =bar.css('display');
			if(display == 'none'){
   				return false;
			}
			bar.css('display','none'); 
			
			var tempBarHeight = bar.outerHeight();
			var cheight = $(op.container).outerHeight();
			cheight = cheight + tempBarHeight;
			
			var theight = $(op.gridScroller).outerHeight();
			theight = theight + tempBarHeight;
			
			//var cLayoutH = container.attr("layoutH");
		    //container.attr("layoutH", cLayoutH-tempBarHeight);
		    //cLayoutH = parseInt(container.attr("layoutH"));
		    
		    var gLayoutH = parseInt(gridScroller.attr("layoutH"));
		    gridScroller.attr("layoutH", gLayoutH-tempBarHeight);
		    gLayoutH = parseInt(gridScroller.attr("layoutH"));
		    
			bar.animate({height: "0px"}, 50, function(){
				//bar.hide();
				$(op.container).animate({height: cheight},50);
				$(op.gridScroller).animate({height: theight}, 50);
			});
			return false;
		});
		
		/*
	    $("#test4").click(function(){
				var tempBarHeight = 100;
				var cheight = $(op.container).outerHeight();
				cheight = cheight - tempBarHeight;
				
				var theight = $(op.gridScroller).outerHeight();
				theight = theight - tempBarHeight;
					
					//container.attr("layoutH", parseInt(container.attr("layoutH"))-200);
					//container.layoutH();
				bar.animate({height: "100px"}, 50, function(){
					bar.show();
					$(op.container).animate({height: cheight},50);
					$(op.gridScroller).animate({height: theight}, 50);
				});
				return false;
			});*/
		//});
	}
})(jQuery);

function openBatchDetail(){
	var container = $("#jbsxBox");
	var gridScroller = $("#gridScroller");
	var bar = $("#sidebarH");
	
	var display =bar.css('display');
	if(display != 'none'){
			return false;
	}
	
	bar.css('display','block'); 
	
	var tempBarHeight = 180;
	var cheight = container.outerHeight();
	cheight = cheight - tempBarHeight;
			
	var theight = gridScroller.outerHeight();
	theight = theight - tempBarHeight;
				
	container.attr("layoutH", parseInt(container.attr("layoutH")) + tempBarHeight);
	gridScroller.attr("layoutH", parseInt(gridScroller.attr("layoutH")) + tempBarHeight);
	
	
	bar.animate({height: "180px"}, 50, function(){
		//bar.show();
		container.animate({height: cheight},50);
		gridScroller.animate({height: theight}, 50);
	});
	
	setTimeout(function() {
		$(".sidebarHeader").css("padding", "0px");
		$("#sidebarContentH").css("padding", "0px");}, 20);
	return true;
}

function closeH(){
	var bar = $("#sidebarH");
	if(bar.css("display") != "none"){
		var tempBarHeight = bar.outerHeight();
		var container = $("#jbsxBox");
        //var gridScroller = $("#gridScroller");
		
        var cheight = container.outerHeight();
		cheight = cheight + tempBarHeight;
		container.css("height", cheight+"px");
		/*
		var theight = gridScroller.outerHeight();
		theight = theight + tempBarHeight;
		gridScroller.css("height", theight+"px");
		
	    var gLayoutH = parseInt(gridScroller.attr("layoutH"));
	    gridScroller.attr("layoutH", gLayoutH-tempBarHeight);
	    gLayoutH = parseInt(gridScroller.attr("layoutH"));
		*/
		$("#sidebarH").css("height", "0px");
		$("#sidebarH").css("display", "none");
	}
}