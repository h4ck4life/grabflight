
var departurePrice = [];
var returnPrice = [];
var departTripDateTimeline = [];
var returnTripDateTimeline = [];

departurePrice.push('Depart');
returnPrice.push('Return');
departTripDateTimeline.push('x');
returnTripDateTimeline.push('x');

$.ajax({
  type: "GET",
  //url: "/KUL/MEL/2016-10-01/2016-10-31/airasia.json",
  url: getDestinationsByGetUrl,
  username: 'Tc7Za7YcTsQNDQqZ',
  password: 'N8Zyj3etybscu6Wv',
  dataType: "json",
  success: function (response) {
	  
	if(response.schedules.length < 1) {
		$('#preload-view').hide();
		$('#preload-noflight').show();
		return false;
	}
	  
    for (var index = 0; index < response.schedules.length; index++) {
    	  var element = response.schedules[index];
    	  // get departure price
    	  if(index === 0) {
    		  for (var i = 0; i < element.departure.length; i++) {
    			  departurePrice.push(element.departure[i].price.replace(/[^0-9\.]/g, ''));
        		  departTripDateTimeline.push(element.departure[i].date);
    		  }
    	  }
    	  // get return price
    	  if(index === 1) {
    		  //console.log('Return length: ' + element.return.length);
    		  for (var i = 0; i < element.return.length; i++) {
    			  returnPrice.push(element.return[i].price.replace(/[^0-9\.]/g, ''));
    			  returnTripDateTimeline.push(element.return[i].date);
    		  }
    	  }
	}
    
    $('#preload-view').hide();
    $('.panel-chart').fadeIn('5000');
    
    // ##### DEPARTURE #####
    var chartDepart = c3.generate({
    	bindto: '#chart-depart',
    	color: {
            pattern: ['#1CA33B']
        },
        legend: {
            show: false
        },
        grid: {
            x: {
                lines: [{value: gDateFrom}]
            }
        },
        transition: {
            duration: 1000
        },
    	data: {
            x: 'x',
            //xFormat: '%Y%m%d', // 'xFormat' can be used as custom format of 'x'
            columns: [
            	departTripDateTimeline,
                departurePrice
            ],
            type: 'spline',
            onclick: function (d, element) {
            	//console.log(d);
            },
            selection: {
                enabled: true
            }
        },
        tooltip: {
            format: {
                value: function(value) {
                    return 'RM ' + value.toFixed(2);
                },
                title: function (tripDate) { 
                	var formatDate = moment(tripDate, "YYYY-MM-DD");
                	return formatDate.format('DD MMM YYYY');
            	}
            }  
        },
        axis: {
            y: {
                label: {
                    text: 'Ticket Price',
                    position: 'outer-center'
                },
                tick : {
                    format: function (d) { return  "RM " + d ; }
                }
            },
            x: {
                label: {
                    text: response.locationFrom + ' - ' + response.LocationTo,
                    position: 'inner-right'
                },
                type: 'timeseries',
                tick: {
                	culling: false,
                	fit: true,
                	rotate: 55,
                    //format: '%Y-%m-%d'
                	format: function (tripDate) { 
                    	var formatDate = moment(tripDate, "YYYY-MM-DD");
                    	return formatDate.format('DD MMM YYYY');
                	}
                }
            }
        }
    });
    
    
    // ##### RETURN #####
    var chartReturn = c3.generate({
    	bindto: '#chart-return',
    	legend: {
            show: false
        },
        grid: {
            x: {
                lines: [{value: gDateTo}]
            }
        },
        transition: {
            duration: 1000
        },
    	data: {
            x: 'x',
            //xFormat: '%Y%m%d', // 'xFormat' can be used as custom format of 'x'
            columns: [
            	returnTripDateTimeline,
            	returnPrice
            ],
            type: 'spline',
            onclick: function (d, element) {
            	//console.log(d);
            },
            selection: {
                enabled: true
            }
        },
        tooltip: {
            format: {
                value: function(value) {
                    return 'RM ' + value.toFixed(2);
                },
                title: function (tripDate) { 
                	var formatDate = moment(tripDate, "YYYY-MM-DD");
                	return formatDate.format('DD MMM YYYY');
            	}
            }
        },
        axis: {
            y: {
                label: {
                    text: 'Ticket Price',
                    position: 'outer-center'
                },
                tick : {
                    format: function (d) { return  "RM " + d ; }
                }
            },
            x: {
                label: {
                    text: response.LocationTo + ' - ' + response.locationFrom,
                    position: 'inner-right'
                },
                type: 'timeseries',
                tick: {
                	culling: false,
                	fit: true,
                	rotate: 55,
                    //format: '%Y-%m-%d'
                	format: function (tripDate) { 
                    	var formatDate = moment(tripDate, "YYYY-MM-DD");
                    	return formatDate.format('DD MMM YYYY');
                	}
                }
            }
        }
    });
    
  },
  error: function(xhr, textStatus, errorThrown){
	  $('#preload-view').hide();
      //alert('Oops, something is wrong. We are sorry. Please try again..');
	  $('#preload-error').show();
  }
});

