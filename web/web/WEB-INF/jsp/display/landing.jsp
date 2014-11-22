<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hello</title>

    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" />

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
    <script src="http://code.highcharts.com/highcharts.js"></script>
    <script src="http://code.highcharts.com/modules/data.js"></script>
    <script src="http://code.highcharts.com/modules/exporting.js"></script>

    <!-- Additional files for the Highslide popup effect -->
    <script type="text/javascript" src="http://www.highcharts.com/media/com_demo/highslide-full.min.js"></script>
    <script type="text/javascript" src="http://www.highcharts.com/media/com_demo/highslide.config.js" charset="utf-8"></script>
    <link rel="stylesheet" type="text/css" href="http://www.highcharts.com/media/com_demo/highslide.css" />

    <script>
        $(function () {
            var chart;
            $(document).ready(function() {
                var options = {
                    chart: {
                        renderTo: 'container',
                        type: 'line'
                    },
                    title: {
                        text: 'Monthly Average Temperature'
                    },
                    subtitle: {
                        text: 'Source: WorldClimate.com'
                    },
                    xAxis: {
                        categories: []
                    },
                    yAxis: {
                        title: {
                            text: 'Temperature (°C)'
                        }
                    },
                    plotOptions: {
                        line: {
                            dataLabels: {
                                enabled: true
                            },
                            enableMouseTracking: false
                        }
                    },
                    series: []
                };

                $.getJSON('${pageContext. request. contextPath}/display/loadStats.json', function(json) {
                    $.map(json, function(value, key) {
                        $.map(value, function(count, day) {
                            if(day !== 'dates') {
                                var series = {
                                    name: '',
                                    data: []
                                };
                                series.name = day;
                                series.data.push(count);
                                options.series.push(series);
                            } else {
                                options.xAxis.categories.push(count);
                            }
                        });
                    });

                    // Create the chart
                    chart = new Highcharts.Chart(options);
                });
            });
        });
    </script>
</head>
<body>
    Hello
    Pending: ${pending}
    Processed: ${processedToday}
    <div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

</body>
</html>
