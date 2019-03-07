<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title>Payara Micro Cluster Rating Live Feed</title>
        <link rel="stylesheet" href="css/payara.css">
        <script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
        <script src="https://code.highcharts.com/highcharts.js"></script>

        <script>
            var chartWidth = 20;

            $(document).ready(function() {
                Highcharts.setOptions({
                    global: {
                        useUTC: false
                    }
                });
                document.chart = new Highcharts.Chart({
                    chart: {
                        renderTo: 'container',
                        defaultSeriesType: 'spline',
                        marginRight: 10,
                        plotShadow: true
                    },
                    title: {
                        text: 'Rating Browser Feed',
                        style: {
                            color: "#FFFFFF",
                            fontSize: "26px"
                        }
                    },
                    xAxis: {
                        title: {
                            text: 'Time',
                            style: {
                                color: "#FFFFFF",
                                fontSize: "20px"
                            }
                        },
                        labels: {
                            style: {
                                color: "#FFFFFF",
                                fontSize: "18px"
                            }
                        },
                        type: 'datetime',
                        tickPixelInterval: 150
                    },
                    yAxis: {
                        min: 0,
                        max: 5,
                        title: {
                            text: 'Score',
                            style: {
                                color: "#FFFFFF",
                                fontSize: "20px"
                            }
                        },
                        labels: {
                            style: {
                                color: "#FFFFFF",
                                fontSize: "18px"
                            }
                        }
                    },
                    tooltip: {
                        formatter: function() {
                            if(this.point.messages){
                                return '<b>Current Score:</b> '+ this.point.y +'<br/>' +
                                       '<b>Opinions:</b><br/>' +
                                       this.point.messages.map(message => '- ' + message.contents + '[' + message.instance + ']' + '<br/>').join('');
                            } else{
                                return '<b>No scores</b>';
                            }
                        }
                    },
                    legend: {
                        enabled: false
                    },
                    exporting: {
                        enabled: false
                    },
                    series: [{
                        name: 'Scores per Time',
                        lineWidth: 2,
                        data: (function() {
                            // generate an array of random data
                            var data = [],
                                time = (new Date()).getTime(),
                                i;
                            for (i = 1 - chartWidth; i <= 0; i++) {
                                data.push({
                                    x: time + i * 1000,
                                    y: 0,
                                    messages: []
                                });
                            }
                            return data;
                        })()
                    }]
                });

                // Function to add data to the graph
                function addRating(rating) {
                    // Round the time of the event to the nearest millisecond
                    var date;
                    if(typeof(rating.time) === "string"){
                        date = new Date(rating.time);                        
                    }else{
                        date = rating.time;
                    }
                    
                    date.setMilliseconds(0);
                    var time = date.getTime();
                    var series = document.chart.series[0];

                    var existingPoint = series.data.find(point => point.options.x === time);
                    if (existingPoint && existingPoint !== null) {
                        existingPoint.options.total += rating.score;
                        existingPoint.options.count++;
                        existingPoint.update(existingPoint.options.total / existingPoint.options.count, false /*redraw*/, true /*animate*/);
                        if(rating.message){
                            existingPoint.options.messages.push({contents: rating.message, instance: rating.instance});
                        }                        
                    } else {
                        var messages = [];
                        if(rating.message){
                            messages.push({contents: rating.message, instance: rating.instance});
                        }
                        series.addPoint({x: time, y: rating.score, messages: messages, total: rating.score, count: 1}, true /*redraw*/, true /*shift*/, true /*animate*/);
                    }
                }

                // Start websocket
                var wsUri = "ws://" + location.host + "${pageContext.request.contextPath}/data";
                console.log("Connected from Server WebSocket");
                websocket = new WebSocket(wsUri); 
                websocket.onopen = function(event) { }; 
                websocket.onclose = function(event) { }; 
                websocket.onerror = function(event) { };   
                websocket.onmessage = function(event) {
                    addRating(JSON.parse(event.data));
                };
                setInterval(()=>{addRating({time : new Date(), score : 0});}, 1000);
            });
        </script>
    </head>
    <body>
        <div id="container"></div>
        <div id="copyright">
            <img id="payara-logo" src="images/payara-logo-orange.png" />
            <p>Powered by Payara Micro 5.184</p>
        </div>
    </body>
</html>